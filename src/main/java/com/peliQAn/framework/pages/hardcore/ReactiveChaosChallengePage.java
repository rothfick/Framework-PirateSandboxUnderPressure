package com.peliQAn.framework.pages.hardcore;

import com.peliQAn.framework.pages.BasePage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Page object for Reactive Chaos Challenge (/test-ui/hardcore/reactive-chaos)
 */
@Slf4j
public class ReactiveChaosChallengePage extends BasePage {

    private static final String PAGE_URL = "/test-ui/hardcore/reactive-chaos";

    // Main elements
    @FindBy(id = "start-challenge-btn")
    private WebElement startChallengeButton;
    
    @FindBy(id = "challenge-description")
    private WebElement challengeDescription;
    
    @FindBy(id = "data-streams-container")
    private WebElement dataStreamsContainer;
    
    @FindBy(id = "calculations-container")
    private WebElement calculationsContainer;
    
    @FindBy(id = "result-submission")
    private WebElement resultSubmission;
    
    @FindBy(id = "challenge-result")
    private WebElement challengeResult;
    
    @FindBy(id = "challenge-code")
    private WebElement challengeCode;
    
    @FindBy(id = "solution-input")
    private WebElement solutionInput;
    
    @FindBy(id = "validate-btn")
    private WebElement validateButton;
    
    // Data stream elements
    @FindBy(css = ".data-stream")
    private List<WebElement> dataStreams;
    
    // Calculation fields
    @FindBy(css = ".calculation-input")
    private List<WebElement> calculationInputs;
    
    @FindBy(css = ".calculation-submit")
    private List<WebElement> calculationSubmits;
    
    // Progress tracking
    private int currentStep = 0;
    private final Map<String, String> streamValues = new HashMap<>();

    /**
     * Navigate to Reactive Chaos Challenge page
     */
    @Step("Navigate to Reactive Chaos Challenge page")
    public ReactiveChaosChallengePage navigateToPage() {
        navigateTo(baseUrl + PAGE_URL);
        waitForPageToLoad();
        log.info("Navigated to Reactive Chaos Challenge page");
        return this;
    }

    /**
     * Start the challenge
     */
    @Step("Start the Reactive Chaos challenge")
    public ReactiveChaosChallengePage startChallenge() {
        click(startChallengeButton);
        waitForElementToBeVisible(dataStreamsContainer);
        log.info("Started Reactive Chaos challenge");
        return this;
    }

    /**
     * Monitor data streams
     */
    @Step("Monitor data streams")
    public ReactiveChaosChallengePage monitorDataStreams() {
        // Wait for data streams to appear
        wait.until(ExpectedConditions.visibilityOfAllElements(dataStreams));
        
        // Create a shorter wait for checking data streams
        var streamWait = new WebDriverWait(driver, Duration.ofMillis(100));
        
        // Monitor streams for about 5 seconds
        long endTime = System.currentTimeMillis() + 5000;
        
        while (System.currentTimeMillis() < endTime) {
            for (WebElement stream : dataStreams) {
                try {
                    // Get stream ID
                    String streamId = stream.getAttribute("id");
                    
                    // Get current value
                    WebElement valueElement = streamWait.until(
                        ExpectedConditions.visibilityOf(stream.findElement(By.className("stream-value"))));
                    
                    String value = valueElement.getText();
                    
                    // Store the value
                    streamValues.put(streamId, value);
                } catch (Exception e) {
                    // Ignore errors during stream monitoring
                }
            }
            
            // Short sleep
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        log.info("Monitored data streams, captured {} stream values", streamValues.size());
        return this;
    }

    /**
     * Capture values at a specific moment
     */
    @Step("Capture values at specific moment")
    public Map<String, String> captureValuesAtSpecificMoment() {
        // Capture stream values at a specific moment
        Map<String, String> capturedValues = new HashMap<>();
        
        for (WebElement stream : dataStreams) {
            try {
                String streamId = stream.getAttribute("id");
                WebElement valueElement = stream.findElement(By.className("stream-value"));
                String value = valueElement.getText();
                capturedValues.put(streamId, value);
                log.info("Captured value for stream {}: {}", streamId, value);
            } catch (Exception e) {
                log.warn("Error capturing value for stream: {}", e.getMessage());
            }
        }
        
        return capturedValues;
    }

    /**
     * Perform calculations on stream values
     */
    @Step("Perform calculations on stream values")
    public ReactiveChaosChallengePage performCalculations() {
        // Capture values
        Map<String, String> values = captureValuesAtSpecificMoment();
        
        // Perform calculations
        for (int i = 0; i < calculationInputs.size(); i++) {
            try {
                WebElement input = calculationInputs.get(i);
                WebElement submit = calculationSubmits.get(i);
                
                // Determine which calculation to perform based on instructions
                WebElement instructionElement = input.findElement(By.xpath("./preceding-sibling::div[@class='calculation-instruction']"));
                String instruction = instructionElement.getText();
                
                // Parse and perform calculation
                String result = calculateResult(instruction, values);
                
                // Enter result
                input.clear();
                input.sendKeys(result);
                
                // Submit
                submit.click();
                
                log.info("Performed calculation {}: {} = {}", i+1, instruction, result);
                
                // Give time for UI to update
                Thread.sleep(300);
                
            } catch (Exception e) {
                log.error("Error performing calculation: {}", e.getMessage());
            }
        }
        
        return this;
    }
    
    /**
     * Calculate result based on instruction and stream values
     */
    private String calculateResult(String instruction, Map<String, String> values) {
        try {
            // Parse instruction to determine operation and streams
            // Example: "Add Stream1 and Stream2" or "Multiply Stream3 by Stream4"
            
            // This is a simplified parser, a real one would be more robust
            String[] parts = instruction.split("\\s+");
            String operation = parts[0].toLowerCase();
            
            // Find stream names in the instruction
            List<String> streamRefs = new ArrayList<>();
            for (String part : parts) {
                if (part.toLowerCase().startsWith("stream")) {
                    streamRefs.add(part);
                }
            }
            
            if (streamRefs.size() < 2) {
                log.warn("Could not find enough stream references in instruction: {}", instruction);
                return "0";
            }
            
            // Get values for streams
            double value1 = Double.parseDouble(values.getOrDefault(streamRefs.get(0), "0"));
            double value2 = Double.parseDouble(values.getOrDefault(streamRefs.get(1), "0"));
            
            // Perform calculation
            double result = 0;
            switch (operation) {
                case "add":
                    result = value1 + value2;
                    break;
                case "subtract":
                    result = value1 - value2;
                    break;
                case "multiply":
                    result = value1 * value2;
                    break;
                case "divide":
                    result = value2 != 0 ? value1 / value2 : 0;
                    break;
                default:
                    log.warn("Unknown operation: {}", operation);
            }
            
            // Return result as string, formatted appropriately
            // Some challenges might require specific formatting
            return String.valueOf(Math.round(result * 100.0) / 100.0);
            
        } catch (Exception e) {
            log.error("Error calculating result: {}", e.getMessage());
            return "0";
        }
    }

    /**
     * Progress through challenge steps
     */
    @Step("Progress through challenge steps")
    public ReactiveChaosChallengePage progressThroughSteps() {
        // Each step involves monitoring streams and performing calculations
        // We'll loop until we reach the end of the challenge
        
        boolean challengeComplete = false;
        int maxSteps = 5; // Safety limit
        
        while (!challengeComplete && currentStep < maxSteps) {
            currentStep++;
            log.info("Starting step {} of the challenge", currentStep);
            
            // Monitor streams
            monitorDataStreams();
            
            // Perform calculations
            performCalculations();
            
            // Check if we've completed the challenge
            try {
                if (isElementDisplayed(challengeCode, 1)) {
                    challengeComplete = true;
                    log.info("Challenge completed at step {}", currentStep);
                }
            } catch (Exception e) {
                // Continue with next step
            }
            
            // Check for next step button
            try {
                WebElement nextButton = driver.findElement(By.id("next-step-btn"));
                if (nextButton.isDisplayed()) {
                    click(nextButton);
                    log.info("Proceeding to next step");
                    
                    // Wait for new streams to appear
                    wait.until(ExpectedConditions.refreshed(
                        ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".data-stream"))));
                }
            } catch (Exception e) {
                // No next step button, may be at end or error
                if (!challengeComplete) {
                    log.warn("No next step button found, but challenge not complete");
                }
            }
        }
        
        if (challengeComplete) {
            log.info("Successfully completed all steps of the Reactive Chaos Challenge");
        } else {
            log.warn("Reached maximum steps without completing challenge");
        }
        
        return this;
    }

    /**
     * Get the challenge completion code
     */
    @Step("Get the challenge completion code")
    public String getChallengeCode() {
        waitForElementToBeVisible(challengeCode);
        String code = getText(challengeCode);
        log.info("Got challenge code: {}", code);
        return code;
    }

    /**
     * Validate the solution with the code
     */
    @Step("Validate the solution with code: {code}")
    public ReactiveChaosChallengePage validateSolution(String code) {
        type(solutionInput, code);
        click(validateButton);
        
        // Wait for validation result
        wait.until(ExpectedConditions.or(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".challenge-success")),
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".challenge-error"))
        ));
        
        log.info("Validated solution with code: {}", code);
        return this;
    }

    /**
     * Check if validation was successful
     */
    @Step("Check if validation was successful")
    public boolean isValidationSuccessful() {
        try {
            WebElement successElement = driver.findElement(By.cssSelector(".challenge-success"));
            boolean isSuccess = successElement.isDisplayed();
            log.info("Validation success: {}", isSuccess);
            return isSuccess;
        } catch (Exception e) {
            log.info("Validation failed");
            return false;
        }
    }

    /**
     * Get validation result message
     */
    @Step("Get validation result message")
    public String getValidationResultMessage() {
        try {
            WebElement successElement = driver.findElement(By.cssSelector(".challenge-success"));
            if (successElement.isDisplayed()) {
                String message = getText(successElement);
                log.info("Validation success message: {}", message);
                return message;
            }
        } catch (Exception e) {
            // Ignore
        }
        
        try {
            WebElement errorElement = driver.findElement(By.cssSelector(".challenge-error"));
            if (errorElement.isDisplayed()) {
                String message = getText(errorElement);
                log.info("Validation error message: {}", message);
                return message;
            }
        } catch (Exception e) {
            // Ignore
        }
        
        return "No validation message found";
    }
    
    /**
     * Run complete Reactive Chaos challenge
     */
    @Step("Run complete Reactive Chaos challenge")
    public String runCompleteChallenge() {
        navigateToPage();
        startChallenge();
        progressThroughSteps();
        String code = getChallengeCode();
        validateSolution(code);
        return code;
    }
}