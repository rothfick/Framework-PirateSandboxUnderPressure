package com.peliQAn.framework.pages.hardcore;

import com.peliQAn.framework.pages.BasePage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Page object for Iframe Inception Challenge (/test-ui/hardcore/iframe-inception)
 */
@Slf4j
public class IframeInceptionChallengePage extends BasePage {

    private static final String PAGE_URL = "/test-ui/hardcore/iframe-inception";

    // Main elements
    @FindBy(id = "start-challenge-btn")
    private WebElement startChallengeButton;
    
    @FindBy(id = "challenge-description")
    private WebElement challengeDescription;
    
    @FindBy(id = "iframe-container")
    private WebElement iframeContainer;
    
    @FindBy(id = "root-iframe")
    private WebElement rootIframe;
    
    @FindBy(id = "challenge-result")
    private WebElement challengeResult;
    
    @FindBy(id = "challenge-code")
    private WebElement challengeCode;
    
    @FindBy(id = "solution-input")
    private WebElement solutionInput;
    
    @FindBy(id = "validate-btn")
    private WebElement validateButton;
    
    // Store collected keys
    private final List<String> collectedKeys = new ArrayList<>();
    private final Map<String, Boolean> visitedIframes = new HashMap<>();

    /**
     * Navigate to Iframe Inception Challenge page
     */
    @Step("Navigate to Iframe Inception Challenge page")
    public IframeInceptionChallengePage navigateToPage() {
        navigateTo(baseUrl + PAGE_URL);
        waitForPageToLoad();
        log.info("Navigated to Iframe Inception Challenge page");
        return this;
    }

    /**
     * Start the challenge
     */
    @Step("Start the Iframe Inception challenge")
    public IframeInceptionChallengePage startChallenge() {
        click(startChallengeButton);
        waitForElementToBeVisible(rootIframe);
        log.info("Started Iframe Inception challenge");
        return this;
    }

    /**
     * Navigate through all nested iframe levels
     */
    @Step("Navigate through all nested iframe levels")
    public IframeInceptionChallengePage navigateThroughAllIframes() {
        // Start by switching to the root iframe
        switchToFrame(rootIframe);
        
        // Begin recursive exploration
        try {
            exploreIframeLevel("root");
        } finally {
            // Make sure we return to the main page when done
            switchToDefaultContent();
        }
        
        log.info("Completed navigation through all iframe levels, collected {} keys", collectedKeys.size());
        return this;
    }
    
    /**
     * Explore a single iframe level and its children recursively
     */
    private void exploreIframeLevel(String frameId) {
        // Mark this frame as visited
        visitedIframes.put(frameId, true);
        log.info("Exploring iframe: {}", frameId);
        
        try {
            // Look for keys in this iframe
            collectKeysInCurrentFrame();
            
            // Look for next level iframes
            List<WebElement> childIframes = driver.findElements(By.tagName("iframe"));
            
            if (!childIframes.isEmpty()) {
                log.info("Found {} child iframes in {}", childIframes.size(), frameId);
                
                for (int i = 0; i < childIframes.size(); i++) {
                    WebElement childIframe = childIframes.get(i);
                    String childId = frameId + "-child" + (i + 1);
                    
                    if (!visitedIframes.containsKey(childId)) {
                        // Switch to this child iframe
                        switchToFrame(childIframe);
                        
                        // Recursively explore it
                        exploreIframeLevel(childId);
                        
                        // Switch back to parent
                        switchToParentFrame();
                        
                        // Refresh reference to child iframes as the DOM might have changed
                        childIframes = driver.findElements(By.tagName("iframe"));
                    }
                }
            }
            
            // Check if there are any action buttons to click in this frame
            performActionsInCurrentFrame();
            
        } catch (Exception e) {
            log.error("Error exploring iframe {}: {}", frameId, e.getMessage());
        }
    }
    
    /**
     * Collect any keys found in the current iframe
     */
    private void collectKeysInCurrentFrame() {
        try {
            // Look for keys with explicit wait to handle delay
            List<WebElement> keyElements = new ArrayList<>();
            try {
                // Wait for any loading indicators to disappear
                List<WebElement> loadingIndicators = driver.findElements(By.cssSelector(".loading"));
                if (!loadingIndicators.isEmpty()) {
                    wait.until(ExpectedConditions.invisibilityOfAllElements(loadingIndicators));
                }
                
                // Look for key elements
                keyElements = driver.findElements(By.cssSelector(".treasure-key, .key-fragment, [data-key]"));
                
                // If not found, wait a bit and try again (for dynamically loaded keys)
                if (keyElements.isEmpty()) {
                    Thread.sleep(1000);
                    keyElements = driver.findElements(By.cssSelector(".treasure-key, .key-fragment, [data-key]"));
                }
            } catch (Exception e) {
                log.debug("Exception waiting for key elements: {}", e.getMessage());
            }
            
            for (WebElement keyElement : keyElements) {
                try {
                    String keyText = keyElement.getText();
                    if (keyText.isEmpty()) {
                        keyText = keyElement.getAttribute("data-key");
                    }
                    
                    if (keyText != null && !keyText.isEmpty() && !collectedKeys.contains(keyText)) {
                        collectedKeys.add(keyText);
                        log.info("Collected key: {}", keyText);
                        
                        // Some keys might need to be clicked to be collected
                        try {
                            keyElement.click();
                            log.info("Clicked on key element");
                        } catch (Exception e) {
                            // Ignore click errors, not all keys need to be clicked
                        }
                    }
                } catch (Exception e) {
                    log.warn("Error collecting key from element: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error collecting keys in current frame: {}", e.getMessage());
        }
    }
    
    /**
     * Perform any actions needed in the current iframe
     */
    private void performActionsInCurrentFrame() {
        try {
            // Look for buttons
            List<WebElement> actionButtons = driver.findElements(By.tagName("button"));
            for (WebElement button : actionButtons) {
                if (button.isDisplayed() && !button.getText().isEmpty() && 
                    !button.getText().equals("Start") && 
                    !button.getText().equals("Validate")) {
                    
                    log.info("Clicking button: {}", button.getText());
                    button.click();
                    
                    // Give time for any animations or effects
                    Thread.sleep(500);
                    
                    // Recheck for keys after action
                    collectKeysInCurrentFrame();
                }
            }
            
            // Look for inputs that need to be filled
            List<WebElement> inputs = driver.findElements(By.cssSelector("input:not([type='hidden'])"));
            for (WebElement input : inputs) {
                if (input.isDisplayed() && input.isEnabled()) {
                    log.info("Filling input field");
                    input.clear();
                    input.sendKeys("treasure");
                    
                    // If there's a submit button nearby, click it
                    WebElement form = input.findElement(By.xpath("./ancestor::form"));
                    if (form != null) {
                        WebElement submit = form.findElement(By.cssSelector("button[type='submit'], input[type='submit']"));
                        if (submit != null) {
                            submit.click();
                            log.info("Submitted form");
                            
                            // Recheck for keys after form submission
                            Thread.sleep(500);
                            collectKeysInCurrentFrame();
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            log.debug("Error performing actions in current frame: {}", e.getMessage());
        }
    }

    /**
     * Collect all keys from iframes
     */
    @Step("Collect all keys from iframes")
    public List<String> collectAllKeys() {
        if (collectedKeys.isEmpty()) {
            navigateThroughAllIframes();
        }
        return new ArrayList<>(collectedKeys);
    }

    /**
     * Submit collected keys to complete the challenge
     */
    @Step("Submit collected keys")
    public IframeInceptionChallengePage submitCollectedKeys() {
        // Switch back to the main document
        switchToDefaultContent();
        
        // Wait for the solution form to be available
        WebElement keySubmitForm = waitForElementToBeVisible(By.id("key-submission-form"));
        
        // Enter collected keys
        WebElement keysInput = keySubmitForm.findElement(By.id("collected-keys"));
        keysInput.clear();
        
        // Join all keys with comma or as required by the challenge
        String keysString = String.join(",", collectedKeys);
        keysInput.sendKeys(keysString);
        
        // Submit the keys
        WebElement submitButton = keySubmitForm.findElement(By.id("submit-keys"));
        click(submitButton);
        
        // Wait for challenge code to appear
        waitForElementToBeVisible(challengeCode);
        
        log.info("Submitted collected keys: {}", keysString);
        return this;
    }

    /**
     * Get the challenge completion code
     */
    @Step("Get the challenge completion code")
    public String getChallengeCode() {
        String code = getText(challengeCode);
        log.info("Got challenge code: {}", code);
        return code;
    }

    /**
     * Validate the solution with the code
     */
    @Step("Validate the solution with code: {code}")
    public IframeInceptionChallengePage validateSolution(String code) {
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
     * Run complete Iframe Inception challenge
     */
    @Step("Run complete Iframe Inception challenge")
    public String runCompleteChallenge() {
        navigateToPage();
        startChallenge();
        navigateThroughAllIframes();
        submitCollectedKeys();
        String code = getChallengeCode();
        validateSolution(code);
        return code;
    }
}