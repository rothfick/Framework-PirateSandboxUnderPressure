package com.peliQAn.framework.api.advanced.pact;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Advanced Pact Consumer Tests demonstrating various contract testing techniques
 */
@Epic("Contract Tests")
@Feature("Advanced PACT Consumer Contracts")
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "treasureProvider")
public class AdvancedPactConsumerTest {

    /**
     * Define contract for filtering treasures with query parameters
     */
    @Pact(provider = "treasureProvider", consumer = "treasureConsumer")
    public RequestResponsePact filterTreasuresPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        
        return builder
                .given("treasures with various values exist")
                .uponReceiving("a request to filter treasures by value")
                .path("/api/treasures/search")
                .method("GET")
                .query("minValue=1000&maxValue=5000")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body(new PactDslJsonArray()
                        .arrayEachLike()
                        .id()
                        .stringType("name", "Example Treasure")
                        .numberType("value", 2000)
                        .stringType("description", "A treasure description")
                        .stringType("location", "Treasure Location")
                        .booleanType("discovered", false)
                        .closeObject())
                .toPact();
    }
    
    /**
     * Define contract for type-based matching of treasure response
     */
    @Pact(provider = "treasureProvider", consumer = "treasureConsumer")
    public RequestResponsePact getDetailedTreasurePact(PactDslWithProvider builder) {
        return builder
                .given("a detailed treasure exists")
                .uponReceiving("a request to get a detailed treasure")
                .path("/api/treasures/detail/1")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(new PactDslJsonBody()
                        .id()
                        .stringType("name")
                        .numberType("value")
                        .stringType("description")
                        .stringType("location")
                        .booleanType("discovered")
                        .object("metadata")
                            .stringType("createdBy")
                            .datetime("createdAt", "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                            .datetime("updatedAt", "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                            .numberType("version")
                        .closeObject()
                        .array("tags")
                            .stringType("gold")
                            .stringType("ancient")
                        .closeArray()
                        .object("coordinates")
                            .numberType("latitude")
                            .numberType("longitude")
                        .closeObject())
                .toPact();
    }
    
    /**
     * Define contract for bulk treasure creation
     */
    @Pact(provider = "treasureProvider", consumer = "treasureConsumer")
    public RequestResponsePact bulkCreateTreasuresPact(PactDslWithProvider builder) {
        // Define the request body using PactDslJsonArray
        PactDslJsonArray requestBody = new PactDslJsonArray()
                .object()
                    .stringValue("name", "Gold Coin")
                    .numberValue("value", 100)
                    .stringValue("description", "A shiny gold coin")
                    .stringValue("location", "Pirate Bay")
                    .booleanValue("discovered", false)
                .closeObject()
                .object()
                    .stringValue("name", "Silver Ring")
                    .numberValue("value", 50)
                    .stringValue("description", "A silver ring")
                    .stringValue("location", "Treasure Island")
                    .booleanValue("discovered", true)
                .closeObject();
        
        // Define the response body with matching rules
        PactDslJsonBody responseBody = new PactDslJsonBody()
                .integerType("count", 2)
                .minArrayLike("created", 2)
                    .id()
                    .stringType("name")
                    .numberType("value")
                    .stringType("description")
                    .stringType("location")
                    .booleanType("discovered")
                .closeObject()
                .array("errors")
                .closeArray();
        
        return builder
                .given("authorized user exists")
                .uponReceiving("a request to bulk create treasures")
                .path("/api/treasures/bulk")
                .method("POST")
                .headers(Map.of(
                        "Content-Type", "application/json",
                        "Authorization", "Bearer test-token"
                ))
                .body(requestBody)
                .willRespondWith()
                .status(201)
                .headers(Map.of("Content-Type", "application/json"))
                .body(responseBody)
                .toPact();
    }
    
    /**
     * Define contract for API errors
     */
    @Pact(provider = "treasureProvider", consumer = "treasureConsumer")
    public RequestResponsePact notFoundErrorPact(PactDslWithProvider builder) {
        return builder
                .given("no treasure with ID 999 exists")
                .uponReceiving("a request for a non-existent treasure")
                .path("/api/treasures/999")
                .method("GET")
                .willRespondWith()
                .status(404)
                .headers(Map.of("Content-Type", "application/json"))
                .body(new PactDslJsonBody()
                        .integerType("status", 404)
                        .stringType("message", "Treasure not found")
                        .stringType("path", "/api/treasures/999")
                        .timestamp("timestamp", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
                .toPact();
    }
    
    /**
     * Define contract for validation errors
     */
    @Pact(provider = "treasureProvider", consumer = "treasureConsumer")
    public RequestResponsePact validationErrorPact(PactDslWithProvider builder) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "");  // Empty name
        requestBody.put("value", -100);  // Negative value
        
        return builder
                .given("validation rules for treasures exist")
                .uponReceiving("a request with invalid treasure data")
                .path("/api/treasures")
                .method("POST")
                .headers(Map.of(
                        "Content-Type", "application/json",
                        "Authorization", "Bearer test-token"
                ))
                .body(requestBody)
                .willRespondWith()
                .status(400)
                .headers(Map.of("Content-Type", "application/json"))
                .body(new PactDslJsonBody()
                        .integerType("status", 400)
                        .stringType("message", "Validation failed")
                        .array("errors")
                            .object()
                                .stringType("field", "name")
                                .stringType("message", "must not be empty")
                            .closeObject()
                            .object()
                                .stringType("field", "value")
                                .stringType("message", "must be positive")
                            .closeObject()
                        .closeArray())
                .toPact();
    }
    
    /**
     * Define contract for treasure search with Lambda DSL
     */
    @Pact(provider = "treasureProvider", consumer = "treasureConsumer")
    public RequestResponsePact searchTreasuresByNamePact(PactDslWithProvider builder) {
        // Use Lambda DSL for more expressive contract definition
        DslPart responseBody = LambdaDsl.newJsonArray((array) -> {
            array.appendEach(treasure -> {
                treasure.id();
                treasure.stringType("name", "Gold Crown");
                treasure.numberType("value", 5000);
                treasure.stringType("description", "A golden crown");
                treasure.stringType("location", "Royal Palace");
                treasure.booleanType("discovered", false);
            }, 1, 3);  // Expect between 1 and 3 matching treasures
        }).build();
        
        return builder
                .given("treasures with 'gold' in the name exist")
                .uponReceiving("a request to search treasures by name containing gold")
                .path("/api/treasures/search")
                .method("GET")
                .query("name=gold")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(responseBody)
                .toPact();
    }
    
    /**
     * Define contract for treasure statistics
     */
    @Pact(provider = "treasureProvider", consumer = "treasureConsumer")
    public RequestResponsePact treasureStatisticsPact(PactDslWithProvider builder) {
        return builder
                .given("treasure statistics are available")
                .uponReceiving("a request for treasure statistics")
                .path("/api/treasures/stats")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(new PactDslJsonBody()
                        .numberType("totalTreasures")
                        .numberType("discoveredTreasures")
                        .numberType("undiscoveredTreasures")
                        .numberType("totalValue")
                        .numberType("averageValue")
                        .numberType("minValue")
                        .numberType("maxValue")
                        .object("valueDistribution")
                            .numberType("0-999")
                            .numberType("1000-4999")
                            .numberType("5000+")
                        .closeObject()
                        .object("locationDistribution")
                            .eachKeyLike("location")
                            .numberType()
                            .closeObject()
                        .closeObject())
                .toPact();
    }
    
    /**
     * Test filtering treasures with query parameters
     */
    @Test
    @PactTestFor(pactMethod = "filterTreasuresPact")
    @Story("Filter Treasures")
    @Description("Test filtering treasures by value range")
    public void testFilterTreasuresByValue(MockServer mockServer) {
        // Make request to mock server
        Response response = given()
                .baseUri(mockServer.getUrl())
                .when()
                .get("/api/treasures/search?minValue=1000&maxValue=5000")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        // Verify response
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("Example Treasure"));
        List<Map<String, Object>> treasures = response.jsonPath().getList("");
        assertFalse(treasures.isEmpty());
        
        // Verify all treasures are within the specified value range
        for (Map<String, Object> treasure : treasures) {
            int value = ((Number)treasure.get("value")).intValue();
            assertTrue(value >= 1000 && value <= 5000,
                    "Treasure value should be between 1000 and 5000");
        }
    }
    
    /**
     * Test retrieving detailed treasure
     */
    @Test
    @PactTestFor(pactMethod = "getDetailedTreasurePact")
    @Story("Detailed Treasure Information")
    @Description("Test retrieving detailed treasure information with metadata")
    public void testGetDetailedTreasure(MockServer mockServer) {
        // Make request to mock server
        Response response = given()
                .baseUri(mockServer.getUrl())
                .when()
                .get("/api/treasures/detail/1")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        // Verify response
        assertEquals(200, response.getStatusCode());
        
        // Verify metadata structure
        assertNotNull(response.jsonPath().get("metadata"));
        assertNotNull(response.jsonPath().getString("metadata.createdBy"));
        assertNotNull(response.jsonPath().getString("metadata.createdAt"));
        assertNotNull(response.jsonPath().getString("metadata.updatedAt"));
        assertTrue(response.jsonPath().getInt("metadata.version") >= 0);
        
        // Verify tags array
        List<String> tags = response.jsonPath().getList("tags");
        assertTrue(tags.size() >= 2);
        
        // Verify coordinates
        assertNotNull(response.jsonPath().get("coordinates.latitude"));
        assertNotNull(response.jsonPath().get("coordinates.longitude"));
    }
    
    /**
     * Test bulk creation of treasures
     */
    @Test
    @PactTestFor(pactMethod = "bulkCreateTreasuresPact")
    @Story("Bulk Treasure Creation")
    @Description("Test bulk creation of multiple treasures in a single request")
    public void testBulkCreateTreasures(MockServer mockServer) {
        // Prepare request body
        String requestBody = """
                [
                  {
                    "name": "Gold Coin",
                    "value": 100,
                    "description": "A shiny gold coin",
                    "location": "Pirate Bay",
                    "discovered": false
                  },
                  {
                    "name": "Silver Ring",
                    "value": 50,
                    "description": "A silver ring",
                    "location": "Treasure Island",
                    "discovered": true
                  }
                ]
                """;
        
        // Make request to mock server
        Response response = given()
                .baseUri(mockServer.getUrl())
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer test-token")
                .body(requestBody)
                .when()
                .post("/api/treasures/bulk")
                .then()
                .statusCode(201)
                .extract()
                .response();
        
        // Verify response
        assertEquals(201, response.getStatusCode());
        assertEquals(2, response.jsonPath().getInt("count"));
        List<Map<String, Object>> createdTreasures = response.jsonPath().getList("created");
        assertEquals(2, createdTreasures.size());
        
        // Verify created treasures contain expected fields
        for (Map<String, Object> treasure : createdTreasures) {
            assertNotNull(treasure.get("id"));
            assertNotNull(treasure.get("name"));
            assertNotNull(treasure.get("value"));
            assertNotNull(treasure.get("description"));
            assertNotNull(treasure.get("location"));
            assertNotNull(treasure.get("discovered"));
        }
        
        // Verify errors array is empty
        List<Object> errors = response.jsonPath().getList("errors");
        assertTrue(errors.isEmpty());
    }
    
    /**
     * Test not found error response
     */
    @Test
    @PactTestFor(pactMethod = "notFoundErrorPact")
    @Story("Error Handling - Not Found")
    @Description("Test handling of not found errors")
    public void testNotFoundError(MockServer mockServer) {
        // Make request to mock server
        Response response = given()
                .baseUri(mockServer.getUrl())
                .when()
                .get("/api/treasures/999")
                .then()
                .statusCode(404)
                .extract()
                .response();
        
        // Verify response
        assertEquals(404, response.getStatusCode());
        assertEquals(404, response.jsonPath().getInt("status"));
        assertEquals("Treasure not found", response.jsonPath().getString("message"));
        assertEquals("/api/treasures/999", response.jsonPath().getString("path"));
        assertNotNull(response.jsonPath().getString("timestamp"));
    }
    
    /**
     * Test validation error response
     */
    @Test
    @PactTestFor(pactMethod = "validationErrorPact")
    @Story("Error Handling - Validation")
    @Description("Test handling of validation errors")
    public void testValidationError(MockServer mockServer) {
        // Prepare request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "");  // Empty name
        requestBody.put("value", -100);  // Negative value
        
        // Make request to mock server
        Response response = given()
                .baseUri(mockServer.getUrl())
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer test-token")
                .body(requestBody)
                .when()
                .post("/api/treasures")
                .then()
                .statusCode(400)
                .extract()
                .response();
        
        // Verify response
        assertEquals(400, response.getStatusCode());
        assertEquals(400, response.jsonPath().getInt("status"));
        assertEquals("Validation failed", response.jsonPath().getString("message"));
        
        // Verify errors array contains expected validation errors
        List<Map<String, Object>> errors = response.jsonPath().getList("errors");
        assertEquals(2, errors.size());
        
        // Check for name validation error
        boolean hasNameError = errors.stream()
                .anyMatch(error -> error.get("field").equals("name") && 
                                  error.get("message").equals("must not be empty"));
        assertTrue(hasNameError, "Response should contain validation error for name field");
        
        // Check for value validation error
        boolean hasValueError = errors.stream()
                .anyMatch(error -> error.get("field").equals("value") && 
                                  error.get("message").equals("must be positive"));
        assertTrue(hasValueError, "Response should contain validation error for value field");
    }
    
    /**
     * Test searching treasures by name
     */
    @Test
    @PactTestFor(pactMethod = "searchTreasuresByNamePact")
    @Story("Search Treasures")
    @Description("Test searching treasures by name")
    public void testSearchTreasuresByName(MockServer mockServer) {
        // Make request to mock server
        Response response = given()
                .baseUri(mockServer.getUrl())
                .when()
                .get("/api/treasures/search?name=gold")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        // Verify response
        assertEquals(200, response.getStatusCode());
        List<Map<String, Object>> treasures = response.jsonPath().getList("");
        assertFalse(treasures.isEmpty());
        assertTrue(treasures.size() >= 1 && treasures.size() <= 3);
        
        // Verify all treasures contain 'gold' in the name
        for (Map<String, Object> treasure : treasures) {
            String name = (String) treasure.get("name");
            assertThat(name.toLowerCase(), containsString("gold"));
        }
    }
    
    /**
     * Test retrieving treasure statistics
     */
    @Test
    @PactTestFor(pactMethod = "treasureStatisticsPact")
    @Story("Treasure Statistics")
    @Description("Test retrieving treasure statistics")
    public void testTreasureStatistics(MockServer mockServer) {
        // Make request to mock server
        Response response = given()
                .baseUri(mockServer.getUrl())
                .when()
                .get("/api/treasures/stats")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        // Verify response
        assertEquals(200, response.getStatusCode());
        
        // Verify statistics
        assertNotNull(response.jsonPath().get("totalTreasures"));
        assertNotNull(response.jsonPath().get("discoveredTreasures"));
        assertNotNull(response.jsonPath().get("undiscoveredTreasures"));
        assertNotNull(response.jsonPath().get("totalValue"));
        assertNotNull(response.jsonPath().get("averageValue"));
        assertNotNull(response.jsonPath().get("minValue"));
        assertNotNull(response.jsonPath().get("maxValue"));
        
        // Verify value distribution
        assertNotNull(response.jsonPath().get("valueDistribution"));
        assertNotNull(response.jsonPath().get("valueDistribution['0-999']"));
        assertNotNull(response.jsonPath().get("valueDistribution['1000-4999']"));
        assertNotNull(response.jsonPath().get("valueDistribution['5000+']"));
        
        // Verify location distribution
        assertNotNull(response.jsonPath().get("locationDistribution"));
    }
}