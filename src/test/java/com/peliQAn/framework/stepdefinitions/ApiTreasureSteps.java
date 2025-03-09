package com.peliQAn.framework.stepdefinitions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peliQAn.framework.api.AuthApiClient;
import com.peliQAn.framework.api.TreasureApiClient;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Step definitions for Treasure API tests
 */
@Slf4j
public class ApiTreasureSteps {

    private final TreasureApiClient treasureApiClient = new TreasureApiClient();
    private final AuthApiClient authApiClient = new AuthApiClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private Response response;
    private String authToken;
    private Map<String, Object> createdTreasure;
    private long createdTreasureId;

    @Given("I am registered and logged in as a user")
    public void iAmRegisteredAndLoggedInAsAUser() {
        String username = "testuser_" + System.currentTimeMillis();
        String email = "testuser_" + System.currentTimeMillis() + "@example.com";
        String password = "Password123";
        
        authToken = authApiClient.registerAndLogin(username, email, password);
        treasureApiClient.setAuthToken(authToken);
        
        Assert.assertNotNull(authToken, "Auth token should not be null");
        log.info("User registered and logged in with token: {}", authToken);
    }

    @Given("there is a treasure with the following details:")
    public void thereIsATreasureWithTheFollowingDetails(DataTable dataTable) {
        Map<String, String> treasureData = dataTable.asMap(String.class, String.class);
        
        Map<String, Object> treasureMap = new HashMap<>();
        treasureMap.put("name", treasureData.get("name"));
        treasureMap.put("value", Integer.parseInt(treasureData.get("value")));
        treasureMap.put("description", treasureData.get("description"));
        treasureMap.put("location", treasureData.get("location"));
        treasureMap.put("discovered", Boolean.parseBoolean(treasureData.get("discovered")));
        
        response = treasureApiClient.createTreasure(treasureMap);
        Assert.assertEquals(response.getStatusCode(), 201, "Create treasure should return 201");
        
        createdTreasure = response.as(Map.class);
        createdTreasureId = Long.parseLong(createdTreasure.get("id").toString());
        
        log.info("Created treasure with ID: {}", createdTreasureId);
    }

    @Given("there are treasures with names:")
    public void thereAreTreasuresWithNames(List<String> names) {
        for (String name : names) {
            Map<String, Object> treasureMap = new HashMap<>();
            treasureMap.put("name", name);
            treasureMap.put("value", 100);
            treasureMap.put("description", "Test treasure: " + name);
            treasureMap.put("location", "Test Island");
            treasureMap.put("discovered", false);
            
            response = treasureApiClient.createTreasure(treasureMap);
            Assert.assertEquals(response.getStatusCode(), 201, "Create treasure should return 201");
        }
        log.info("Created {} treasures with specified names", names.size());
    }

    @Given("there are treasures with discovered status:")
    public void thereAreTreasuresWithDiscoveredStatus(DataTable dataTable) {
        List<Map<String, String>> treasures = dataTable.asMaps();
        
        for (Map<String, String> treasure : treasures) {
            String name = treasure.get(0);
            boolean discovered = Boolean.parseBoolean(treasure.get(1));
            
            Map<String, Object> treasureMap = new HashMap<>();
            treasureMap.put("name", name);
            treasureMap.put("value", 100);
            treasureMap.put("description", "Test treasure: " + name);
            treasureMap.put("location", "Test Island");
            treasureMap.put("discovered", discovered);
            
            response = treasureApiClient.createTreasure(treasureMap);
            Assert.assertEquals(response.getStatusCode(), 201, "Create treasure should return 201");
        }
        log.info("Created treasures with specified discovered status");
    }

    @When("I send a GET request to {string}")
    public void iSendAGETRequestTo(String endpoint) {
        endpoint = endpoint.startsWith("/") ? endpoint : "/" + endpoint;
        response = treasureApiClient.getAllTreasures();
        log.info("Sent GET request to {}", endpoint);
    }

    @When("I send a GET request to {string} with the treasure id")
    public void iSendAGETRequestToWithTheTreasureId(String endpoint) {
        endpoint = endpoint.replace("{id}", String.valueOf(createdTreasureId));
        endpoint = endpoint.startsWith("/") ? endpoint : "/" + endpoint;
        
        response = treasureApiClient.getTreasureById(createdTreasureId);
        log.info("Sent GET request to {} with treasure ID: {}", endpoint, createdTreasureId);
    }

    @When("I send a POST request to {string} with body:")
    public void iSendAPOSTRequestToWithBody(String endpoint, String body) {
        endpoint = endpoint.startsWith("/") ? endpoint : "/" + endpoint;
        
        try {
            Map<String, Object> requestBody = objectMapper.readValue(body, new TypeReference<>() {});
            response = treasureApiClient.createTreasure(requestBody);
            log.info("Sent POST request to {} with body: {}", endpoint, body);
        } catch (Exception e) {
            log.error("Error parsing request body", e);
            throw new RuntimeException("Error parsing request body", e);
        }
    }

    @When("I send a PUT request to {string} with the treasure id and body:")
    public void iSendAPUTRequestToWithTheTreasureIdAndBody(String endpoint, String body) {
        endpoint = endpoint.replace("{id}", String.valueOf(createdTreasureId));
        endpoint = endpoint.startsWith("/") ? endpoint : "/" + endpoint;
        
        try {
            Map<String, Object> requestBody = objectMapper.readValue(body, new TypeReference<>() {});
            response = treasureApiClient.updateTreasure(createdTreasureId, requestBody);
            log.info("Sent PUT request to {} with treasure ID: {} and body: {}", 
                    endpoint, createdTreasureId, body);
        } catch (Exception e) {
            log.error("Error parsing request body", e);
            throw new RuntimeException("Error parsing request body", e);
        }
    }

    @When("I send a DELETE request to {string} with the treasure id")
    public void iSendADELETERequestToWithTheTreasureId(String endpoint) {
        endpoint = endpoint.replace("{id}", String.valueOf(createdTreasureId));
        endpoint = endpoint.startsWith("/") ? endpoint : "/" + endpoint;
        
        response = treasureApiClient.deleteTreasure(createdTreasureId);
        log.info("Sent DELETE request to {} with treasure ID: {}", endpoint, createdTreasureId);
    }

    @When("I send a GET request to {string} with query parameter {string} set to {string}")
    public void iSendAGETRequestToWithQueryParameterSetTo(String endpoint, String paramName, String paramValue) {
        endpoint = endpoint.startsWith("/") ? endpoint : "/" + endpoint;
        
        if (paramName.equals("name")) {
            response = treasureApiClient.searchTreasuresByName(paramValue);
        } else if (paramName.equals("discovered")) {
            if (paramValue.equals("true")) {
                response = treasureApiClient.getDiscoveredTreasures();
            } else {
                response = treasureApiClient.getUndiscoveredTreasures();
            }
        } else {
            throw new IllegalArgumentException("Unsupported parameter: " + paramName);
        }
        
        log.info("Sent GET request to {} with query parameter {}={}", endpoint, paramName, paramValue);
    }

    @Then("I should receive a {int} status code")
    public void iShouldReceiveAStatusCode(int statusCode) {
        Assert.assertEquals(response.getStatusCode(), statusCode, 
                "Response status code should be " + statusCode);
        log.info("Received status code: {}", statusCode);
    }

    @And("the response should be a list of treasures")
    public void theResponseShouldBeAListOfTreasures() {
        List<Map<String, Object>> treasures = response.jsonPath().getList("");
        Assert.assertNotNull(treasures, "Response should be a list");
        Assert.assertFalse(treasures.isEmpty(), "Treasure list should not be empty");
        log.info("Response contains {} treasures", treasures.size());
    }

    @And("each treasure should have id, name, value, description, location and discovered fields")
    public void eachTreasureShouldHaveIdNameValueDescriptionLocationAndDiscoveredFields() {
        List<Map<String, Object>> treasures = response.jsonPath().getList("");
        
        for (Map<String, Object> treasure : treasures) {
            Assert.assertTrue(treasure.containsKey("id"), "Treasure should have id field");
            Assert.assertTrue(treasure.containsKey("name"), "Treasure should have name field");
            Assert.assertTrue(treasure.containsKey("value"), "Treasure should have value field");
            Assert.assertTrue(treasure.containsKey("description"), "Treasure should have description field");
            Assert.assertTrue(treasure.containsKey("location"), "Treasure should have location field");
            Assert.assertTrue(treasure.containsKey("discovered"), "Treasure should have discovered field");
        }
        
        log.info("All treasures have required fields");
    }

    @And("the response should contain the treasure details")
    public void theResponseShouldContainTheTreasureDetails() {
        Map<String, Object> treasure = response.as(Map.class);
        
        Assert.assertEquals(treasure.get("id").toString(), String.valueOf(createdTreasureId), 
                "Treasure ID should match");
        Assert.assertEquals(treasure.get("name"), createdTreasure.get("name"), 
                "Treasure name should match");
        Assert.assertEquals(treasure.get("description"), createdTreasure.get("description"), 
                "Treasure description should match");
        
        log.info("Response contains correct treasure details");
    }

    @And("the response should contain the created treasure with an id")
    public void theResponseShouldContainTheCreatedTreasureWithAnId() {
        Map<String, Object> treasure = response.as(Map.class);
        
        Assert.assertTrue(treasure.containsKey("id"), "Created treasure should have id field");
        createdTreasureId = Long.parseLong(treasure.get("id").toString());
        createdTreasure = treasure;
        
        log.info("Response contains created treasure with ID: {}", createdTreasureId);
    }

    @And("the created treasure should have name {string}")
    public void theCreatedTreasureShouldHaveName(String name) {
        Map<String, Object> treasure = response.as(Map.class);
        Assert.assertEquals(treasure.get("name"), name, "Created treasure should have name: " + name);
        log.info("Created treasure has name: {}", name);
    }

    @And("the response should contain the updated treasure")
    public void theResponseShouldContainTheUpdatedTreasure() {
        Map<String, Object> treasure = response.as(Map.class);
        
        Assert.assertEquals(treasure.get("id").toString(), String.valueOf(createdTreasureId), 
                "Updated treasure ID should match");
        
        log.info("Response contains updated treasure");
    }

    @And("the updated treasure should have value {int}")
    public void theUpdatedTreasureShouldHaveValue(int value) {
        Map<String, Object> treasure = response.as(Map.class);
        
        // Handle different number types in JSON deserialization
        int treasureValue;
        if (treasure.get("value") instanceof Integer) {
            treasureValue = (Integer) treasure.get("value");
        } else if (treasure.get("value") instanceof Long) {
            treasureValue = ((Long) treasure.get("value")).intValue();
        } else {
            treasureValue = Integer.parseInt(treasure.get("value").toString());
        }
        
        Assert.assertEquals(treasureValue, value, "Updated treasure should have value: " + value);
        log.info("Updated treasure has value: {}", value);
    }

    @And("the updated treasure should have discovered {boolean}")
    public void theUpdatedTreasureShouldHaveDiscovered(boolean discovered) {
        Map<String, Object> treasure = response.as(Map.class);
        Assert.assertEquals(treasure.get("discovered"), discovered, 
                "Updated treasure should have discovered: " + discovered);
        log.info("Updated treasure has discovered: {}", discovered);
    }

    @And("all treasure names should contain {string}")
    public void allTreasureNamesShouldContain(String substring) {
        List<Map<String, Object>> treasures = response.jsonPath().getList("");
        
        for (Map<String, Object> treasure : treasures) {
            String name = treasure.get("name").toString();
            Assert.assertTrue(name.contains(substring), 
                    "Treasure name should contain: " + substring + ", but was: " + name);
        }
        
        log.info("All treasure names contain: {}", substring);
    }

    @And("the treasure list should not contain treasures with name containing {string}")
    public void theTreasureListShouldNotContainTreasuresWithNameContaining(String substring) {
        List<Map<String, Object>> treasures = response.jsonPath().getList("");
        
        for (Map<String, Object> treasure : treasures) {
            String name = treasure.get("name").toString();
            Assert.assertFalse(name.contains(substring), 
                    "Treasure name should not contain: " + substring + ", but was: " + name);
        }
        
        log.info("No treasure names contain: {}", substring);
    }

    @And("all treasures should have discovered status {boolean}")
    public void allTreasuresShouldHaveDiscoveredStatus(boolean discovered) {
        List<Map<String, Object>> treasures = response.jsonPath().getList("");
        
        for (Map<String, Object> treasure : treasures) {
            Assert.assertEquals(treasure.get("discovered"), discovered, 
                    "Treasure should have discovered: " + discovered);
        }
        
        log.info("All treasures have discovered status: {}", discovered);
    }
}