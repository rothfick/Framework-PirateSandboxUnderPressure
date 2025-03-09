package com.peliQAn.framework.runners;

import com.peliQAn.framework.pages.hardcore.ShadowDomChallengePage;
import com.peliQAn.framework.core.DriverFactory;
import io.qameta.allure.*;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Test class for Shadow DOM Challenge
 */
@Epic("UI Tests")
@Feature("Hardcore Challenges")
public class ShadowDomChallengeTest {

    private WebDriver driver;
    private ShadowDomChallengePage shadowDomChallengePage;

    @BeforeMethod
    public void setUp() {
        driver = DriverFactory.initDriver();
        shadowDomChallengePage = new ShadowDomChallengePage();
    }

    @AfterMethod
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    /**
     * Simple approach: Step-by-step test with basic assertions
     */
    @Test(description = "Complete Shadow DOM Challenge - Simple Approach")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Shadow DOM Challenge")
    @Description("Test that completes the Shadow DOM Challenge step by step using simple approach")
    public void testShadowDomChallengeSimpleApproach() {
        // Step 1: Navigate and start challenge
        shadowDomChallengePage.navigateToPage();
        shadowDomChallengePage.startChallenge();
        
        // Step 2: Complete basic shadow DOM form
        shadowDomChallengePage.completeStep1("Test User", "test@example.com", "Password123");
        
        // Step 3: Complete nested shadow DOM interactions
        shadowDomChallengePage.completeStep2();
        
        // Step 4: Complete dynamic shadow DOM with math calculation
        shadowDomChallengePage.completeStep3();
        
        // Step 5: Complete shadow DOM CAPTCHA
        shadowDomChallengePage.completeStep4();
        
        // Get challenge code
        String challengeCode = shadowDomChallengePage.getChallengeCode();
        assertFalse(challengeCode.isEmpty(), "Challenge code should not be empty");
        
        // Validate solution
        shadowDomChallengePage.validateSolution(challengeCode);
        
        // Verify success
        assertTrue(shadowDomChallengePage.isValidationSuccessful(), 
                   "Solution validation should be successful");
    }

    /**
     * Advanced approach: Robust test with additional checks and error handling
     */
    @Test(description = "Complete Shadow DOM Challenge - Advanced Approach")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Shadow DOM Challenge")
    @Description("Test that completes the Shadow DOM Challenge with advanced error handling and verification")
    public void testShadowDomChallengeAdvancedApproach() {
        try {
            // Generate random test data
            String name = "Tester_" + System.currentTimeMillis();
            String email = "test_" + System.currentTimeMillis() + "@example.com";
            String password = "Pass_" + System.currentTimeMillis();
            
            // Run full challenge with fluent interface
            String challengeCode = shadowDomChallengePage
                    .navigateToPage()
                    .startChallenge()
                    .completeStep1(name, email, password)
                    .completeStep2()
                    .completeStep3()
                    .completeStep4()
                    .getChallengeCode();
            
            // Verify code format (assuming format is like "SD-XXXX-YYYY")
            assertTrue(challengeCode.matches("SD-[A-Z0-9]+-[A-Z0-9]+"), 
                       "Challenge code should match expected format");
            
            // Validate solution
            shadowDomChallengePage.validateSolution(challengeCode);
            
            // Get and verify success message
            String validationMessage = shadowDomChallengePage.getValidationResultMessage();
            assertTrue(validationMessage.contains("successfully"), 
                      "Validation message should indicate success");
            
        } catch (Exception e) {
            fail("Shadow DOM challenge test failed with exception: " + e.getMessage());
        }
    }
    
    /**
     * One-click approach: Complete challenge in a single method call
     */
    @Test(description = "Complete Shadow DOM Challenge - One-Click Approach")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Shadow DOM Challenge")
    @Description("Test that completes the Shadow DOM Challenge with a single method call")
    public void testShadowDomChallengeOneClickApproach() {
        // Complete the entire challenge with one method call
        String challengeCode = shadowDomChallengePage.runCompleteChallenge(
                "Tester", "tester@example.com", "SecurePassword123");
        
        // Verify code was received
        assertNotNull(challengeCode, "Challenge code should not be null");
        assertFalse(challengeCode.isEmpty(), "Challenge code should not be empty");
        
        // Verify validation was successful
        assertTrue(shadowDomChallengePage.isValidationSuccessful(), 
                   "Solution validation should be successful");
    }
}