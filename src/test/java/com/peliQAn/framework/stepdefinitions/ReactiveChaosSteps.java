package com.peliQAn.framework.stepdefinitions;

import com.peliQAn.framework.pages.hardcore.ReactiveChaosChallengePage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.util.Map;

/**
 * Step definitions for Reactive Chaos Challenge
 */
@Slf4j
public class ReactiveChaosSteps {

    private final ReactiveChaosChallengePage reactiveChaosPage = new ReactiveChaosChallengePage();
    private String challengeCode;
    private Map<String, String> capturedValues;

    @Given("I am on the {string} challenge page")
    public void iAmOnTheReactiveChaosChallengePage(String challenge) {
        if (challenge.equals("reactive-chaos")) {
            reactiveChaosPage.navigateToPage();
        }
    }

    @When("I start the reactive chaos challenge")
    public void iStartTheReactiveChaosChallenge() {
        reactiveChaosPage.startChallenge();
    }

    @And("I monitor multiple data streams simultaneously")
    public void iMonitorMultipleDataStreamsSimultaneously() {
        reactiveChaosPage.monitorDataStreams();
    }

    @And("I capture values from all streams at a specific moment")
    public void iCaptureValuesFromAllStreamsAtASpecificMoment() {
        capturedValues = reactiveChaosPage.captureValuesAtSpecificMoment();
        Assert.assertFalse(capturedValues.isEmpty(), "Should have captured values from at least one stream");
        log.info("Captured values from {} streams", capturedValues.size());
    }

    @And("I perform calculations on the captured values")
    public void iPerformCalculationsOnTheCapturedValues() {
        reactiveChaosPage.performCalculations();
    }

    @And("I progress through all challenge steps")
    public void iProgressThroughAllChallengeSteps() {
        reactiveChaosPage.progressThroughSteps();
    }

    @Then("I should complete the reactive chaos challenge")
    public void iShouldCompleteTheReactiveChaosChallenge() {
        challengeCode = reactiveChaosPage.getChallengeCode();
        Assert.assertFalse(challengeCode.isEmpty(), "Challenge code should not be empty");
    }

    @When("I submit the reactive chaos challenge code")
    public void iSubmitTheReactiveChaosCode() {
        reactiveChaosPage.validateSolution(challengeCode);
    }

    @Then("the validation should be successful")
    public void theValidationShouldBeSuccessful() {
        boolean isSuccessful = reactiveChaosPage.isValidationSuccessful();
        Assert.assertTrue(isSuccessful, "Solution validation should be successful");
        
        String message = reactiveChaosPage.getValidationResultMessage();
        Assert.assertTrue(message.contains("successfully"), "Message should indicate success");
    }
    
    @When("I complete all reactive chaos challenge steps")
    public void iCompleteAllReactiveChaosSteps() {
        challengeCode = reactiveChaosPage.runCompleteChallenge();
        Assert.assertFalse(challengeCode.isEmpty(), "Challenge code should not be empty");
    }
}