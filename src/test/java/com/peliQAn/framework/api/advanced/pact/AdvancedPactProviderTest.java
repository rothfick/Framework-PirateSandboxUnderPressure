package com.peliQAn.framework.api.advanced.pact;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.StateChangeAction;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import au.com.dius.pact.provider.junitsupport.target.Target;
import au.com.dius.pact.provider.junitsupport.target.TestTarget;
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider;
import com.github.javafaker.Faker;
import com.peliQAn.framework.config.PropertyManager;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

/**
 * Advanced Provider contract tests for Treasure API with extended provider state handling
 */
@Epic("Contract Tests")
@Feature("Advanced PACT Provider Contracts")
@Provider("treasureProvider")
@PactFolder("pacts")
public class AdvancedPactProviderTest {

    private static final Faker faker = new Faker();
    private String apiBaseUrl;
    private String authToken;
    
    // Store IDs for test data cleanup
    private Map<String, Object> testDataMap = new HashMap<>();

    @TestTarget
    public final Target target = new HttpTestTarget("localhost", 8080, "/api");

    @BeforeEach
    void setUp(PactVerificationContext context) {
        PropertyManager propertyManager = PropertyManager.getInstance();
        apiBaseUrl = propertyManager.getProperty("api.baseUrl", "http://localhost:8080/api");
        
        // If we were implementing against a real API, we would get a valid authToken here
        authToken = "test-token-for-provider-verification";
        
        // Set the test target
        context.setTarget(new HttpTestTarget(apiBaseUrl));
        
        // Optionally configure RestAssured if it's being used in provider state setup
        RestAssured.baseURI = apiBaseUrl;
    }

    @TestTemplate
    @ExtendWith(PactVerificationSpringProvider.class)
    @Story("Verify Provider Contracts")
    @Description("Verify that the provider implements the contracts as expected")
    void verifyPact(PactVerificationContext context) {
        context.verifyInteraction();
    }

    /**
     * Provider state: Treasures with various values exist
     */
    @State(value = "treasures with various values exist", action = StateChangeAction.SETUP)
    public void setupTreasuresWithVariousValues() {
        // In a real implementation, we would create treasure records in different value ranges
        // For now, we'll just log what would happen
        System.out.println("Setting up treasures with various values:");
        
        // Create treasures with values in different ranges:
        // 1. Below 1000
        createTestTreasure("Cheap Necklace", 500, "A cheap necklace", "Island X", false);
        
        // 2. Between 1000-5000 (matching our filter)
        createTestTreasure("Medium Crown", 3000, "A medium-priced crown", "Island Y", false);
        createTestTreasure("Valuable Map", 4500, "A valuable treasure map", "Island Z", true);
        
        // 3. Above 5000
        createTestTreasure("Expensive Diamond", 10000, "A very expensive diamond", "Island A", false);
    }
    
    /**
     * Provider state: A detailed treasure exists
     */
    @State(value = "a detailed treasure exists", action = StateChangeAction.SETUP)
    public void setupDetailedTreasure() {
        // In a real implementation, we would create a detailed treasure record
        // with all the required fields including metadata, tags and coordinates
        System.out.println("Setting up a detailed treasure with id=1");
        
        // Create a detailed treasure with metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("createdBy", "admin");
        metadata.put("createdAt", "2023-01-01T12:00:00.000Z");
        metadata.put("updatedAt", "2023-01-02T14:30:00.000Z");
        metadata.put("version", 2);
        
        // Create coordinates
        Map<String, Object> coordinates = new HashMap<>();
        coordinates.put("latitude", 34.5678);
        coordinates.put("longitude", -12.3456);
        
        // Store this in our test data map for reference
        testDataMap.put("detailedTreasureId", 1);
    }
    
    /**
     * Provider state: Treasures with 'gold' in the name exist
     */
    @State(value = "treasures with 'gold' in the name exist", action = StateChangeAction.SETUP)
    public void setupTreasuresWithGoldInName() {
        // In a real implementation, we would create treasures with "gold" in their names
        System.out.println("Setting up treasures with 'gold' in the name");
        
        createTestTreasure("Golden Crown", 5000, "A royal golden crown", "Palace", false);
        createTestTreasure("Gold Necklace", 3000, "A gold necklace", "Jewelry Box", true);
        createTestTreasure("Gold-plated Ring", 1500, "A gold-plated ring", "Drawer", false);
    }
    
    /**
     * Provider state: Treasure statistics are available
     */
    @State(value = "treasure statistics are available", action = StateChangeAction.SETUP)
    public void setupTreasureStatistics() {
        // In a real implementation, we would ensure there is enough data
        // to generate meaningful statistics
        System.out.println("Setting up data for treasure statistics");
        
        // Create a variety of treasures to generate interesting statistics
        createTestTreasure("Golden Crown", 5000, "A royal golden crown", "Palace", false);
        createTestTreasure("Silver Coin", 200, "A silver coin", "Pirate Bay", true);
        createTestTreasure("Diamond Ring", 7500, "A diamond ring", "Jewelry Box", true);
        createTestTreasure("Bronze Medal", 50, "A bronze medal", "Museum", false);
        createTestTreasure("Ruby Necklace", 3500, "A ruby necklace", "Safe", true);
    }
    
    /**
     * Provider state: No treasure with ID 999 exists
     */
    @State(value = "no treasure with ID 999 exists", action = StateChangeAction.SETUP)
    public void ensureNoTreasureWithId999Exists() {
        // In a real implementation, we would ensure no treasure with ID 999 exists
        System.out.println("Ensuring no treasure with ID 999 exists");
        
        // If we were implementing against a real API, we would delete the treasure if it exists
        given()
            .header("Authorization", "Bearer " + authToken)
            .when()
            .delete("/treasures/999")
            .then()
            .statusCode(anyOf(is(204), is(404))); // Either deleted successfully or wasn't there
    }
    
    /**
     * Provider state: Validation rules for treasures exist
     */
    @State(value = "validation rules for treasures exist", action = StateChangeAction.SETUP)
    public void setupValidationRules() {
        // Validation rules would typically be part of the API implementation
        // and not something we need to set up, but we can log that we're verifying them
        System.out.println("Validation rules for treasures are in place");
    }
    
    /**
     * Provider state: Authorized user exists
     */
    @State(value = "authorized user exists", action = StateChangeAction.SETUP)
    public void setupAuthorizedUser() {
        // In a real implementation, we would ensure a user exists and is authorized
        System.out.println("Setting up authorized user");
        
        // Generate a unique username
        String username = "testuser_" + System.currentTimeMillis();
        String email = username + "@example.com";
        
        // Store credentials in our test data for potential cleanup
        testDataMap.put("testUsername", username);
        testDataMap.put("testEmail", email);
        testDataMap.put("testAuthToken", authToken);
    }
    
    /**
     * Tear down provider states
     */
    @State(value = {"treasures with various values exist", 
                   "a detailed treasure exists", 
                   "treasures with 'gold' in the name exist",
                   "treasure statistics are available",
                   "validation rules for treasures exist",
                   "authorized user exists"}, 
           action = StateChangeAction.TEARDOWN)
    public void tearDown() {
        // In a real implementation, we would clean up all test data created during setup
        System.out.println("Tearing down test data");
        
        // Clean up the treasures we created
        // For each stored treasure ID, delete it
        for (Map.Entry<String, Object> entry : testDataMap.entrySet()) {
            if (entry.getKey().endsWith("TreasureId")) {
                try {
                    Long treasureId = Long.valueOf(entry.getValue().toString());
                    System.out.println("Deleting test treasure with ID: " + treasureId);
                    
                    // Delete via API if we were implementing against a real API
                    // given()
                    //    .header("Authorization", "Bearer " + authToken)
                    //    .when()
                    //    .delete("/treasures/" + treasureId);
                } catch (Exception e) {
                    System.err.println("Error deleting treasure: " + e.getMessage());
                }
            }
        }
        
        // Clear our test data map
        testDataMap.clear();
    }
    
    /**
     * Helper method to create a test treasure
     */
    private void createTestTreasure(String name, int value, String description, String location, boolean discovered) {
        // In a real implementation, we would create a treasure via the API
        System.out.println("Creating test treasure: " + name + " with value: " + value);
        
        // Prepare treasure data
        Map<String, Object> treasureData = new HashMap<>();
        treasureData.put("name", name);
        treasureData.put("value", value);
        treasureData.put("description", description);
        treasureData.put("location", location);
        treasureData.put("discovered", discovered);
        
        // Create via API if we were implementing against a real API
        // Response response = given()
        //    .contentType(ContentType.JSON)
        //    .header("Authorization", "Bearer " + authToken)
        //    .body(treasureData)
        //    .when()
        //    .post("/treasures");
        
        // For now, we'll just pretend we got an ID back and store it
        Long fakeId = faker.number().randomNumber(5, true);
        testDataMap.put(name.replaceAll("\\s", "") + "TreasureId", fakeId);
    }
}