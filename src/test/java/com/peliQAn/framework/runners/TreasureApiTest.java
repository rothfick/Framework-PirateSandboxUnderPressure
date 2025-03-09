package com.peliQAn.framework.runners;

import com.peliQAn.framework.api.AuthApiClient;
import com.peliQAn.framework.api.TreasureApiClient;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * Test class for Treasure API
 */
@Epic("API Tests")
@Feature("Treasure API")
public class TreasureApiTest {

    private TreasureApiClient treasureApiClient;
    private AuthApiClient authApiClient;
    private String authToken;
    private Map<String, Object> createdTreasure;

    @BeforeClass
    public void setup() {
        treasureApiClient = new TreasureApiClient();
        authApiClient = new AuthApiClient();
        
        // Register and login to get auth token
        String username = "testuser_" + System.currentTimeMillis();
        String email = "testuser_" + System.currentTimeMillis() + "@example.com";
        String password = "Password123";
        
        authToken = authApiClient.registerAndLogin(username, email, password);
        treasureApiClient.setAuthToken(authToken);
    }

    /**
     * Simple approach: Basic CRUD operations
     */
    @Test(description = "Simple CRUD operations for treasures")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Treasure CRUD")
    @Description("Test basic CRUD operations for treasures with simple approach")
    public void testSimpleTreasureCrud() {
        // Get all treasures
        Response getAllResponse = treasureApiClient.getAllTreasures();
        assertEquals(getAllResponse.getStatusCode(), 200, "Get all treasures should return 200");
        
        // Create a treasure
        Map<String, Object> treasureData = new HashMap<>();
        treasureData.put("name", "Golden Chalice");
        treasureData.put("value", 1000);
        treasureData.put("description", "An ancient golden chalice");
        treasureData.put("location", "Skull Island");
        treasureData.put("discovered", false);
        
        Response createResponse = treasureApiClient.createTreasure(treasureData);
        assertEquals(createResponse.getStatusCode(), 201, "Create treasure should return 201");
        
        // Get the created treasure ID
        createdTreasure = createResponse.as(Map.class);
        Long treasureId = Long.valueOf(createdTreasure.get("id").toString());
        
        // Get the treasure by ID
        Response getByIdResponse = treasureApiClient.getTreasureById(treasureId);
        assertEquals(getByIdResponse.getStatusCode(), 200, "Get treasure by ID should return 200");
        Map<String, Object> retrievedTreasure = getByIdResponse.as(Map.class);
        assertEquals(retrievedTreasure.get("name"), treasureData.get("name"), "Retrieved treasure name should match");
        
        // Update the treasure
        treasureData.put("value", 2000);
        treasureData.put("description", "An ancient golden chalice with jewels");
        Response updateResponse = treasureApiClient.updateTreasure(treasureId, treasureData);
        assertEquals(updateResponse.getStatusCode(), 200, "Update treasure should return 200");
        
        // Verify update
        getByIdResponse = treasureApiClient.getTreasureById(treasureId);
        retrievedTreasure = getByIdResponse.as(Map.class);
        assertEquals(retrievedTreasure.get("value"), 2000, "Updated treasure value should match");
        
        // Delete the treasure
        Response deleteResponse = treasureApiClient.deleteTreasure(treasureId);
        assertEquals(deleteResponse.getStatusCode(), 204, "Delete treasure should return 204");
        
        // Verify deletion
        getByIdResponse = treasureApiClient.getTreasureById(treasureId);
        assertEquals(getByIdResponse.getStatusCode(), 404, "Get deleted treasure should return 404");
    }

    /**
     * Advanced approach: Complex operations and validations
     */
    @Test(description = "Advanced treasure operations with validations")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Advanced Treasure Operations")
    @Description("Test advanced treasure operations with comprehensive validations")
    public void testAdvancedTreasureOperations() {
        // Create multiple treasures
        Map<String, Object> treasure1 = treasureApiClient.createTreasureHelper(
                "Diamond Crown", 5000, "A crown studded with diamonds", "Treasure Island", false);
        
        Map<String, Object> treasure2 = treasureApiClient.createTreasureHelper(
                "Ruby Necklace", 3000, "A necklace with large rubies", "Serpent Island", true);
        
        Map<String, Object> treasure3 = treasureApiClient.createTreasureHelper(
                "Emerald Ring", 2000, "A ring with an emerald stone", "Skull Island", false);
        
        assertNotNull(treasure1, "Treasure 1 should be created");
        assertNotNull(treasure2, "Treasure 2 should be created");
        assertNotNull(treasure3, "Treasure 3 should be created");
        
        // Get all treasures and verify count
        List<Map<String, Object>> allTreasures = treasureApiClient.getAllTreasuresAsList();
        assertNotNull(allTreasures, "Should retrieve all treasures");
        
        // Check if our treasures are in the list
        boolean foundTreasure1 = allTreasures.stream()
                .anyMatch(t -> t.get("name").equals("Diamond Crown"));
        boolean foundTreasure2 = allTreasures.stream()
                .anyMatch(t -> t.get("name").equals("Ruby Necklace"));
        boolean foundTreasure3 = allTreasures.stream()
                .anyMatch(t -> t.get("name").equals("Emerald Ring"));
        
        assertTrue(foundTreasure1, "Should find Treasure 1 in the list");
        assertTrue(foundTreasure2, "Should find Treasure 2 in the list");
        assertTrue(foundTreasure3, "Should find Treasure 3 in the list");
        
        // Search for treasures by name
        Response searchResponse = treasureApiClient.searchTreasuresByName("Diamond");
        assertEquals(searchResponse.getStatusCode(), 200, "Search should return 200");
        List<Map<String, Object>> searchResults = searchResponse.jsonPath().getList("$");
        assertTrue(searchResults.stream().anyMatch(t -> t.get("name").equals("Diamond Crown")), 
                  "Search results should contain Diamond Crown");
        
        // Get discovered treasures
        Response discoveredResponse = treasureApiClient.getDiscoveredTreasures();
        assertEquals(discoveredResponse.getStatusCode(), 200, "Get discovered should return 200");
        List<Map<String, Object>> discoveredTreasures = discoveredResponse.jsonPath().getList("$");
        assertTrue(discoveredTreasures.stream().anyMatch(t -> t.get("name").equals("Ruby Necklace")), 
                  "Discovered treasures should contain Ruby Necklace");
        assertFalse(discoveredTreasures.stream().anyMatch(t -> t.get("name").equals("Diamond Crown")), 
                   "Discovered treasures should not contain Diamond Crown");
        
        // Discover a treasure
        Long treasure1Id = Long.valueOf(treasure1.get("id").toString());
        Response discoverResponse = treasureApiClient.discoverTreasure(treasure1Id);
        assertEquals(discoverResponse.getStatusCode(), 200, "Discover treasure should return 200");
        
        // Verify treasure is now discovered
        Response getUpdatedResponse = treasureApiClient.getTreasureById(treasure1Id);
        Map<String, Object> updatedTreasure = getUpdatedResponse.as(Map.class);
        assertTrue((Boolean) updatedTreasure.get("discovered"), "Treasure should be marked as discovered");
        
        // Clean up
        treasureApiClient.deleteTreasure(Long.valueOf(treasure1.get("id").toString()));
        treasureApiClient.deleteTreasure(Long.valueOf(treasure2.get("id").toString()));
        treasureApiClient.deleteTreasure(Long.valueOf(treasure3.get("id").toString()));
    }

    /**
     * Edge case tests for treasure API
     */
    @Test(description = "Edge cases for treasure API")
    @Severity(SeverityLevel.NORMAL)
    @Story("Treasure API Edge Cases")
    @Description("Test edge cases and error handling for treasure API")
    public void testTreasureApiEdgeCases() {
        // Test case: Get non-existent treasure
        Response getNonExistentResponse = treasureApiClient.getTreasureById(99999);
        assertEquals(getNonExistentResponse.getStatusCode(), 404, "Get non-existent treasure should return 404");
        
        // Test case: Create treasure with missing required fields
        Map<String, Object> incompleteData = new HashMap<>();
        incompleteData.put("name", "Incomplete Treasure");
        // Missing other required fields
        
        Response incompleteCreateResponse = treasureApiClient.createTreasure(incompleteData);
        assertNotEquals(incompleteCreateResponse.getStatusCode(), 201, 
                      "Create with incomplete data should not return 201");
        
        // Test case: Create treasure with invalid value
        Map<String, Object> invalidData = new HashMap<>();
        invalidData.put("name", "Invalid Treasure");
        invalidData.put("value", "not-a-number"); // Value should be a number
        invalidData.put("description", "Invalid treasure data");
        invalidData.put("location", "Test Island");
        invalidData.put("discovered", false);
        
        Response invalidCreateResponse = treasureApiClient.createTreasure(invalidData);
        assertNotEquals(invalidCreateResponse.getStatusCode(), 201, 
                      "Create with invalid data should not return 201");
        
        // Test case: Update non-existent treasure
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("name", "Updated Treasure");
        updateData.put("value", 500);
        
        Response updateNonExistentResponse = treasureApiClient.updateTreasure(99999, updateData);
        assertEquals(updateNonExistentResponse.getStatusCode(), 404, 
                   "Update non-existent treasure should return 404");
        
        // Test case: Create and then retrieve a treasure with very long text
        String veryLongName = "This is a very long treasure name that exceeds the normal size of a name field " +
                              "to test how the API handles extremely long input values. It should either truncate " +
                              "this or return an error, depending on the implementation.";
        
        Map<String, Object> longTextData = new HashMap<>();
        longTextData.put("name", veryLongName);
        longTextData.put("value", 100);
        longTextData.put("description", "A treasure with very long name");
        longTextData.put("location", "Test Island");
        longTextData.put("discovered", false);
        
        Response longTextResponse = treasureApiClient.createTreasure(longTextData);
        
        // If creation succeeds, delete the treasure
        if (longTextResponse.getStatusCode() == 201) {
            Map<String, Object> createdLongTreasure = longTextResponse.as(Map.class);
            Long longTreasureId = Long.valueOf(createdLongTreasure.get("id").toString());
            
            // Get the treasure to check if name was truncated
            Response getLongResponse = treasureApiClient.getTreasureById(longTreasureId);
            Map<String, Object> retrievedLongTreasure = getLongResponse.as(Map.class);
            
            // Clean up
            treasureApiClient.deleteTreasure(longTreasureId);
        }
    }
}