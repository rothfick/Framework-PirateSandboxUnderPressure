package com.peliQAn.framework.stepdefinitions;

import com.peliQAn.framework.pages.hardcore.TimeWarpChallengePage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

/**
 * Step definitions for Time Warp Challenge
 */
@Slf4j
public class TimeWarpSteps {

    private final TimeWarpChallengePage timeWarpPage = new TimeWarpChallengePage();
    private String challengeCode;

    @Given("I am on the {string} challenge page")
    public void iAmOnTheTimeWarpChallengePage(String challenge) {
        if (challenge.equals("time-warp")) {
            timeWarpPage.navigateToPage();
        }
    }

    @When("I start the time warp challenge")
    public void iStartTheTimeWarpChallenge() {
        timeWarpPage.startChallenge();
    }

    @And("I set the browser time to {string} to get the past artifact")
    public void iSetTheBrowserTimeToToGetThePastArtifact(String dateTimeString) {
        timeWarpPage.setBrowserTime(dateTimeString);
        timeWarpPage.getPastArtifact();
    }

    @And("I set the browser time to {string} to get the future technology")
    public void iSetTheBrowserTimeToToGetTheFutureTechnology(String dateTimeString) {
        timeWarpPage.setBrowserTime(dateTimeString);
        timeWarpPage.getFutureTechnology();
    }

    @And("I change the timezone to UTC+{int} to solve the paradox")
    public void iChangeTheTimezoneToUTCToSolveTheParadox(int timezoneOffset) {
        timeWarpPage.setTimezoneOffset(timezoneOffset);
        timeWarpPage.solveTimezoneParadox();
    }

    @Then("I should collect all time codes")
    public void iShouldCollectAllTimeCodes() {
        timeWarpPage.stabilizeTimeline();
    }

    @When("I submit the time codes")
    public void iSubmitTheTimeCodes() {
        challengeCode = timeWarpPage.getChallengeCode();
        Assert.assertFalse(challengeCode.isEmpty(), "Challenge code should not be empty");
        
        timeWarpPage.validateSolution(challengeCode);
    }

    @Then("the validation should be successful")
    public void theValidationShouldBeSuccessful() {
        boolean isSuccessful = timeWarpPage.isValidationSuccessful();
        Assert.assertTrue(isSuccessful, "Solution validation should be successful");
        
        String message = timeWarpPage.getValidationResultMessage();
        Assert.assertTrue(message.contains("successfully"), "Message should indicate success");
    }
    
    @And("I reset the browser time and timezone")
    public void iResetTheBrowserTimeAndTimezone() {
        timeWarpPage.resetBrowserTime();
        timeWarpPage.resetBrowserTimezone();
    }
    
    @When("I complete all time warp challenge steps")
    public void iCompleteAllTimeWarpChallengeSteps() {
        challengeCode = timeWarpPage.runCompleteChallenge();
        Assert.assertFalse(challengeCode.isEmpty(), "Challenge code should not be empty");
    }
}