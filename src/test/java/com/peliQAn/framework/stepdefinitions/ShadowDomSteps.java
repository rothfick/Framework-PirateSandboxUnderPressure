package com.peliQAn.framework.stepdefinitions;

import com.peliQAn.framework.pages.hardcore.ShadowDomChallengePage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

/**
 * Step definitions for Shadow DOM Challenge
 */
@Slf4j
public class ShadowDomSteps {

    private final ShadowDomChallengePage shadowDomPage = new ShadowDomChallengePage();
    private String challengeCode;

    @Given("I am on the {string} challenge page")
    public void iAmOnTheChallengePage(String challenge) {
        if (challenge.equals("shadow-dom")) {
            shadowDomPage.navigateToPage();
        } else {
            throw new IllegalArgumentException("Unsupported challenge: " + challenge);
        }
    }

    @When("I start the challenge")
    public void iStartTheChallenge() {
        shadowDomPage.startChallenge();
    }

    @And("I complete Step 1 with name {string}, email {string}, and password {string}")
    public void iCompleteStepWithNameEmailAndPassword(String name, String email, String password) {
        shadowDomPage.completeStep1(name, email, password);
    }

    @And("I complete Step 2 with nested Shadow DOM interactions")
    public void iCompleteStepWithNestedShadowDOMInteractions() {
        shadowDomPage.completeStep2();
    }

    @And("I complete Step 3 with dynamic content")
    public void iCompleteStepWithDynamicContent() {
        shadowDomPage.completeStep3();
    }

    @And("I complete Step 4 with Shadow DOM CAPTCHA")
    public void iCompleteStepWithShadowDOMCAPTCHA() {
        shadowDomPage.completeStep4();
    }

    @Then("I should receive a challenge code")
    public void iShouldReceiveAChallengeCode() {
        challengeCode = shadowDomPage.getChallengeCode();
        Assert.assertFalse(challengeCode.isEmpty(), "Challenge code should not be empty");
        log.info("Received challenge code: {}", challengeCode);
    }

    @When("I validate the solution with the code")
    public void iValidateTheSolutionWithTheCode() {
        shadowDomPage.validateSolution(challengeCode);
    }

    @Then("the validation should be successful")
    public void theValidationShouldBeSuccessful() {
        boolean isSuccessful = shadowDomPage.isValidationSuccessful();
        Assert.assertTrue(isSuccessful, "Solution validation should be successful");
        
        String message = shadowDomPage.getValidationResultMessage();
        log.info("Validation result message: {}", message);
        Assert.assertTrue(message.contains("successfully"), "Message should indicate success");
    }
}