package com.peliQAn.framework.pages.hardcore;

import com.peliQAn.framework.pages.BasePage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;
import java.util.List;

/**
 * Page object for Shadow DOM Challenge page (/test-ui/hardcore/shadow-dom)
 */
@Slf4j
public class ShadowDomChallengePage extends BasePage {

    private static final String PAGE_URL = "/test-ui/hardcore/shadow-dom";

    // Main elements
    @FindBy(id = "start-challenge-btn")
    private WebElement startChallengeButton;

    @FindBy(id = "shadow-host")
    private WebElement shadowHost;

    @FindBy(id = "nested-shadow-host")
    private WebElement nestedShadowHost;

    @FindBy(id = "dynamic-shadow-host")
    private WebElement dynamicShadowHost;

    @FindBy(id = "captcha-shadow-host")
    private WebElement captchaShadowHost;

    @FindBy(id = "challenge-result")
    private WebElement challengeResult;

    @FindBy(id = "challenge-code")
    private WebElement challengeCode;

    @FindBy(id = "solution-input")
    private WebElement solutionInput;

    @FindBy(id = "validate-btn")
    private WebElement validateButton;

    /**
     * Navigate to Shadow DOM Challenge page
     */
    @Step("Navigate to Shadow DOM Challenge page")
    public ShadowDomChallengePage navigateToPage() {
        navigateTo(baseUrl + PAGE_URL);
        waitForPageToLoad();
        log.info("Navigated to Shadow DOM Challenge page");
        return this;
    }

    /**
     * Start the challenge
     */
    @Step("Start the challenge")
    public ShadowDomChallengePage startChallenge() {
        click(startChallengeButton);
        
        // Wait for Shadow DOM to be attached
        waitForShadowDOM(shadowHost, 5);
        
        log.info("Started Shadow DOM challenge");
        return this;
    }

    /**
     * Complete Step 1: Basic Shadow DOM
     */
    @Step("Complete Step 1: Basic Shadow DOM")
    public ShadowDomChallengePage completeStep1(String name, String email, String password) {
        // Get Shadow Root and interact with form elements
        WebElement nameInput = findElementInShadowDOM(shadowHost, "#shadow-name");
        WebElement emailInput = findElementInShadowDOM(shadowHost, "#shadow-email");
        WebElement passwordInput = findElementInShadowDOM(shadowHost, "#shadow-password");
        WebElement nextButton = findElementInShadowDOM(shadowHost, "#shadow-next-btn");
        
        // Fill the form
        nameInput.sendKeys(name);
        emailInput.sendKeys(email);
        passwordInput.sendKeys(password);
        
        // Click next button
        nextButton.click();
        
        // Wait for Step 2 to load
        waitForShadowDOM(nestedShadowHost, 5);
        
        log.info("Completed Step 1 with name: {}, email: {}", name, email);
        return this;
    }

    /**
     * Complete Step 2: Nested Shadow DOM
     */
    @Step("Complete Step 2: Nested Shadow DOM")
    public ShadowDomChallengePage completeStep2() {
        // First level: Select dropdown option
        WebElement shadowDropdown = findElementInShadowDOM(nestedShadowHost, "#shadow-dropdown");
        Select dropdown = new Select(shadowDropdown);
        dropdown.selectByIndex(1); // Select second option
        
        // Find second level host
        WebElement secondLevelHost = findElementInShadowDOM(nestedShadowHost, "#second-level-host");
        
        // Second level: Select radio button
        WebElement radioButton = findElementInNestedShadowDOM(
            nestedShadowHost, 
            "#second-level-host", 
            "#shadow-radio-group .radio-option:nth-child(2)"
        );
        radioButton.click();
        
        // Find third level host
        WebElement thirdLevelHost = findElementInNestedShadowDOM(
            nestedShadowHost,
            "#second-level-host",
            "#third-level-host"
        );
        
        // Third level: Set slider
        WebElement slider = findElementInNestedShadowDOM(
            nestedShadowHost,
            "#second-level-host",
            "#third-level-host",
            "#shadow-slider"
        );
        
        // Use JavaScript to set slider value
        js.executeScript("arguments[0].value = 50;", slider);
        js.executeScript("arguments[0].dispatchEvent(new Event('change'));", slider);
        
        // Click continue button
        WebElement continueBtn = findElementInShadowDOM(nestedShadowHost, "#shadow-continue-btn");
        continueBtn.click();
        
        // Wait for Step 3 to load (with intentional delay)
        try {
            wait.until(ExpectedConditions.visibilityOf(dynamicShadowHost));
        } catch (Exception e) {
            log.warn("Dynamic shadow host visibility timeout, continuing anyway", e);
        }
        
        log.info("Completed Step 2: Nested Shadow DOM");
        return this;
    }

    /**
     * Complete Step 3: Dynamic Shadow DOM
     */
    @Step("Complete Step 3: Dynamic Shadow DOM")
    public ShadowDomChallengePage completeStep3() {
        // Wait for dynamic shadow DOM to be attached (longer timeout due to artificial delay)
        waitForShadowDOM(dynamicShadowHost, 15);
        
        // Wait for secret key to be available
        wait.until(driver -> {
            try {
                return findElementInShadowDOM(dynamicShadowHost, "#secret-key") != null;
            } catch (Exception e) {
                return false;
            }
        });
        
        // Get secret key
        String secretKey = findElementInShadowDOM(dynamicShadowHost, "#secret-key").getText();
        log.info("Retrieved secret key: {}", secretKey);
        
        // Get math expression from label
        String mathExpression = findElementInShadowDOM(dynamicShadowHost, "label[for='shadow-verification']").getText();
        mathExpression = mathExpression.replaceAll(".*: (.*)", "$1");
        log.info("Math expression: {}", mathExpression);
        
        // Calculate result
        int result = evaluateMathExpression(mathExpression);
        log.info("Calculated result: {}", result);
        
        // Enter result in verification field
        WebElement verificationInput = findElementInShadowDOM(dynamicShadowHost, "#shadow-verification");
        verificationInput.sendKeys(String.valueOf(result));
        
        // Click verify button
        WebElement verifyButton = findElementInShadowDOM(dynamicShadowHost, "#shadow-verify-btn");
        verifyButton.click();
        
        // Wait for Step 4 to load
        waitForShadowDOM(captchaShadowHost, 5);
        
        log.info("Completed Step 3: Dynamic Shadow DOM");
        return this;
    }

    /**
     * Evaluate simple math expression
     */
    private int evaluateMathExpression(String expression) {
        String[] parts = expression.trim().split("\\s+");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid math expression format: " + expression);
        }
        
        int a = Integer.parseInt(parts[0]);
        int b = Integer.parseInt(parts[2]);
        String operator = parts[1];
        
        return switch (operator) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> a / b;
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }

    /**
     * Complete Step 4: Shadow DOM CAPTCHA
     */
    @Step("Complete Step 4: Shadow DOM CAPTCHA")
    public ShadowDomChallengePage completeStep4() {
        // Wait for CAPTCHA to load
        waitForShadowDOM(captchaShadowHost, 5);
        
        // Find all target CAPTCHA items using JavaScript
        String script = 
            "return Array.from(arguments[0].shadowRoot.querySelectorAll('.captcha-item.target'))";
        List<WebElement> targetItems = (List<WebElement>) js.executeScript(script, captchaShadowHost);
        
        // Click on all target items
        for (WebElement item : targetItems) {
            item.click();
            // Small delay between clicks to ensure stability
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Click complete challenge button
        WebElement completeButton = findElementInShadowDOM(captchaShadowHost, "#complete-challenge-btn");
        completeButton.click();
        
        // Wait for challenge code to appear
        wait.until(ExpectedConditions.visibilityOf(challengeCode));
        
        log.info("Completed Step 4: Shadow DOM CAPTCHA");
        return this;
    }

    /**
     * Get challenge code
     */
    @Step("Get challenge code")
    public String getChallengeCode() {
        String code = getText(challengeCode);
        log.info("Got challenge code: {}", code);
        return code;
    }

    /**
     * Validate solution
     */
    @Step("Validate solution with code: {code}")
    public ShadowDomChallengePage validateSolution(String code) {
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
     * Run complete shadow DOM challenge
     */
    @Step("Run complete shadow DOM challenge")
    public String runCompleteChallenge(String name, String email, String password) {
        navigateToPage();
        startChallenge();
        completeStep1(name, email, password);
        completeStep2();
        completeStep3();
        completeStep4();
        String code = getChallengeCode();
        validateSolution(code);
        return code;
    }
}