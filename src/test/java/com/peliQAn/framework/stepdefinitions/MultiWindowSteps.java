package com.peliQAn.framework.stepdefinitions;

import com.peliQAn.framework.pages.hardcore.MultiWindowChallengePage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

/**
 * Step definitions for Multi-Window Treasure Hunt Challenge
 */
@Slf4j
public class MultiWindowSteps {

    private final MultiWindowChallengePage multiWindowPage = new MultiWindowChallengePage();
    private String challengeCode;

    @Given("I am on the {string} challenge page")
    public void iAmOnTheMultiWindowChallengePage(String challenge) {
        if (challenge.equals("multi-window")) {
            multiWindowPage.navigateToPage();
        }
    }

    @When("I start the treasure hunt")
    public void iStartTheTreasureHunt() {
        multiWindowPage.startChallenge();
    }

    @And("I navigate through all treasure windows collecting clues")
    public void iNavigateThroughAllTreasureWindowsCollectingClues() {
        multiWindowPage.navigateThroughAllWindows();
    }

    @And("I solve the final puzzle with the collected clues")
    public void iSolveTheFinalPuzzleWithTheCollectedClues() {
        multiWindowPage.solveFinalPuzzle();
    }

    @Then("I should receive a treasure hunt completion code")
    public void iShouldReceiveATreasureHuntCompletionCode() {
        challengeCode = multiWindowPage.getChallengeCode();
        Assert.assertFalse(challengeCode.isEmpty(), "Challenge code should not be empty");
    }

    @When("I submit the treasure hunt code")
    public void iSubmitTheTreasureHuntCode() {
        multiWindowPage.validateSolution(challengeCode);
    }

    @Then("the validation should be successful")
    public void theValidationShouldBeSuccessful() {
        boolean isSuccessful = multiWindowPage.isValidationSuccessful();
        Assert.assertTrue(isSuccessful, "Solution validation should be successful");
        
        String message = multiWindowPage.getValidationResultMessage();
        Assert.assertTrue(message.contains("successfully"), "Message should indicate success");
    }
    
    @When("I complete all multi-window challenge steps")
    public void iCompleteAllMultiWindowChallengeSteps() {
        challengeCode = multiWindowPage.runCompleteChallenge();
        Assert.assertFalse(challengeCode.isEmpty(), "Challenge code should not be empty");
    }
}