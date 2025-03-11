package com.peliQAn.framework.pact;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider;
import com.peliQAn.framework.config.PropertyManager;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Provider contract tests for Treasure API
 */
@Epic("Contract Tests")
@Feature("Treasure API Provider Contracts")
@Provider("treasureProvider")
@PactFolder("pacts")
public class TreasureProviderPactTest {

    @BeforeEach
    void setUp(PactVerificationContext context) {
        PropertyManager propertyManager = PropertyManager.getInstance();
        String apiBaseUrl = propertyManager.getProperty("api.baseUrl", "http://localhost:8080/api");
        
        context.setTarget(new HttpTestTarget(apiBaseUrl));
    }

    @TestTemplate
    @ExtendWith(PactVerificationSpringProvider.class)
    @Story("Verify Provider Contracts")
    @Description("Verify that the provider implements the contract as expected")
    void verifyPact(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("treasures exist")
    public void treasuresExist() {
        // No setup needed if test data already exists
        // This would typically seed the database with test data
        // For this example, we assume the data already exists
    }

    @State("treasure with ID 1 exists")
    public void treasureWithId1Exists() {
        // No setup needed if test data already exists
        // This would typically ensure a specific treasure exists
        // For this example, we assume the treasure with ID 1 already exists
    }

    @State("authorized user exists")
    public void authorizedUserExists() {
        // No setup needed if auth system already exists
        // This would typically ensure a test user exists
        // For this example, we assume the auth system accepts our test token
    }

    @State("treasure with ID 1 exists and user is authorized")
    public void treasureExistsAndUserIsAuthorized() {
        // Combines the two states above
        treasureWithId1Exists();
        authorizedUserExists();
    }
}