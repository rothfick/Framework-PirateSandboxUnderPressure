package com.peliQAn.framework.pact;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.peliQAn.framework.pact.PactBaseConsumer;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Consumer contract tests for Treasure API
 */
@Epic("Contract Tests")
@Feature("Treasure API Contracts")
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "treasureProvider")
public class TreasureConsumerPactTest extends PactBaseConsumer {

    /**
     * Define contract for getting all treasures
     */
    @Pact(provider = "treasureProvider", consumer = "treasureConsumer")
    public RequestResponsePact getAllTreasuresPact(PactDslWithProvider builder) {
        return builder
                .given("treasures exist")
                .uponReceiving("a request to get all treasures")
                .path("/api/treasures")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body("""
                [
                  {
                    "id": 1,
                    "name": "Golden Chalice",
                    "value": 1000,
                    "description": "An ancient golden chalice",
                    "location": "Skull Island",
                    "discovered": false
                  },
                  {
                    "id": 2,
                    "name": "Silver Crown",
                    "value": 500,
                    "description": "A royal silver crown",
                    "location": "Treasure Bay",
                    "discovered": true
                  }
                ]
                """)
                .toPact();
    }

    /**
     * Define contract for getting a specific treasure
     */
    @Pact(provider = "treasureProvider", consumer = "treasureConsumer")
    public RequestResponsePact getTreasureByIdPact(PactDslWithProvider builder) {
        return builder
                .given("treasure with ID 1 exists")
                .uponReceiving("a request to get a specific treasure")
                .path("/api/treasures/1")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body("""
                {
                  "id": 1,
                  "name": "Golden Chalice",
                  "value": 1000,
                  "description": "An ancient golden chalice",
                  "location": "Skull Island",
                  "discovered": false
                }
                """)
                .toPact();
    }

    /**
     * Define contract for creating a new treasure
     */
    @Pact(provider = "treasureProvider", consumer = "treasureConsumer")
    public RequestResponsePact createTreasurePact(PactDslWithProvider builder) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "Diamond Necklace");
        requestBody.put("value", 2000);
        requestBody.put("description", "A necklace with diamonds");
        requestBody.put("location", "Crystal Cave");
        requestBody.put("discovered", false);

        return builder
                .given("authorized user exists")
                .uponReceiving("a request to create a treasure")
                .path("/api/treasures")
                .method("POST")
                .headers(Map.of(
                        "Content-Type", "application/json",
                        "Authorization", "Bearer test-token"
                ))
                .body(requestBody)
                .willRespondWith()
                .status(201)
                .headers(Map.of("Content-Type", "application/json"))
                .body("""
                {
                  "id": 3,
                  "name": "Diamond Necklace",
                  "value": 2000,
                  "description": "A necklace with diamonds",
                  "location": "Crystal Cave",
                  "discovered": false
                }
                """)
                .toPact();
    }

    /**
     * Define contract for updating a treasure
     */
    @Pact(provider = "treasureProvider", consumer = "treasureConsumer")
    public RequestResponsePact updateTreasurePact(PactDslWithProvider builder) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "Golden Chalice");
        requestBody.put("value", 1500);
        requestBody.put("description", "An ancient golden chalice with jewels");
        requestBody.put("location", "Skull Island");
        requestBody.put("discovered", true);

        return builder
                .given("treasure with ID 1 exists and user is authorized")
                .uponReceiving("a request to update a treasure")
                .path("/api/treasures/1")
                .method("PUT")
                .headers(Map.of(
                        "Content-Type", "application/json",
                        "Authorization", "Bearer test-token"
                ))
                .body(requestBody)
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body("""
                {
                  "id": 1,
                  "name": "Golden Chalice",
                  "value": 1500,
                  "description": "An ancient golden chalice with jewels",
                  "location": "Skull Island",
                  "discovered": true
                }
                """)
                .toPact();
    }

    /**
     * Define contract for deleting a treasure
     */
    @Pact(provider = "treasureProvider", consumer = "treasureConsumer")
    public RequestResponsePact deleteTreasurePact(PactDslWithProvider builder) {
        return builder
                .given("treasure with ID 1 exists and user is authorized")
                .uponReceiving("a request to delete a treasure")
                .path("/api/treasures/1")
                .method("DELETE")
                .headers(Map.of("Authorization", "Bearer test-token"))
                .willRespondWith()
                .status(204)
                .toPact();
    }

    /**
     * Test getting all treasures
     */
    @Test
    @PactTestFor(pactMethod = "getAllTreasuresPact")
    @Story("Get All Treasures")
    @Description("Test getting all treasures contract")
    public void testGetAllTreasures(MockServer mockServer) {
        // Setup API client with mock server URL
        String url = setupPactTest(mockServer);
        
        // Execute test
        Response response = RestAssured.given()
                .baseUri(url)
                .when()
                .get("/api/treasures")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        // Verify response
        assertEquals(200, response.getStatusCode());
        assertEquals("application/json", response.getContentType());
        
        // Verify response body
        assertTrue(response.jsonPath().getList("").size() >= 2);
        assertEquals("Golden Chalice", response.jsonPath().getString("[0].name"));
        assertEquals(1000, response.jsonPath().getInt("[0].value"));
    }

    /**
     * Test getting a treasure by ID
     */
    @Test
    @PactTestFor(pactMethod = "getTreasureByIdPact")
    @Story("Get Treasure By ID")
    @Description("Test getting a specific treasure by ID contract")
    public void testGetTreasureById(MockServer mockServer) {
        // Setup API client with mock server URL
        String url = setupPactTest(mockServer);
        
        // Execute test
        Response response = RestAssured.given()
                .baseUri(url)
                .when()
                .get("/api/treasures/1")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        // Verify response
        assertEquals(200, response.getStatusCode());
        assertEquals("application/json", response.getContentType());
        
        // Verify response body
        assertEquals(1, response.jsonPath().getInt("id"));
        assertEquals("Golden Chalice", response.jsonPath().getString("name"));
        assertEquals(1000, response.jsonPath().getInt("value"));
        assertEquals("Skull Island", response.jsonPath().getString("location"));
    }

    /**
     * Test creating a treasure
     */
    @Test
    @PactTestFor(pactMethod = "createTreasurePact")
    @Story("Create Treasure")
    @Description("Test creating a new treasure contract")
    public void testCreateTreasure(MockServer mockServer) {
        // Setup API client with mock server URL
        String url = setupPactTest(mockServer);
        
        // Prepare request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "Diamond Necklace");
        requestBody.put("value", 2000);
        requestBody.put("description", "A necklace with diamonds");
        requestBody.put("location", "Crystal Cave");
        requestBody.put("discovered", false);
        
        // Execute test
        Response response = RestAssured.given()
                .baseUri(url)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer test-token")
                .body(requestBody)
                .when()
                .post("/api/treasures")
                .then()
                .statusCode(201)
                .extract()
                .response();
        
        // Verify response
        assertEquals(201, response.getStatusCode());
        assertEquals("application/json", response.getContentType());
        
        // Verify response body
        assertEquals(3, response.jsonPath().getInt("id"));
        assertEquals("Diamond Necklace", response.jsonPath().getString("name"));
        assertEquals(2000, response.jsonPath().getInt("value"));
    }

    /**
     * Test updating a treasure
     */
    @Test
    @PactTestFor(pactMethod = "updateTreasurePact")
    @Story("Update Treasure")
    @Description("Test updating an existing treasure contract")
    public void testUpdateTreasure(MockServer mockServer) {
        // Setup API client with mock server URL
        String url = setupPactTest(mockServer);
        
        // Prepare request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "Golden Chalice");
        requestBody.put("value", 1500);
        requestBody.put("description", "An ancient golden chalice with jewels");
        requestBody.put("location", "Skull Island");
        requestBody.put("discovered", true);
        
        // Execute test
        Response response = RestAssured.given()
                .baseUri(url)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer test-token")
                .body(requestBody)
                .when()
                .put("/api/treasures/1")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        // Verify response
        assertEquals(200, response.getStatusCode());
        assertEquals("application/json", response.getContentType());
        
        // Verify response body
        assertEquals(1, response.jsonPath().getInt("id"));
        assertEquals("Golden Chalice", response.jsonPath().getString("name"));
        assertEquals(1500, response.jsonPath().getInt("value"));
        assertTrue(response.jsonPath().getBoolean("discovered"));
    }

    /**
     * Test deleting a treasure
     */
    @Test
    @PactTestFor(pactMethod = "deleteTreasurePact")
    @Story("Delete Treasure")
    @Description("Test deleting an existing treasure contract")
    public void testDeleteTreasure(MockServer mockServer) {
        // Setup API client with mock server URL
        String url = setupPactTest(mockServer);
        
        // Execute test
        Response response = RestAssured.given()
                .baseUri(url)
                .header("Authorization", "Bearer test-token")
                .when()
                .delete("/api/treasures/1")
                .then()
                .statusCode(204)
                .extract()
                .response();
        
        // Verify response
        assertEquals(204, response.getStatusCode());
    }
}