package com.peliQAn.framework.api.advanced.wiremock;

import com.github.javafaker.Faker;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wiremock.client.WireMock;
import org.wiremock.stubbing.StubMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.wiremock.client.WireMock.*;

/**
 * Advanced API tests using WireMock for mocking server responses
 */
@Epic("API Tests")
@Feature("WireMock API Mocking")
public class WireMockTest {

    private static final int WIREMOCK_PORT = 8888;
    private static final String WIREMOCK_HOST = "localhost";
    private Faker faker;

    @BeforeClass
    public void setup() {
        // Configure WireMock server
        WireMock.configureFor(WIREMOCK_HOST, WIREMOCK_PORT);
        
        // Reset all previous stubs
        WireMock.reset();
        
        // Set RestAssured base URI to point to WireMock server
        RestAssured.baseURI = "http://" + WIREMOCK_HOST + ":" + WIREMOCK_PORT;
        
        // Initialize faker for test data generation
        faker = new Faker();
    }

    @AfterClass
    public void tearDown() {
        // Reset all stubs
        WireMock.reset();
    }
    
    /**
     * Test: Basic stubbing for GET request
     * Technique: Simple WireMock stub for GET endpoint
     */
    @Test(description = "Basic GET request stubbing")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Basic WireMock Stubbing")
    @Description("Test demonstrates basic WireMock stubbing for GET requests")
    public void testBasicGetStubbing() {
        // Create a stub
        stubFor(get(urlEqualTo("/api/treasures/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"id\": 1,\n" +
                                "  \"name\": \"Golden Crown\",\n" +
                                "  \"value\": 5000,\n" +
                                "  \"description\": \"A valuable golden crown\",\n" +
                                "  \"location\": \"Royal Palace\",\n" +
                                "  \"discovered\": false\n" +
                                "}")));
        
        // Make request to stubbed endpoint
        Response response = given()
                .when()
                .get("/api/treasures/1")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        // Verify response
        assertEquals(response.getStatusCode(), 200);
        assertEquals(response.jsonPath().getString("name"), "Golden Crown");
        assertEquals(response.jsonPath().getInt("value"), 5000);
        
        // Verify that our stub was called
        verify(getRequestedFor(urlEqualTo("/api/treasures/1")));
    }
    
    /**
     * Test: Stubbing POST request with request body matching
     * Technique: WireMock request body matching
     */
    @Test(description = "POST request stubbing with request body matching")
    @Severity(SeverityLevel.HIGH)
    @Story("Request Body Matching")
    @Description("Test demonstrates WireMock stubbing with request body matching")
    public void testPostRequestWithBodyMatching() {
        // Create a stub with request body matching
        stubFor(post(urlEqualTo("/api/treasures"))
                .withHeader("Content-Type", containing("application/json"))
                .withRequestBody(matchingJsonPath("$.name", equalTo("Diamond Ring")))
                .withRequestBody(matchingJsonPath("$.value", matching("[0-9]+")))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"id\": 10,\n" +
                                "  \"name\": \"Diamond Ring\",\n" +
                                "  \"value\": 2000,\n" +
                                "  \"description\": \"A sparkling diamond ring\",\n" +
                                "  \"location\": \"Jewelry Box\",\n" +
                                "  \"discovered\": false\n" +
                                "}")));
        
        // Prepare request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "Diamond Ring");
        requestBody.put("value", 2000);
        requestBody.put("description", "A sparkling diamond ring");
        requestBody.put("location", "Jewelry Box");
        requestBody.put("discovered", false);
        
        // Make request to stubbed endpoint
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/treasures")
                .then()
                .statusCode(201)
                .extract()
                .response();
        
        // Verify response
        assertEquals(response.getStatusCode(), 201);
        assertEquals(response.jsonPath().getInt("id"), 10);
        assertEquals(response.jsonPath().getString("name"), "Diamond Ring");
        
        // Verify that our stub was called
        verify(postRequestedFor(urlEqualTo("/api/treasures"))
                .withRequestBody(matchingJsonPath("$.name")));
    }
    
    /**
     * Test: Simulating different HTTP status codes
     * Technique: WireMock response status codes
     */
    @Test(description = "Simulating different HTTP status codes")
    @Severity(SeverityLevel.NORMAL)
    @Story("HTTP Status Simulation")
    @Description("Test demonstrates stubbing different HTTP status code responses")
    public void testDifferentHttpStatusCodes() {
        // 404 Not Found
        stubFor(get(urlEqualTo("/api/treasures/999"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"status\": 404,\n" +
                                "  \"message\": \"Treasure not found\"\n" +
                                "}")));
        
        // 400 Bad Request
        stubFor(post(urlEqualTo("/api/treasures"))
                .withRequestBody(matchingJsonPath("$.name", equalTo("")))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"status\": 400,\n" +
                                "  \"message\": \"Name cannot be empty\"\n" +
                                "}")));
        
        // 401 Unauthorized
        stubFor(get(urlEqualTo("/api/treasures/secure"))
                .withHeader("Authorization", absent())
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"status\": 401,\n" +
                                "  \"message\": \"Unauthorized access\"\n" +
                                "}")));
        
        // 403 Forbidden
        stubFor(delete(urlEqualTo("/api/treasures/1"))
                .withHeader("Authorization", matching("Bearer .*"))
                .withHeader("Role", absent())
                .willReturn(aResponse()
                        .withStatus(403)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"status\": 403,\n" +
                                "  \"message\": \"Forbidden: Insufficient permissions\"\n" +
                                "}")));
        
        // 500 Internal Server Error
        stubFor(get(urlEqualTo("/api/treasures/error"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"status\": 500,\n" +
                                "  \"message\": \"Internal server error\"\n" +
                                "}")));
        
        // Test 404
        Response notFoundResponse = given()
                .when()
                .get("/api/treasures/999")
                .then()
                .statusCode(404)
                .extract()
                .response();
        
        assertEquals(notFoundResponse.getStatusCode(), 404);
        assertEquals(notFoundResponse.jsonPath().getString("message"), "Treasure not found");
        
        // Test 400
        Map<String, Object> invalidRequest = new HashMap<>();
        invalidRequest.put("name", "");
        invalidRequest.put("value", 100);
        
        Response badRequestResponse = given()
                .contentType(ContentType.JSON)
                .body(invalidRequest)
                .when()
                .post("/api/treasures")
                .then()
                .statusCode(400)
                .extract()
                .response();
        
        assertEquals(badRequestResponse.getStatusCode(), 400);
        assertEquals(badRequestResponse.jsonPath().getString("message"), "Name cannot be empty");
        
        // Test 401
        Response unauthorizedResponse = given()
                .when()
                .get("/api/treasures/secure")
                .then()
                .statusCode(401)
                .extract()
                .response();
        
        assertEquals(unauthorizedResponse.getStatusCode(), 401);
        assertEquals(unauthorizedResponse.jsonPath().getString("message"), "Unauthorized access");
        
        // Test 403
        Response forbiddenResponse = given()
                .header("Authorization", "Bearer fake-token")
                .when()
                .delete("/api/treasures/1")
                .then()
                .statusCode(403)
                .extract()
                .response();
        
        assertEquals(forbiddenResponse.getStatusCode(), 403);
        assertEquals(forbiddenResponse.jsonPath().getString("message"), "Forbidden: Insufficient permissions");
        
        // Test 500
        Response serverErrorResponse = given()
                .when()
                .get("/api/treasures/error")
                .then()
                .statusCode(500)
                .extract()
                .response();
        
        assertEquals(serverErrorResponse.getStatusCode(), 500);
        assertEquals(serverErrorResponse.jsonPath().getString("message"), "Internal server error");
    }
    
    /**
     * Test: Simulating network delays
     * Technique: WireMock response delays
     */
    @Test(description = "Simulating network delays")
    @Severity(SeverityLevel.LOW)
    @Story("Network Delay Simulation")
    @Description("Test demonstrates simulating network delays with WireMock")
    public void testNetworkDelays() {
        // Stub with fixed delay
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
        
        // Stub with random delay
        stubFor(get(urlEqualTo("/api/treasures/variable"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"id\": 6,\n" +
                                "  \"name\": \"Variable Treasure\",\n" +
                                "  \"value\": 2000\n" +
                                "}")
                        .withUniformRandomDelay(1000, 3000))); // 1-3 second random delay
        
        // Test fixed delay
        long startTime = System.currentTimeMillis();
        
        Response fixedDelayResponse = given()
                .when()
                .get("/api/treasures/slow")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertEquals(fixedDelayResponse.getStatusCode(), 200);
        assertEquals(fixedDelayResponse.jsonPath().getString("name"), "Slow Treasure");
        assertTrue(duration >= 2000, "Response should take at least 2 seconds");
        
        // Test random delay
        startTime = System.currentTimeMillis();
        
        Response randomDelayResponse = given()
                .when()
                .get("/api/treasures/variable")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        endTime = System.currentTimeMillis();
        duration = endTime - startTime;
        
        assertEquals(randomDelayResponse.getStatusCode(), 200);
        assertEquals(randomDelayResponse.jsonPath().getString("name"), "Variable Treasure");
        assertTrue(duration >= 1000, "Response should take at least 1 second");
    }
    
    /**
     * Test: Simulating stateful behavior
     * Technique: WireMock stateful behavior using scenarios
     */
    @Test(description = "Simulating stateful behavior")
    @Severity(SeverityLevel.HIGH)
    @Story("Stateful Behavior")
    @Description("Test demonstrates simulating stateful behavior with WireMock scenarios")
    public void testStatefulBehavior() {
        // Initial state: Treasure not discovered
        stubFor(get(urlEqualTo("/api/treasures/stateful/1"))
                .inScenario("Treasure Discovery")
                .whenScenarioStateIs(WireMock.STARTED)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"id\": 1,\n" +
                                "  \"name\": \"Hidden Treasure\",\n" +
                                "  \"value\": 9000,\n" +
                                "  \"description\": \"A hidden treasure chest\",\n" +
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
                                "  \"name\": \"Hidden Treasure\",\n" +
                                "  \"value\": 9000,\n" +
                                "  \"description\": \"A hidden treasure chest\",\n" +
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
                                "  \"name\": \"Hidden Treasure\",\n" +
                                "  \"value\": 9000,\n" +
                                "  \"description\": \"A hidden treasure chest\",\n" +
                                "  \"location\": \"Secret Cave\",\n" +
                                "  \"discovered\": true\n" +
                                "}")));
        
        // Test initial state
        Response initialResponse = given()
                .when()
                .get("/api/treasures/stateful/1")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        assertEquals(initialResponse.getStatusCode(), 200);
        assertEquals(initialResponse.jsonPath().getString("name"), "Hidden Treasure");
        assertEquals(initialResponse.jsonPath().getBoolean("discovered"), false);
        
        // Perform transition
        Response transitionResponse = given()
                .when()
                .put("/api/treasures/stateful/1/discover")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        assertEquals(transitionResponse.getStatusCode(), 200);
        assertEquals(transitionResponse.jsonPath().getBoolean("discovered"), true);
        
        // Test final state
        Response finalResponse = given()
                .when()
                .get("/api/treasures/stateful/1")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        assertEquals(finalResponse.getStatusCode(), 200);
        assertEquals(finalResponse.jsonPath().getString("name"), "Hidden Treasure");
        assertEquals(finalResponse.jsonPath().getBoolean("discovered"), true);
    }
    
    /**
     * Test: Response templating
     * Technique: WireMock response templating using Handlebars
     */
    @Test(description = "Response templating")
    @Severity(SeverityLevel.NORMAL)
    @Story("Response Templating")
    @Description("Test demonstrates response templating with WireMock")
    public void testResponseTemplating() {
        // Define a stub with response templating
        stubFor(post(urlEqualTo("/api/treasures/template"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withTransformers("response-template")
                        .withBody("{\n" +
                                "  \"id\": {{randomValue type='UUID'}},\n" +
                                "  \"name\": \"{{jsonPath request.body '$.name'}}\",\n" +
                                "  \"value\": {{jsonPath request.body '$.value'}},\n" +
                                "  \"description\": \"{{jsonPath request.body '$.description'}}\",\n" +
                                "  \"created\": \"{{now}}\",\n" +
                                "  \"requestUrl\": \"{{request.url}}\"\n" +
                                "}")));
        
        // Prepare request body
        String treasureName = faker.ancient().hero();
        int treasureValue = faker.number().numberBetween(500, 10000);
        String treasureDescription = faker.lorem().sentence();
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", treasureName);
        requestBody.put("value", treasureValue);
        requestBody.put("description", treasureDescription);
        
        // Make request to stubbed endpoint
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/treasures/template")
                .then()
                .statusCode(201)
                .extract()
                .response();
        
        // Verify response
        assertEquals(response.getStatusCode(), 201);
        assertEquals(response.jsonPath().getString("name"), treasureName);
        assertEquals(response.jsonPath().getInt("value"), treasureValue);
        assertEquals(response.jsonPath().getString("description"), treasureDescription);
        assertTrue(response.jsonPath().getString("id").matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
        assertEquals(response.jsonPath().getString("requestUrl"), "/api/treasures/template");
    }
    
    /**
     * Test: Request matching with multiple conditions
     * Technique: WireMock advanced request matching
     */
    @Test(description = "Request matching with multiple conditions")
    @Severity(SeverityLevel.NORMAL)
    @Story("Advanced Request Matching")
    @Description("Test demonstrates complex request matching with WireMock")
    public void testAdvancedRequestMatching() {
        // Define stub with multiple matching conditions
        stubFor(get(urlPathMatching("/api/treasures/search/.*"))
                .withQueryParam("name", containing("gold"))
                .withQueryParam("minValue", matching("[0-9]+"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Authorization", matching("Bearer .*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\n" +
                                "  {\n" +
                                "    \"id\": 1,\n" +
                                "    \"name\": \"Golden Crown\",\n" +
                                "    \"value\": 5000\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"id\": 2,\n" +
                                "    \"name\": \"Gold Necklace\",\n" +
                                "    \"value\": 2500\n" +
                                "  }\n" +
                                "]")));
        
        // Make request to stubbed endpoint
        Response response = given()
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + UUID.randomUUID().toString())
                .queryParam("name", "gold")
                .queryParam("minValue", "1000")
                .when()
                .get("/api/treasures/search/advanced")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        // Verify response
        assertEquals(response.getStatusCode(), 200);
        assertEquals(response.jsonPath().getList("").size(), 2);
        assertEquals(response.jsonPath().getString("[0].name"), "Golden Crown");
        assertEquals(response.jsonPath().getString("[1].name"), "Gold Necklace");
        
        // Verify stub was called
        verify(getRequestedFor(urlPathMatching("/api/treasures/search/.*"))
                .withQueryParam("name", containing("gold"))
                .withQueryParam("minValue", matching("[0-9]+")));
    }
    
    /**
     * Test: Fault simulation
     * Technique: WireMock fault injection
     */
    @Test(description = "Fault simulation")
    @Severity(SeverityLevel.HIGH)
    @Story("Fault Injection")
    @Description("Test demonstrates fault simulation with WireMock")
    public void testFaultSimulation() {
        // Connection reset fault
        stubFor(get(urlEqualTo("/api/treasures/fault/connection-reset"))
                .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));
        
        // Malformed response chunk
        stubFor(get(urlEqualTo("/api/treasures/fault/malformed-response"))
                .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
        
        // Random data then close
        stubFor(get(urlEqualTo("/api/treasures/fault/random-data"))
                .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));
        
        // Empty response
        stubFor(get(urlEqualTo("/api/treasures/fault/empty-response"))
                .willReturn(aResponse().withStatus(200).withBody("")));
        
        // Malformed JSON
        stubFor(get(urlEqualTo("/api/treasures/fault/malformed-json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"name\": \"Broken Treasure\", \"value\": 100, ")));
        
        // Test empty response
        Response emptyResponse = given()
                .when()
                .get("/api/treasures/fault/empty-response")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        assertEquals(emptyResponse.getStatusCode(), 200);
        assertEquals(emptyResponse.getBody().asString(), "");
        
        // Test malformed JSON
        try {
            Response malformedJsonResponse = given()
                    .when()
                    .get("/api/treasures/fault/malformed-json")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();
            
            // This should throw an exception when we try to parse it
            malformedJsonResponse.jsonPath().get("name");
        } catch (Exception e) {
            // Expected exception for malformed JSON
            assertTrue(e.getMessage().contains("Failed to parse"));
        }
        
        // Note: The other fault simulations would throw exceptions at the HTTP client level
        // and are difficult to test in automated tests, but they're useful for manual testing
    }
    
    /**
     * Test: Proxying to another service
     * Technique: WireMock proxy configuration
     */
    @Test(description = "Proxying to another service")
    @Severity(SeverityLevel.LOW)
    @Story("Service Proxying")
    @Description("Test demonstrates proxying requests to another service with WireMock")
    public void testProxyingRequests() {
        // First create a target stub
        stubFor(get(urlEqualTo("/actual-api/treasures"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("X-Source", "actual-api")
                        .withBody("[\n" +
                                "  {\n" +
                                "    \"id\": 1,\n" +
                                "    \"name\": \"Original Treasure\",\n" +
                                "    \"value\": 5000\n" +
                                "  }\n" +
                                "]")));
        
        // Then create a proxy stub that forwards to our target stub
        stubFor(get(urlEqualTo("/proxy-api/treasures"))
                .willReturn(aResponse()
                        .proxiedFrom("http://" + WIREMOCK_HOST + ":" + WIREMOCK_PORT)
                        .transformers("response-template")
                        .withHeader("X-Proxy", "true")
                        .transformResponseHeader("X-Source", "proxied-{{value}}")
                        .withProxyUrl("http://" + WIREMOCK_HOST + ":" + WIREMOCK_PORT + "/actual-api")));
        
        // Make request to the proxy endpoint
        Response response = given()
                .when()
                .get("/proxy-api/treasures")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        // Verify response
        assertEquals(response.getStatusCode(), 200);
        assertEquals(response.getHeader("X-Proxy"), "true");
        assertTrue(response.getHeader("X-Source").contains("proxied-"));
        assertEquals(response.jsonPath().getString("[0].name"), "Original Treasure");
        
        // Verify both stubs were called
        verify(getRequestedFor(urlEqualTo("/proxy-api/treasures")));
        verify(getRequestedFor(urlEqualTo("/actual-api/treasures")));
    }
    
    /**
     * Test: Dynamic response based on request
     * Technique: Advanced WireMock response customization
     */
    @Test(description = "Dynamic response based on request")
    @Severity(SeverityLevel.NORMAL)
    @Story("Dynamic Responses")
    @Description("Test demonstrates generating dynamic responses based on request parameters")
    public void testDynamicResponse() {
        // Create a stub that returns different responses based on the ID
        StubMapping stubMapping = stubFor(get(urlMatching("/api/treasures/dynamic/([0-9]+)"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withTransformers("response-template")
                        .withBody("{\n" +
                                "  \"id\": {{request.pathSegments.[3]}},\n" +
                                "  \"name\": \"Treasure {{request.pathSegments.[3]}}\",\n" +
                                "  \"value\": {{multiply request.pathSegments.[3] 1000}},\n" +
                                "  \"requestTime\": \"{{now}}\",\n" +
                                "  \"requestId\": \"{{request.id}}\"\n" +
                                "}")));
        
        // Test with different IDs
        for (int id = 1; id <= 3; id++) {
            Response response = given()
                    .when()
                    .get("/api/treasures/dynamic/" + id)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();
            
            assertEquals(response.getStatusCode(), 200);
            assertEquals(response.jsonPath().getInt("id"), id);
            assertEquals(response.jsonPath().getString("name"), "Treasure " + id);
            assertEquals(response.jsonPath().getInt("value"), id * 1000);
            assertTrue(response.jsonPath().getString("requestTime").length() > 0);
            assertTrue(response.jsonPath().getString("requestId").length() > 0);
        }
        
        // Verify stub was called multiple times
        verify(3, getRequestedFor(urlMatching("/api/treasures/dynamic/([0-9]+)")));
    }
}