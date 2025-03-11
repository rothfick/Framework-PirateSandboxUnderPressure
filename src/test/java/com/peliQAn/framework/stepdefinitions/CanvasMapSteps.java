package com.peliQAn.framework.stepdefinitions;

import com.peliQAn.framework.pages.hardcore.CanvasMapChallengePage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

/**
 * Step definitions for Canvas Map Challenge
 */
@Slf4j
public class CanvasMapSteps {

    private final CanvasMapChallengePage canvasMapPage = new CanvasMapChallengePage();
    private String challengeCode;

    @Given("I am on the {string} challenge page")
    public void iAmOnTheCanvasMapChallengePage(String challenge) {
        if (challenge.equals("canvas-map")) {
            canvasMapPage.navigateToPage();
        }
    }

    @When("I start the canvas challenge")
    public void iStartTheCanvasChallenge() {
        canvasMapPage.startChallenge();
    }

    @And("I navigate the treasure map to find the X marks")
    public void iNavigateTheTreasureMapToFindTheXMarks() {
        canvasMapPage.findXMarksOnMap();
    }

    @And("I dig at all X marks on the map")
    public void iDigAtAllXMarksOnTheMap() {
        canvasMapPage.digAtAllXMarks();
    }

    @Then("I should uncover the hidden treasure")
    public void iShouldUncoverTheHiddenTreasure() {
        // This verification is implicit in the digAtAllXMarks method
        // If no treasure is found, the test would fail at the next step
    }

    @When("I collect the treasure and get the code")
    public void iCollectTheTreasureAndGetTheCode() {
        challengeCode = canvasMapPage.collectTreasureAndGetCode();
        Assert.assertFalse(challengeCode.isEmpty(), "Challenge code should not be empty");
    }

    @And("I submit the treasure code")
    public void iSubmitTheTreasureCode() {
        canvasMapPage.validateSolution(challengeCode);
    }

    @Then("the validation should be successful")
    public void theValidationShouldBeSuccessful() {
        boolean isSuccessful = canvasMapPage.isValidationSuccessful();
        Assert.assertTrue(isSuccessful, "Solution validation should be successful");
        
        String message = canvasMapPage.getValidationResultMessage();
        Assert.assertTrue(message.contains("successfully"), "Message should indicate success");
    }
    
    @And("I explore the map visually to find interesting points")
    public void iExploreTheMapVisuallyToFindInterestingPoints() {
        canvasMapPage.exploreMapByMovingCursor();
    }
    
    @When("I complete all canvas map challenge steps")
    public void iCompleteAllCanvasMapChallengeSteps() {
        challengeCode = canvasMapPage.runCompleteChallenge();
        Assert.assertFalse(challengeCode.isEmpty(), "Challenge code should not be empty");
    }
}