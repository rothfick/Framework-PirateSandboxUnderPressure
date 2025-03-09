package com.peliQAn.framework.runners;

import com.peliQAn.framework.pages.hardcore.*;
import com.peliQAn.framework.core.DriverFactory;
import io.qameta.allure.*;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Test runner for all hardcore challenges
 */
@Epic("UI Tests")
@Feature("Hardcore Challenges")
public class HardcoreChallengesTestRunner {

    private WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = DriverFactory.initDriver();
    }

    @AfterMethod
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    /**
     * Shadow DOM Challenge
     */
    @Test(description = "Complete Shadow DOM Challenge")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Shadow DOM Challenge")
    @Description("Test that completes the Shadow DOM Challenge")
    public void testShadowDomChallenge() {
        ShadowDomChallengePage shadowDomChallengePage = new ShadowDomChallengePage();
        
        // Complete all steps in one flow
        String challengeCode = shadowDomChallengePage.runCompleteChallenge(
                "Test User", "test@example.com", "Password123");
        
        // Verify code was received
        assertNotNull(challengeCode, "Challenge code should not be null");
        assertFalse(challengeCode.isEmpty(), "Challenge code should not be empty");
        
        // Verify validation was successful
        assertTrue(shadowDomChallengePage.isValidationSuccessful(), 
                   "Solution validation should be successful");
    }

    /**
     * Multi-Window Treasure Hunt
     */
    @Test(description = "Complete Multi-Window Treasure Hunt Challenge")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Multi-Window Treasure Hunt")
    @Description("Test that completes the Multi-Window Treasure Hunt Challenge")
    public void testMultiWindowChallenge() {
        MultiWindowChallengePage multiWindowChallengePage = new MultiWindowChallengePage();
        
        // Complete all steps in one flow
        String challengeCode = multiWindowChallengePage.runCompleteChallenge();
        
        // Verify code was received
        assertNotNull(challengeCode, "Challenge code should not be null");
        assertFalse(challengeCode.isEmpty(), "Challenge code should not be empty");
        
        // Verify validation was successful
        assertTrue(multiWindowChallengePage.isValidationSuccessful(), 
                   "Solution validation should be successful");
    }

    /**
     * Canvas Map Challenge
     */
    @Test(description = "Complete Canvas Map Challenge")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Canvas Map Challenge")
    @Description("Test that completes the Canvas Map Challenge")
    public void testCanvasMapChallenge() {
        CanvasMapChallengePage canvasMapChallengePage = new CanvasMapChallengePage();
        
        // Complete all steps in one flow
        String challengeCode = canvasMapChallengePage.runCompleteChallenge();
        
        // Verify code was received
        assertNotNull(challengeCode, "Challenge code should not be null");
        assertFalse(challengeCode.isEmpty(), "Challenge code should not be empty");
        
        // Verify validation was successful
        assertTrue(canvasMapChallengePage.isValidationSuccessful(), 
                   "Solution validation should be successful");
    }

    /**
     * Iframe Inception Challenge
     */
    @Test(description = "Complete Iframe Inception Challenge")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Iframe Inception Challenge")
    @Description("Test that completes the Iframe Inception Challenge")
    public void testIframeInceptionChallenge() {
        IframeInceptionChallengePage iframeInceptionChallengePage = new IframeInceptionChallengePage();
        
        // Complete all steps in one flow
        String challengeCode = iframeInceptionChallengePage.runCompleteChallenge();
        
        // Verify code was received
        assertNotNull(challengeCode, "Challenge code should not be null");
        assertFalse(challengeCode.isEmpty(), "Challenge code should not be empty");
        
        // Verify validation was successful
        assertTrue(iframeInceptionChallengePage.isValidationSuccessful(), 
                   "Solution validation should be successful");
    }

    /**
     * Time Warp Challenge
     */
    @Test(description = "Complete Time Warp Challenge")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Time Warp Challenge")
    @Description("Test that completes the Time Warp Challenge")
    public void testTimeWarpChallenge() {
        TimeWarpChallengePage timeWarpChallengePage = new TimeWarpChallengePage();
        
        // Complete all steps in one flow
        String challengeCode = timeWarpChallengePage.runCompleteChallenge();
        
        // Verify code was received
        assertNotNull(challengeCode, "Challenge code should not be null");
        assertFalse(challengeCode.isEmpty(), "Challenge code should not be empty");
        
        // Verify validation was successful
        assertTrue(timeWarpChallengePage.isValidationSuccessful(), 
                   "Solution validation should be successful");
    }

    /**
     * Reactive Chaos Challenge
     */
    @Test(description = "Complete Reactive Chaos Challenge")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Reactive Chaos Challenge")
    @Description("Test that completes the Reactive Chaos Challenge")
    public void testReactiveChaosChallenge() {
        ReactiveChaosChallengePage reactiveChaosChallengePage = new ReactiveChaosChallengePage();
        
        // Complete all steps in one flow
        String challengeCode = reactiveChaosChallengePage.runCompleteChallenge();
        
        // Verify code was received
        assertNotNull(challengeCode, "Challenge code should not be null");
        assertFalse(challengeCode.isEmpty(), "Challenge code should not be empty");
        
        // Verify validation was successful
        assertTrue(reactiveChaosChallengePage.isValidationSuccessful(), 
                   "Solution validation should be successful");
    }
}