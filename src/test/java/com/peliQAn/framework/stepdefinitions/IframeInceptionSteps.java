package com.peliQAn.framework.stepdefinitions;

import com.peliQAn.framework.pages.hardcore.IframeInceptionChallengePage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.util.List;

/**
 * Step definitions for Iframe Inception Challenge
 */
@Slf4j
public class IframeInceptionSteps {

    private final IframeInceptionChallengePage iframeInceptionPage = new IframeInceptionChallengePage();
    private String challengeCode;
    private List<String> collectedKeys;

    @Given("I am on the {string} challenge page")
    public void iAmOnTheIframeInceptionChallengePage(String challenge) {
        if (challenge.equals("iframe-inception")) {
            iframeInceptionPage.navigateToPage();
        }
    }

    @When("I start the iframe challenge")
    public void iStartTheIframeChallenge() {
        iframeInceptionPage.startChallenge();
    }

    @And("I navigate through all nested iframe levels")
    public void iNavigateThroughAllNestedIframeLevels() {
        iframeInceptionPage.navigateThroughAllIframes();
    }

    @And("I collect all hidden keys in the iframes")
    public void iCollectAllHiddenKeysInTheIframes() {
        collectedKeys = iframeInceptionPage.collectAllKeys();
        Assert.assertFalse(collectedKeys.isEmpty(), "Should have collected at least one key");
        log.info("Collected {} keys: {}", collectedKeys.size(), collectedKeys);
    }

    @Then("I should complete the inception challenge")
    public void iShouldCompleteTheInceptionChallenge() {
        iframeInceptionPage.submitCollectedKeys();
    }

    @When("I submit the collected keys")
    public void iSubmitTheCollectedKeys() {
        // If we already submitted the keys in the previous step, get the code now
        challengeCode = iframeInceptionPage.getChallengeCode();
        Assert.assertFalse(challengeCode.isEmpty(), "Challenge code should not be empty");
        
        // Validate the solution
        iframeInceptionPage.validateSolution(challengeCode);
    }

    @Then("the validation should be successful")
    public void theValidationShouldBeSuccessful() {
        boolean isSuccessful = iframeInceptionPage.isValidationSuccessful();
        Assert.assertTrue(isSuccessful, "Solution validation should be successful");
        
        String message = iframeInceptionPage.getValidationResultMessage();
        Assert.assertTrue(message.contains("successfully"), "Message should indicate success");
    }
    
    @When("I complete all iframe inception challenge steps")
    public void iCompleteAllIframeInceptionChallengeSteps() {
        challengeCode = iframeInceptionPage.runCompleteChallenge();
        Assert.assertFalse(challengeCode.isEmpty(), "Challenge code should not be empty");
    }
}