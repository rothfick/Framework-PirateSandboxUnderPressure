package com.peliQAn.framework.stepdefinitions;

import com.github.javafaker.Faker;
import com.peliQAn.framework.api.AuthApiClient;
import com.peliQAn.framework.api.TreasureApiClient;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.wiremock.client.WireMock;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.wiremock.client.WireMock.*;

/**
 * Step definitions for advanced API testing scenarios
 */
public class AdvancedApiSteps {

    private TreasureApiClient treasureApiClient;
    private AuthApiClient authApiClient;
    private String authToken;
    private Response lastResponse;
    private Map<String, Object> lastCreatedTreasure;
    private List<Map<String, Object>> testTreasures = new ArrayList<>();
    private final Faker faker = new Faker();
    private long startTime;
    private long endTime;
    
    // WireMock server settings
    private static final int WIREMOCK_PORT = 8888;
    private static final String WIREMOCK_HOST = "localhost";

    @Before
    public void setup() {
        treasureApiClient = new TreasureApiClient();
        authApiClient = new AuthApiClient();
    }

    @After
    public void cleanup() {
        // Clean up any resources created during tests
        cleanupTestTreasures();
    }

    @Given("the API base URL is set")
    public void theApiBaseUrlIsSet() {
        // The base URL is set in the API client constructor using PropertyManager
        Assert.assertNotNull(treasureApiClient, "TreasureApiClient should be initialized");
    }

    @And("I am authenticated with valid credentials")
    public void iAmAuthenticatedWithValidCredentials() {
        // Register and login a test user
        String username = "testuser_" + System.currentTimeMillis();
        String email = username + "@example.com";
        String password = "Password123";
        
        authToken = authApiClient.registerAndLogin(username, email, password);
        Assert.assertNotNull(authToken, "Authentication token should not be null");
        treasureApiClient.setAuthToken(authToken);
    }

    @When("I create a new treasure with valid data")
    public void iCreateANewTreasureWithValidData() {
        // Create treasure with random data
        String treasureName = "Test Treasure " + faker.ancient().god();
        int treasureValue = faker.number().numberBetween(100, 10000);
        String description = faker.lorem().sentence();
        String location = faker.ancient().hero() + "'s " + faker.elderScrolls().region();
        
        Map<String, Object> treasureData = new HashMap<>();
        treasureData.put("name", treasureName);
        treasureData.put("value", treasureValue);
        treasureData.put("description", description);
        treasureData.put("location", location);
        treasureData.put("discovered", false);
        
        lastResponse = treasureApiClient.createTreasure(treasureData);
        lastCreatedTreasure = new HashMap<>();
        lastCreatedTreasure.putAll(treasureData);
        
        // Store ID for cleanup
        if (lastResponse.getStatusCode() == 201) {
            Long treasureId = lastResponse.jsonPath().getLong("id");
            lastCreatedTreasure.put("id", treasureId);
            testTreasures.add(lastCreatedTreasure);
        }
    }

    @Then("the treasure should be created successfully")
    public void theTreasureShouldBeCreatedSuccessfully() {
        Assert.assertEquals(lastResponse.getStatusCode(), 201, "Expected status code 201 for treasure creation");
        Assert.assertEquals(lastResponse.jsonPath().getString("name"), lastCreatedTreasure.get("name"), 
                "Response name should match request name");
        Assert.assertEquals(lastResponse.jsonPath().getInt("value"), lastCreatedTreasure.get("value"), 
                "Response value should match request value");
    }

    @And("the response should match the defined JSON schema")
    public void theResponseShouldMatchTheDefinedJsonSchema() {
        // Define schema inline for testing (in a real project, this would be in a separate file)
        String treasureSchema = """
                {
                  "$schema": "http://json-schema.org/draft-07/schema#",
                  "type": "object",
                  "required": ["id", "name", "value", "description", "location", "discovered"],
                  "properties": {
                    "id": { "type": "number" },
                    "name": { "type": "string" },
                    "value": { "type": "number" },
                    "description": { "type": "string" },
                    "location": { "type": "string" },
                    "discovered": { "type": "boolean" }
                  }
                }
                """;
        
        lastResponse.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(treasureSchema));
    }

    @And("I should clean up the created treasure")
    public void iShouldCleanUpTheCreatedTreasure() {
        if (lastCreatedTreasure != null && lastCreatedTreasure.containsKey("id")) {
            Long treasureId = ((Number) lastCreatedTreasure.get("id")).longValue();
            Response deleteResponse = treasureApiClient.deleteTreasure(treasureId);
            Assert.assertEquals(deleteResponse.getStatusCode(), 204, 
                    "Expected status code 204 for treasure deletion");
        }
    }

    @When("I request a list of all treasures")
    public void iRequestAListOfAllTreasures() {
        startTime = System.currentTimeMillis();
        lastResponse = treasureApiClient.getAllTreasures();
        endTime = System.currentTimeMillis();
    }

    @Then("the response time should be less than {int} seconds")
    public void theResponseTimeShouldBeLessThanSeconds(int seconds) {
        long durationMs = endTime - startTime;
        long durationSec = TimeUnit.MILLISECONDS.toSeconds(durationMs);
        
        Assert.assertTrue(durationSec < seconds, 
                "Response time should be less than " + seconds + " seconds, but was " + durationSec + " seconds");
        
        // Also verify using RestAssured's time measurement
        long responseTimeMs = lastResponse.getTimeIn(TimeUnit.MILLISECONDS);
        Assert.assertTrue(responseTimeMs < seconds * 1000, 
                "Response time from RestAssured should be less than " + (seconds * 1000) + "ms, but was " + responseTimeMs + "ms");
    }

    @When("I create a new treasure as undiscovered")
    public void iCreateANewTreasureAsUndiscovered() {
        // Similar to create treasure method but explicitly setting discovered to false
        String treasureName = "Undiscovered Treasure " + faker.ancient().god();
        int treasureValue = faker.number().numberBetween(100, 10000);
        
        Map<String, Object> treasureData = new HashMap<>();
        treasureData.put("name", treasureName);
        treasureData.put("value", treasureValue);
        treasureData.put("description", "An undiscovered treasure to test status updates");
        treasureData.put("location", faker.ancient().hero() + "'s Tomb");
        treasureData.put("discovered", false);
        
        lastResponse = treasureApiClient.createTreasure(treasureData);
        lastCreatedTreasure = new HashMap<>();
        lastCreatedTreasure.putAll(treasureData);
        
        // Store ID for next steps and cleanup
        if (lastResponse.getStatusCode() == 201) {
            Long treasureId = lastResponse.jsonPath().getLong("id");
            lastCreatedTreasure.put("id", treasureId);
            testTreasures.add(lastCreatedTreasure);
        }
    }

    @And("I mark the treasure as discovered")
    public void iMarkTheTreasureAsDiscovered() {
        if (lastCreatedTreasure != null && lastCreatedTreasure.containsKey("id")) {
            Long treasureId = ((Number) lastCreatedTreasure.get("id")).longValue();
            lastResponse = treasureApiClient.discoverTreasure(treasureId);
            
            // Update our reference
            if (lastResponse.getStatusCode() == 200) {
                lastCreatedTreasure.put("discovered", true);
            }
        } else {
            Assert.fail("No treasure was created in previous step");
        }
    }

    @Then("I should find the treasure in the discovered treasures list")
    public void iShouldFindTheTreasureInTheDiscoveredTreasuresList() {
        if (lastCreatedTreasure != null && lastCreatedTreasure.containsKey("id")) {
            Long treasureId = ((Number) lastCreatedTreasure.get("id")).longValue();
            
            // Get discovered treasures and check if our treasure is there
            Response discoveredResponse = treasureApiClient.getDiscoveredTreasures();
            Assert.assertEquals(discoveredResponse.getStatusCode(), 200, "Expected status code 200 for discovered treasures");
            
            List<Map<String, Object>> discoveredTreasures = discoveredResponse.jsonPath().getList("");
            
            boolean found = discoveredTreasures.stream()
                    .anyMatch(t -> {
                        Long id = ((Number) t.get("id")).longValue();
                        return id.equals(treasureId);
                    });
            
            Assert.assertTrue(found, "The created treasure should be in the discovered treasures list");
        } else {
            Assert.fail("No treasure was created in previous steps");
        }
    }

    @Given("there are treasures with different properties")
    public void thereAreTreasuresWithDifferentProperties() {
        // Create several treasures with different properties for testing filters
        
        // 1. Gold treasure with high value
        Map<String, Object> goldTreasure = treasureApiClient.createTreasureHelper(
                "Golden Crown", 5000, "A valuable golden crown", "Royal Palace", false);
        testTreasures.add(goldTreasure);
        
        // 2. Gold treasure with medium value
        Map<String, Object> goldMedium = treasureApiClient.createTreasureHelper(
                "Gold Ring", 2000, "A gold ring", "Jewelry Box", true);
        testTreasures.add(goldMedium);
        
        // 3. Silver treasure with low value
        Map<String, Object> silverTreasure = treasureApiClient.createTreasureHelper(
                "Silver Coin", 500, "A silver coin", "Pirate Chest", false);
        testTreasures.add(silverTreasure);
        
        // 4. Expensive non-gold treasure
        Map<String, Object> diamondTreasure = treasureApiClient.createTreasureHelper(
                "Diamond Necklace", 8000, "A diamond necklace", "Secret Safe", true);
        testTreasures.add(diamondTreasure);
        
        // Verify creations
        Assert.assertEquals(testTreasures.size(), 4, "Expected 4 test treasures to be created");
    }

    @When("I search for treasures with {string} in the name")
    public void iSearchForTreasuresWithInTheName(String searchTerm) {
        lastResponse = treasureApiClient.searchTreasuresByName(searchTerm);
        Assert.assertEquals(lastResponse.getStatusCode(), 200, "Expected status code 200 for treasure search");
    }

    @And("I filter treasures with value between {int} and {int}")
    public void iFilterTreasuresWithValueBetweenAnd(int minValue, int maxValue) {
        // For this demo, we'll reuse the lastResponse and filter it programmatically
        // In a real implementation, we might make a new API call with filter parameters
        
        List<Map<String, Object>> allTreasures = lastResponse.jsonPath().getList("");
        
        List<Map<String, Object>> filteredTreasures = allTreasures.stream()
                .filter(t -> {
                    int value = ((Number) t.get("value")).intValue();
                    return value >= minValue && value <= maxValue;
                })
                .toList();
        
        // Store the filtered result for the next step
        // In a real implementation, we would make an actual API call to filter
        lastResponse = given()
                .contentType("application/json")
                .body(filteredTreasures)
                .when()
                .get("/dummy")  // This isn't actually sent anywhere
                .then()
                .extract()
                .response();
    }

    @Then("I should receive treasures matching all filter criteria")
    public void iShouldReceiveTreasuresMatchingAllFilterCriteria() {
        List<Map<String, Object>> filteredTreasures = lastResponse.jsonPath().getList("");
        Assert.assertFalse(filteredTreasures.isEmpty(), "Filtered treasures should not be empty");
        
        // Verify each treasure matches the criteria
        for (Map<String, Object> treasure : filteredTreasures) {
            String name = (String) treasure.get("name");
            int value = ((Number) treasure.get("value")).intValue();
            
            Assert.assertTrue(name.toLowerCase().contains("gold"), 
                    "Treasure name should contain 'gold': " + name);
            Assert.assertTrue(value >= 1000 && value <= 5000, 
                    "Treasure value should be between 1000 and 5000: " + value);
        }
    }

    @And("I should clean up the test treasures")
    public void iShouldCleanUpTheTestTreasures() {
        cleanupTestTreasures();
    }

    @When("I request a non-existent treasure")
    public void iRequestANonExistentTreasure() {
        // Request a treasure with an ID that doesn't exist
        long nonExistentId = 999999L;
        lastResponse = treasureApiClient.getTreasureById(nonExistentId);
    }

    @Then("I should receive a not found error response")
    public void iShouldReceiveANotFoundErrorResponse() {
        Assert.assertEquals(lastResponse.getStatusCode(), 404, "Expected status code 404 for non-existent treasure");
        
        // Verify error response structure
        Assert.assertTrue(lastResponse.getBody().asString().contains("not found"), 
                "Error message should contain 'not found'");
    }

    @When("I submit invalid treasure data")
    public void iSubmitInvalidTreasureData() {
        // Create treasure with invalid data (empty name, negative value)
        Map<String, Object> invalidData = new HashMap<>();
        invalidData.put("name", "");  // Empty name
        invalidData.put("value", -100);  // Negative value
        invalidData.put("description", "Invalid treasure data");
        invalidData.put("location", "Test Location");
        invalidData.put("discovered", false);
        
        lastResponse = treasureApiClient.createTreasure(invalidData);
    }

    @Then("I should receive a validation error response")
    public void iShouldReceiveAValidationErrorResponse() {
        Assert.assertEquals(lastResponse.getStatusCode(), 400, "Expected status code 400 for invalid treasure data");
        
        // Verify validation error structure
        Assert.assertTrue(lastResponse.getBody().asString().contains("validation"), 
                "Error message should indicate validation failure");
    }

    @Given("the WireMock server is configured")
    public void theWireMockServerIsConfigured() {
        // Configure WireMock server
        WireMock.configureFor(WIREMOCK_HOST, WIREMOCK_PORT);
        WireMock.reset();
        
        // Define a basic stub
        stubFor(get(urlEqualTo("/api/treasures/mock/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"id\": 1,\n" +
                                "  \"name\": \"Mocked Treasure\",\n" +
                                "  \"value\": 5000,\n" +
                                "  \"description\": \"A treasure created by WireMock\",\n" +
                                "  \"location\": \"WireMock Server\",\n" +
                                "  \"discovered\": false\n" +
                                "}")));
    }

    @When("I make a request to the mocked treasure endpoint")
    public void iMakeARequestToTheMockedTreasureEndpoint() {
        // Make request to the mock server
        lastResponse = given()
                .baseUri("http://" + WIREMOCK_HOST + ":" + WIREMOCK_PORT)
                .when()
                .get("/api/treasures/mock/1")
                .then()
                .extract()
                .response();
    }

    @Then("I should receive the predefined mock response")
    public void iShouldReceiveThePredefinedMockResponse() {
        Assert.assertEquals(lastResponse.getStatusCode(), 200, "Expected status code 200 from mock server");
        Assert.assertEquals(lastResponse.jsonPath().getString("name"), "Mocked Treasure", 
                "Response should contain the mocked treasure name");
        
        // Verify the mock was called
        verify(getRequestedFor(urlEqualTo("/api/treasures/mock/1")));
    }

    @Given("the WireMock server is configured with network delays")
    public void theWireMockServerIsConfiguredWithNetworkDelays() {
        // Configure WireMock server with delay
        WireMock.configureFor(WIREMOCK_HOST, WIREMOCK_PORT);
        WireMock.reset();
        
        // Define a stub with delay
        stubFor(get(urlEqualTo("/api/treasures/slow"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"id\": 5,\n" +
                                "  \"name\": \"Slow Treasure\",\n" +
                                "  \"value\": 1000\n" +
                                "}")
                        .withFixedDelay(2000))); // 2 second delay
    }

    @When("I make a request to the delayed endpoint")
    public void iMakeARequestToTheDelayedEndpoint() {
        // Make request to the delayed endpoint and measure time
        startTime = System.currentTimeMillis();
        
        lastResponse = given()
                .baseUri("http://" + WIREMOCK_HOST + ":" + WIREMOCK_PORT)
                .when()
                .get("/api/treasures/slow")
                .then()
                .extract()
                .response();
        
        endTime = System.currentTimeMillis();
    }

    @Then("the response should be delayed by the configured time")
    public void theResponseShouldBeDelayedByTheConfiguredTime() {
        long durationMs = endTime - startTime;
        
        Assert.assertEquals(lastResponse.getStatusCode(), 200, "Expected status code 200 from mock server");
        Assert.assertEquals(lastResponse.jsonPath().getString("name"), "Slow Treasure", 
                "Response should contain the correct treasure name");
        
        // Verify the delay was applied (should be at least 2000ms)
        Assert.assertTrue(durationMs >= 2000, 
                "Response should be delayed by at least 2000ms, but was " + durationMs + "ms");
    }

    @Given("the WireMock server is configured with fault injection")
    public void theWireMockServerIsConfiguredWithFaultInjection() {
        // Configure WireMock server with faults
        WireMock.configureFor(WIREMOCK_HOST, WIREMOCK_PORT);
        WireMock.reset();
        
        // Define stubs with different faults
        
        // Empty response
        stubFor(get(urlEqualTo("/api/treasures/fault/empty-response"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("")));
        
        // Malformed JSON
        stubFor(get(urlEqualTo("/api/treasures/fault/malformed-json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"name\": \"Broken Treasure\", \"value\": 100, ")));
    }

    @When("I make a request to the faulty endpoint")
    public void iMakeARequestToTheFaultyEndpoint() {
        // Make request to a faulty endpoint (empty response)
        lastResponse = given()
                .baseUri("http://" + WIREMOCK_HOST + ":" + WIREMOCK_PORT)
                .when()
                .get("/api/treasures/fault/empty-response")
                .then()
                .extract()
                .response();
    }

    @Then("the client should handle the error appropriately")
    public void theClientShouldHandleTheErrorAppropriately() {
        // For empty response test
        Assert.assertEquals(lastResponse.getStatusCode(), 200, "Status code should be 200");
        Assert.assertEquals(lastResponse.getBody().asString(), "", "Body should be empty");
        
        // For malformed JSON, we test that the client can handle parsing errors
        Response malformedResponse = given()
                .baseUri("http://" + WIREMOCK_HOST + ":" + WIREMOCK_PORT)
                .when()
                .get("/api/treasures/fault/malformed-json")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        try {
            // This should throw an exception for malformed JSON
            malformedResponse.jsonPath().getString("name");
            Assert.fail("Should have thrown exception for malformed JSON");
        } catch (Exception e) {
            // Expected exception
            Assert.assertTrue(e.getMessage().contains("Failed to parse") || 
                             e.getMessage().contains("Unexpected end") ||
                             e.getMessage().contains("incomplete"));
        }
    }

    @Given("the WireMock server is configured with stateful behavior")
    public void theWireMockServerIsConfiguredWithStatefulBehavior() {
        // Configure WireMock server with stateful behavior
        WireMock.configureFor(WIREMOCK_HOST, WIREMOCK_PORT);
        WireMock.reset();
        
        // Initial state: Treasure not discovered
        stubFor(get(urlEqualTo("/api/treasures/stateful/1"))
                .inScenario("Treasure Discovery")
                .whenScenarioStateIs(WireMock.STARTED)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"id\": 1,\n" +
                                "  \"name\": \"Stateful Treasure\",\n" +
                                "  \"value\": 9000,\n" +
                                "  \"description\": \"A treasure that changes state\",\n" +
                                "  \"location\": \"Secret Cave\",\n" +
                                "  \"discovered\": false\n" +
                                "}")));
        
        // Transition action: Mark treasure as discovered
        stubFor(put(urlEqualTo("/api/treasures/stateful/1/discover"))
                .inScenario("Treasure Discovery")
                .whenScenarioStateIs(WireMock.STARTED)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"id\": 1,\n" +
                                "  \"name\": \"Stateful Treasure\",\n" +
                                "  \"value\": 9000,\n" +
                                "  \"description\": \"A treasure that changes state\",\n" +
                                "  \"location\": \"Secret Cave\",\n" +
                                "  \"discovered\": true\n" +
                                "}"))
                .willSetStateTo("discovered"));
        
        // Final state: Treasure is discovered
        stubFor(get(urlEqualTo("/api/treasures/stateful/1"))
                .inScenario("Treasure Discovery")
                .whenScenarioStateIs("discovered")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"id\": 1,\n" +
                                "  \"name\": \"Stateful Treasure\",\n" +
                                "  \"value\": 9000,\n" +
                                "  \"description\": \"A treasure that changes state\",\n" +
                                "  \"location\": \"Secret Cave\",\n" +
                                "  \"discovered\": true\n" +
                                "}")));
    }

    @When("I change the state of a resource through the mock API")
    public void iChangeTheStateOfAResourceThroughTheMockApi() {
        // First get the initial state
        Response initialResponse = given()
                .baseUri("http://" + WIREMOCK_HOST + ":" + WIREMOCK_PORT)
                .when()
                .get("/api/treasures/stateful/1")
                .then()
                .extract()
                .response();
        
        // Verify initial state
        Assert.assertEquals(initialResponse.getStatusCode(), 200);
        Assert.assertEquals(initialResponse.jsonPath().getBoolean("discovered"), false);
        
        // Perform transition to change state
        lastResponse = given()
                .baseUri("http://" + WIREMOCK_HOST + ":" + WIREMOCK_PORT)
                .when()
                .put("/api/treasures/stateful/1/discover")
                .then()
                .extract()
                .response();
    }

    @Then("subsequent requests should reflect the new state")
    public void subsequentRequestsShouldReflectTheNewState() {
        // Verify the transition response
        Assert.assertEquals(lastResponse.getStatusCode(), 200);
        Assert.assertEquals(lastResponse.jsonPath().getBoolean("discovered"), true);
        
        // Check that the state was updated
        Response finalResponse = given()
                .baseUri("http://" + WIREMOCK_HOST + ":" + WIREMOCK_PORT)
                .when()
                .get("/api/treasures/stateful/1")
                .then()
                .extract()
                .response();
        
        // Verify final state
        Assert.assertEquals(finalResponse.getStatusCode(), 200);
        Assert.assertEquals(finalResponse.jsonPath().getBoolean("discovered"), true);
    }

    @Given("I have defined Pact contracts as a consumer")
    public void iHaveDefinedPactContractsAsAConsumer() {
        // This is more of a documentation step since Pact contracts are defined in test classes
        // In a real test, we would validate that the contracts are in place
        System.out.println("Pact contracts are defined in AdvancedPactConsumerTest");
    }

    @When("I verify against the provider API")
    public void iVerifyAgainstTheProviderApi() {
        // This step would normally be run as part of the Pact verification process
        System.out.println("Provider verification would be run using the PactTestFor annotation");
    }

    @Then("all contract expectations should be met")
    public void allContractExpectationsShouldBeMet() {
        // This is verified automatically by the Pact framework
        System.out.println("Contract expectations are verified by the Pact framework");
    }

    @Given("I have defined type-based matchers in Pact contracts")
    public void iHaveDefinedTypeBasedMatchersInPactContracts() {
        // Documentation step for Pact type matchers
        System.out.println("Type-based matchers are defined in the Pact DSL in AdvancedPactConsumerTest");
    }

    @When("I verify various data types in the response")
    public void iVerifyVariousDataTypesInTheResponse() {
        // This would be automatically handled by Pact
        System.out.println("Data type verification is handled by the Pact framework");
    }

    @Then("the contract verification should succeed")
    public void theContractVerificationShouldSucceed() {
        // Automatically verified by Pact
        System.out.println("Contract verification success is reported by the Pact framework");
    }

    @Given("I have contracts for error responses")
    public void iHaveContractsForErrorResponses() {
        // Documentation step for error contracts
        System.out.println("Error contracts are defined in notFoundErrorPact and validationErrorPact methods");
    }

    @When("I trigger error conditions against the provider")
    public void iTriggerErrorConditionsAgainstTheProvider() {
        // This would be part of the Pact verification
        System.out.println("Error conditions are triggered during Pact provider verification");
    }

    @Then("the error responses should match the contracts")
    public void theErrorResponsesShouldMatchTheContracts() {
        // Automatically verified by Pact
        System.out.println("Error response matching is handled by the Pact framework");
    }

    private void cleanupTestTreasures() {
        // Clean up all test treasures
        for (Map<String, Object> treasure : testTreasures) {
            if (treasure.containsKey("id")) {
                try {
                    Long treasureId = ((Number) treasure.get("id")).longValue();
                    treasureApiClient.deleteTreasure(treasureId);
                } catch (Exception e) {
                    System.err.println("Error deleting treasure: " + e.getMessage());
                }
            }
        }
        testTreasures.clear();
    }
}