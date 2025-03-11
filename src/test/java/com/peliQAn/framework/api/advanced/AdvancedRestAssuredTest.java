package com.peliQAn.framework.api.advanced;

import com.peliQAn.framework.api.AuthApiClient;
import com.peliQAn.framework.api.TestCasesApiClient;
import com.peliQAn.framework.api.TreasureApiClient;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.io.FileUtils;
import org.hamcrest.MatcherAssert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * Advanced RestAssured Tests demonstrating various techniques
 */
@Epic("API Tests")
@Feature("Advanced RestAssured Techniques")
public class AdvancedRestAssuredTest {

    private TreasureApiClient treasureApiClient;
    private AuthApiClient authApiClient;
    private TestCasesApiClient testCasesApiClient;
    private String authToken;
    private RequestSpecification requestSpec;
    private ResponseSpecification responseSpec;

    @BeforeClass
    public void setup() {
        treasureApiClient = new TreasureApiClient();
        authApiClient = new AuthApiClient();
        testCasesApiClient = new TestCasesApiClient();
        
        // Register and login to get auth token
        String username = "advuser_" + System.currentTimeMillis();
        String email = "advuser_" + System.currentTimeMillis() + "@example.com";
        String password = "Password123";
        
        authToken = authApiClient.registerAndLogin(username, email, password);
        treasureApiClient.setAuthToken(authToken);
        testCasesApiClient.setAuthToken(authToken);
        
        // Setup base request and response specifications
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(RestAssured.baseURI)
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "Bearer " + authToken)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
                
        responseSpec = new ResponseSpecBuilder()
                .expectStatusCode(anyOf(is(200), is(201), is(204)))
                .expectContentType(ContentType.JSON)
                .build();
    }

    /**
     * Test: JSON Schema Validation
     * Technique: Validates API responses against JSON schema
     */
    @Test(description = "Validate treasure API response against JSON schema")
    @Severity(SeverityLevel.CRITICAL)
    @Story("JSON Schema Validation")
    @Description("Test validates that the treasure API responses conform to a defined JSON schema")
    public void testJsonSchemaValidation() {
        // Create a treasure to ensure we have data
        Map<String, Object> treasureData = new HashMap<>();
        treasureData.put("name", "Diamond Crown");
        treasureData.put("value", 5000);
        treasureData.put("description", "A crown with diamonds");
        treasureData.put("location", "Treasure Island");
        treasureData.put("discovered", false);
        
        Response createResponse = treasureApiClient.createTreasure(treasureData);
        assertEquals(createResponse.getStatusCode(), 201, "Treasure creation should succeed");
        
        // Define the schema (typically this would be stored in a file)
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
        
        // Retrieve and validate
        Map<String, Object> createdTreasure = createResponse.as(Map.class);
        Long treasureId = Long.valueOf(createdTreasure.get("id").toString());
        
        given()
            .spec(requestSpec)
        .when()
            .get("/api/treasures/" + treasureId)
        .then()
            .assertThat()
            .body(JsonSchemaValidator.matchesJsonSchema(treasureSchema));
        
        // Clean up
        treasureApiClient.deleteTreasure(treasureId);
    }

    /**
     * Test: Response time assertions
     * Technique: Validates API response times
     */
    @Test(description = "Test API response time assertions")
    @Severity(SeverityLevel.NORMAL)
    @Story("Response Time Assertions")
    @Description("Test validates that API endpoints respond within acceptable time limits")
    public void testResponseTimeAssertions() {
        Response response = given()
            .spec(requestSpec)
        .when()
            .get("/api/treasures")
        .then()
            .time(lessThan(3000L)) // 3 seconds
            .extract()
            .response();
        
        // Additional time assertions using different time units
        long responseTimeMs = response.getTimeIn(TimeUnit.MILLISECONDS);
        long responseTimeSec = response.getTimeIn(TimeUnit.SECONDS);
        
        assertTrue(responseTimeMs < 3000, "Response time should be less than 3000ms");
        assertTrue(responseTimeSec < 3, "Response time should be less than 3 seconds");
        
        // Log the actual response time
        System.out.println("Treasure API response time: " + responseTimeMs + "ms");
    }

    /**
     * Test: Path parameters with parameterized tests
     * Technique: Demonstrates using DataProvider with PathParams
     */
    @DataProvider(name = "treasureIds")
    public Object[][] treasureIds() {
        // Create some treasures for testing
        List<Long> ids = new ArrayList<>();
        
        Map<String, Object> treasure1 = treasureApiClient.createTreasureHelper(
                "Gold Coin", 100, "A shiny gold coin", "Pirate Bay", false);
        ids.add(Long.valueOf(treasure1.get("id").toString()));
        
        Map<String, Object> treasure2 = treasureApiClient.createTreasureHelper(
                "Silver Coin", 50, "A polished silver coin", "Smuggler's Cove", true);
        ids.add(Long.valueOf(treasure2.get("id").toString()));
        
        return new Object[][]{
            {ids.get(0), "Gold Coin"},
            {ids.get(1), "Silver Coin"}
        };
    }
    
    @Test(description = "Test path parameters with parameterized tests", 
          dataProvider = "treasureIds")
    @Severity(SeverityLevel.NORMAL)
    @Story("Parameterized Tests")
    @Description("Test demonstrates using DataProvider with path parameters")
    public void testPathParametersWithDataProvider(Long treasureId, String expectedName) {
        given()
            .spec(requestSpec)
            .pathParam("id", treasureId)
        .when()
            .get("/api/treasures/{id}")
        .then()
            .spec(responseSpec)
            .body("name", equalTo(expectedName));
        
        // Clean up after all tests are done (would be better in an @AfterClass)
        treasureApiClient.deleteTreasure(treasureId);
    }

    /**
     * Test: Request/Response Filtering and Logging
     * Technique: Custom filters for request/response manipulation
     */
    @Test(description = "Test request and response filtering")
    @Severity(SeverityLevel.LOW)
    @Story("Request/Response Filtering")
    @Description("Test demonstrates using filters to log and manipulate requests/responses")
    public void testRequestResponseFiltering() {
        // Add custom filter for this specific test
        RequestSpecification filteredSpec = given()
            .spec(requestSpec)
            .filter((requestSpec, responseSpec, ctx) -> {
                // Before request is sent
                System.out.println("=== CUSTOM FILTER: Before sending request ===");
                
                // Continue with the request and get the response
                Response response = ctx.next(requestSpec, responseSpec);
                
                // After response is received
                System.out.println("=== CUSTOM FILTER: After receiving response ===");
                System.out.println("Response Status: " + response.getStatusCode());
                
                // You can modify the response if needed
                return response;
            });
        
        // Make request with the filtered spec
        Response response = filteredSpec
            .when()
            .get("/api/treasures")
            .then()
            .extract()
            .response();
        
        assertEquals(response.getStatusCode(), 200, "Status code should be 200");
    }

    /**
     * Test: Complex JSON path expressions
     * Technique: Advanced JsonPath queries
     */
    @Test(description = "Test complex JSON path expressions")
    @Severity(SeverityLevel.NORMAL)
    @Story("Complex JsonPath")
    @Description("Test demonstrates using complex JsonPath expressions for data extraction")
    public void testComplexJsonPathExpressions() {
        // Create multiple treasures with different values
        Map<String, Object> treasure1 = treasureApiClient.createTreasureHelper(
                "Expensive Crown", 10000, "A very expensive crown", "Royal Island", false);
        
        Map<String, Object> treasure2 = treasureApiClient.createTreasureHelper(
                "Cheap Necklace", 200, "A cheap necklace", "Poor Island", false);
        
        Map<String, Object> treasure3 = treasureApiClient.createTreasureHelper(
                "Moderate Ring", 1000, "A moderately priced ring", "Middle Island", true);
        
        // Get all treasures
        Response response = given()
            .spec(requestSpec)
        .when()
            .get("/api/treasures")
        .then()
            .extract()
            .response();
        
        // Use JsonPath for complex queries
        JsonPath jsonPath = response.jsonPath();
        
        // Find treasures with value > 5000
        List<Map<String, Object>> expensiveTreasures = jsonPath.getList("findAll { treasure -> treasure.value > 5000 }");
        assertTrue(expensiveTreasures.size() >= 1, "Should find at least one expensive treasure");
        
        // Find treasures with value < 500
        List<Map<String, Object>> cheapTreasures = jsonPath.getList("findAll { treasure -> treasure.value < 500 }");
        assertTrue(cheapTreasures.size() >= 1, "Should find at least one cheap treasure");
        
        // Find discovered treasures
        List<Map<String, Object>> discoveredTreasures = jsonPath.getList("findAll { it.discovered == true }");
        assertTrue(discoveredTreasures.size() >= 1, "Should find at least one discovered treasure");
        
        // Find treasure with a specific name
        Map<String, Object> foundTreasure = jsonPath.getMap("find { it.name == 'Expensive Crown' }");
        assertNotNull(foundTreasure, "Should find the treasure by name");
        assertEquals(10000, ((Number)foundTreasure.get("value")).intValue(), "Value should match");
        
        // Clean up
        treasureApiClient.deleteTreasure(Long.valueOf(treasure1.get("id").toString()));
        treasureApiClient.deleteTreasure(Long.valueOf(treasure2.get("id").toString()));
        treasureApiClient.deleteTreasure(Long.valueOf(treasure3.get("id").toString()));
    }

    /**
     * Test: Sequential requests with data dependencies
     * Technique: Chain API calls with data from previous responses
     */
    @Test(description = "Test sequential API requests with data dependencies")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Sequential Requests")
    @Description("Test demonstrates chaining API calls with data dependencies")
    public void testSequentialRequestsWithDependencies() {
        // Step 1: Create a treasure
        Map<String, Object> treasureData = new HashMap<>();
        treasureData.put("name", "Magic Map");
        treasureData.put("value", 3000);
        treasureData.put("description", "A map to hidden treasures");
        treasureData.put("location", "Captain's Cabin");
        treasureData.put("discovered", false);
        
        Response createResponse = given()
            .spec(requestSpec)
            .body(treasureData)
        .when()
            .post("/api/treasures")
        .then()
            .statusCode(201)
            .extract()
            .response();
        
        Long treasureId = createResponse.jsonPath().getLong("id");
        
        // Step 2: Mark the treasure as discovered
        Map<String, Object> updateData = new HashMap<>(treasureData);
        updateData.put("discovered", true);
        
        Response updateResponse = given()
            .spec(requestSpec)
            .body(updateData)
        .when()
            .put("/api/treasures/" + treasureId)
        .then()
            .statusCode(200)
            .body("discovered", is(true))
            .extract()
            .response();
        
        // Step 3: Get all discovered treasures and verify our treasure is included
        Response discoveredResponse = given()
            .spec(requestSpec)
            .queryParam("discovered", true)
        .when()
            .get("/api/treasures")
        .then()
            .statusCode(200)
            .extract()
            .response();
        
        List<Map<String, Object>> discoveredTreasures = discoveredResponse.jsonPath().getList("");
        boolean found = discoveredTreasures.stream()
                .anyMatch(t -> t.get("id").equals(treasureId));
        
        assertTrue(found, "The updated treasure should be in the discovered treasures list");
        
        // Step 4: Delete the treasure and verify it's gone
        given()
            .spec(requestSpec)
        .when()
            .delete("/api/treasures/" + treasureId)
        .then()
            .statusCode(204);
        
        given()
            .spec(requestSpec)
        .when()
            .get("/api/treasures/" + treasureId)
        .then()
            .statusCode(404);
    }

    /**
     * Test: Query parameters and filters
     * Technique: Complex query parameter handling
     */
    @Test(description = "Test query parameters and filtering")
    @Severity(SeverityLevel.NORMAL)
    @Story("Query Parameters")
    @Description("Test demonstrates handling complex query parameters for filtering")
    public void testQueryParametersAndFiltering() {
        // Create test treasures with different properties
        Map<String, Object> treasure1 = treasureApiClient.createTreasureHelper(
                "Golden Compass", 1500, "A magical golden compass", "Northern Island", false);
        
        Map<String, Object> treasure2 = treasureApiClient.createTreasureHelper(
                "Golden Map", 1200, "A golden map to treasures", "Eastern Island", false);
        
        Map<String, Object> treasure3 = treasureApiClient.createTreasureHelper(
                "Silver Telescope", 800, "A silver telescope", "Western Island", true);
        
        // Test filter by name (contains)
        Response nameResponse = given()
            .spec(requestSpec)
            .queryParam("name", "Golden")
        .when()
            .get("/api/treasures/search")
        .then()
            .statusCode(200)
            .extract()
            .response();
        
        List<Map<String, Object>> goldenTreasures = nameResponse.jsonPath().getList("");
        assertTrue(goldenTreasures.size() >= 2, "Should find at least two treasures with 'Golden' in name");
        assertTrue(goldenTreasures.stream().allMatch(t -> t.get("name").toString().contains("Golden")),
                "All results should contain 'Golden' in name");
        
        // Test filter by value (min/max)
        Response valueResponse = given()
            .spec(requestSpec)
            .queryParam("minValue", 1000)
            .queryParam("maxValue", 2000)
        .when()
            .get("/api/treasures")
        .then()
            .statusCode(200)
            .extract()
            .response();
        
        List<Map<String, Object>> valueTreasures = valueResponse.jsonPath().getList("");
        assertTrue(valueTreasures.stream()
                .allMatch(t -> ((Number)t.get("value")).intValue() >= 1000 
                        && ((Number)t.get("value")).intValue() <= 2000),
                "All results should have values between 1000 and 2000");
        
        // Test multiple filter combination
        Response combinedResponse = given()
            .spec(requestSpec)
            .queryParam("name", "Golden")
            .queryParam("location", "Northern")
            .queryParam("discovered", false)
        .when()
            .get("/api/treasures/search")
        .then()
            .statusCode(200)
            .extract()
            .response();
        
        List<Map<String, Object>> combinedResults = combinedResponse.jsonPath().getList("");
        assertTrue(combinedResults.stream()
                .allMatch(t -> t.get("name").toString().contains("Golden") 
                        && t.get("location").toString().contains("Northern")
                        && !(Boolean)t.get("discovered")),
                "Results should match all filter criteria");
        
        // Clean up
        treasureApiClient.deleteTreasure(Long.valueOf(treasure1.get("id").toString()));
        treasureApiClient.deleteTreasure(Long.valueOf(treasure2.get("id").toString()));
        treasureApiClient.deleteTreasure(Long.valueOf(treasure3.get("id").toString()));
    }

    /**
     * Test: Error response handling
     * Technique: Advanced error handling and response validation
     */
    @Test(description = "Test error responses and validation")
    @Severity(SeverityLevel.HIGH)
    @Story("Error Handling")
    @Description("Test demonstrates handling and validating error responses")
    public void testErrorResponseHandling() {
        // Test case 1: Invalid resource (404)
        Response notFoundResponse = given()
            .spec(requestSpec)
        .when()
            .get("/api/treasures/99999")
        .then()
            .statusCode(404)
            .contentType(ContentType.JSON)
            .extract()
            .response();
        
        // Validate error structure
        assertTrue(notFoundResponse.jsonPath().getString("message").contains("not found"),
                "Error message should indicate resource not found");
        
        // Test case 2: Validation error (400)
        Map<String, Object> invalidData = new HashMap<>();
        invalidData.put("name", ""); // Empty name
        invalidData.put("value", -100); // Negative value
        
        Response validationResponse = given()
            .spec(requestSpec)
            .body(invalidData)
        .when()
            .post("/api/treasures")
        .then()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .extract()
            .response();
        
        // Validate validation error structure
        assertTrue(validationResponse.jsonPath().getString("message").contains("validation"),
                "Error should indicate validation failure");
        
        // Test case 3: Unauthorized access (401/403)
        // First create a treasure to attempt to update without auth
        Map<String, Object> treasure = treasureApiClient.createTreasureHelper(
                "Auth Test Treasure", 100, "Test treasure", "Auth Island", false);
        
        Response unauthorizedResponse = given()
            .contentType(ContentType.JSON)
            .body(treasure)
        .when()
            .put("/api/treasures/" + treasure.get("id"))
        .then()
            .statusCode(anyOf(is(401), is(403)))
            .extract()
            .response();
        
        // Validate auth error
        assertTrue(unauthorizedResponse.jsonPath().getString("message").contains("auth") ||
                unauthorizedResponse.jsonPath().getString("message").contains("token") ||
                unauthorizedResponse.jsonPath().getString("message").contains("unauthorized"),
                "Error should indicate authentication/authorization issue");
        
        // Clean up
        treasureApiClient.deleteTreasure(Long.valueOf(treasure.get("id").toString()));
    }

    /**
     * Test: File upload and download
     * Technique: Multipart requests and file handling
     */
    @Test(description = "Test file upload and download")
    @Severity(SeverityLevel.NORMAL)
    @Story("File Operations")
    @Description("Test demonstrates file upload and download operations")
    public void testFileUploadAndDownload() throws IOException {
        // Create a temporary file to upload
        File tempFile = File.createTempFile("treasure-map-", ".txt");
        FileUtils.writeStringToFile(tempFile, "X marks the spot!", StandardCharsets.UTF_8);
        
        // Upload the file
        Response uploadResponse = given()
            .spec(requestSpec)
            .multiPart("file", tempFile)
            .contentType("multipart/form-data")
        .when()
            .post("/api/test/upload")
        .then()
            .statusCode(anyOf(is(200), is(201)))
            .extract()
            .response();
        
        // Get file identifier from response
        String fileId = uploadResponse.jsonPath().getString("fileId");
        assertNotNull(fileId, "File ID should not be null");
        
        // Download the file
        Response downloadResponse = given()
            .spec(requestSpec)
        .when()
            .get("/api/test/download/" + fileId)
        .then()
            .statusCode(200)
            .extract()
            .response();
        
        // Verify content
        String downloadedContent = downloadResponse.asString();
        assertEquals("X marks the spot!", downloadedContent, "Downloaded content should match uploaded content");
        
        // Clean up
        tempFile.delete();
    }

    /**
     * Test: Concurrent API requests
     * Technique: Parallel API testing
     */
    @Test(description = "Test concurrent API requests")
    @Severity(SeverityLevel.HIGH)
    @Story("Concurrent Requests")
    @Description("Test demonstrates making concurrent API requests")
    public void testConcurrentApiRequests() {
        // Create a treasure first
        Map<String, Object> treasure = treasureApiClient.createTreasureHelper(
                "Concurrency Test Treasure", 500, "Test for concurrency", "Thread Island", false);
        Long treasureId = Long.valueOf(treasure.get("id").toString());
        
        // Make multiple concurrent requests
        List<Thread> threads = new ArrayList<>();
        List<Response> responses = Collections.synchronizedList(new ArrayList<>());
        
        // Create 5 threads for concurrent GET requests
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(() -> {
                Response response = given()
                    .spec(requestSpec)
                .when()
                    .get("/api/treasures/" + treasureId)
                .then()
                    .extract()
                    .response();
                
                responses.add(response);
            });
            
            threads.add(thread);
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // Verify all responses
        assertEquals(5, responses.size(), "Should have 5 responses");
        for (Response response : responses) {
            assertEquals(200, response.getStatusCode(), "All responses should return 200");
            assertEquals(treasureId, response.jsonPath().getLong("id"), "All responses should return the correct treasure");
        }
        
        // Clean up
        treasureApiClient.deleteTreasure(treasureId);
    }
    
    /**
     * Test: Rate limiting and throttling
     * Technique: Testing API rate limits
     */
    @Test(description = "Test API rate limiting")
    @Severity(SeverityLevel.LOW)
    @Story("Rate Limiting")
    @Description("Test demonstrates handling API rate limiting")
    public void testRateLimiting() {
        // Make rapid requests to potentially trigger rate limiting
        List<Response> responses = new ArrayList<>();
        
        for (int i = 0; i < 20; i++) {
            Response response = given()
                .spec(requestSpec)
            .when()
                .get("/api/treasures")
            .then()
                .extract()
                .response();
            
            responses.add(response);
            
            // Don't sleep if we've already hit rate limiting
            if (response.getStatusCode() == 429) {
                break;
            }
        }
        
        // Check responses
        int rateLimit = 0;
        for (Response response : responses) {
            if (response.getStatusCode() == 429) {
                rateLimit++;
                // Validate rate limit response
                String message = response.jsonPath().getString("message");
                assertTrue(message.contains("rate limit") || message.contains("too many requests"),
                        "Rate limit error should mention rate limiting");
                
                // Verify rate limit headers if present
                if (response.getHeader("X-RateLimit-Limit") != null) {
                    assertNotNull(response.getHeader("X-RateLimit-Remaining"), 
                            "Rate limit remaining header should be present");
                    assertNotNull(response.getHeader("X-RateLimit-Reset"), 
                            "Rate limit reset header should be present");
                }
            }
        }
        
        // Note: This test might not always trigger rate limiting depending on the API configuration
        System.out.println("Rate limit triggered " + rateLimit + " times out of " + responses.size() + " requests");
    }
}