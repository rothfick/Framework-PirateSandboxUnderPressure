package com.peliQAn.framework.pages.hardcore;

import com.peliQAn.framework.pages.BasePage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Page object for Time Warp Challenge (/test-ui/hardcore/time-warp)
 */
@Slf4j
public class TimeWarpChallengePage extends BasePage {

    private static final String PAGE_URL = "/test-ui/hardcore/time-warp";

    // Main elements
    @FindBy(id = "start-challenge-btn")
    private WebElement startChallengeButton;
    
    @FindBy(id = "challenge-description")
    private WebElement challengeDescription;
    
    @FindBy(id = "time-display")
    private WebElement timeDisplay;
    
    @FindBy(id = "timezone-display")
    private WebElement timezoneDisplay;
    
    @FindBy(id = "time-machine")
    private WebElement timeMachine;
    
    @FindBy(id = "past-artifact")
    private WebElement pastArtifact;
    
    @FindBy(id = "future-technology")
    private WebElement futureTechnology;
    
    @FindBy(id = "timeline-stabilizer")
    private WebElement timelineStabilizer;
    
    @FindBy(id = "challenge-result")
    private WebElement challengeResult;
    
    @FindBy(id = "challenge-code")
    private WebElement challengeCode;
    
    @FindBy(id = "solution-input")
    private WebElement solutionInput;
    
    @FindBy(id = "validate-btn")
    private WebElement validateButton;
    
    // Store collected time codes
    private final List<String> timeCodesList = new ArrayList<>();

    /**
     * Navigate to Time Warp Challenge page
     */
    @Step("Navigate to Time Warp Challenge page")
    public TimeWarpChallengePage navigateToPage() {
        navigateTo(baseUrl + PAGE_URL);
        waitForPageToLoad();
        log.info("Navigated to Time Warp Challenge page");
        return this;
    }

    /**
     * Start the challenge
     */
    @Step("Start the Time Warp challenge")
    public TimeWarpChallengePage startChallenge() {
        click(startChallengeButton);
        waitForElementToBeVisible(timeMachine);
        log.info("Started Time Warp challenge");
        return this;
    }

    /**
     * Set browser time to a specific date and time
     */
    @Step("Set browser time to: {dateTimeString}")
    public TimeWarpChallengePage setBrowserTime(String dateTimeString) {
        // Parse the date time string
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, 
                DateTimeFormatter.ISO_DATE_TIME);
        
        // Calculate milliseconds since epoch
        long timestamp = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        
        // Override JavaScript Date using script
        String script = 
            "const originalDate = Date;" +
            "const timestamp = " + timestamp + ";" +
            "const customDate = new Date(timestamp);" +
            "Date = class extends originalDate {" +
            "  constructor() {" +
            "    super();" +
            "    return customDate;" +
            "  }" +
            "  static now() {" +
            "    return timestamp;" +
            "  }" +
            "}";
        
        js.executeScript(script);
        
        // Set the date in the time machine UI
        WebElement dateInput = timeMachine.findElement(By.id("date-input"));
        WebElement timeInput = timeMachine.findElement(By.id("time-input"));
        WebElement setTimeButton = timeMachine.findElement(By.id("set-time-btn"));
        
        // Format for input fields
        String dateString = dateTime.format(DateTimeFormatter.ISO_DATE);
        String timeString = dateTime.format(DateTimeFormatter.ISO_TIME).substring(0, 5); // HH:mm format
        
        // Clear and set inputs
        dateInput.clear();
        dateInput.sendKeys(dateString);
        timeInput.clear();
        timeInput.sendKeys(timeString);
        
        // Click set time
        click(setTimeButton);
        
        log.info("Set browser time to: {}", dateTimeString);
        return this;
    }

    /**
     * Set browser timezone offset
     */
    @Step("Set browser timezone to UTC{timezoneOffset}")
    public TimeWarpChallengePage setTimezoneOffset(int timezoneOffset) {
        // Store original method
        js.executeScript(
            "if (!window.originalGetTimezoneOffset) {" +
            "  window.originalGetTimezoneOffset = Date.prototype.getTimezoneOffset;" +
            "}" +
            "Date.prototype.getTimezoneOffset = function() { return " + (-timezoneOffset * 60) + "; };"
        );
        
        // Set the timezone in UI
        WebElement timezoneSelect = timeMachine.findElement(By.id("timezone-select"));
        WebElement setTimezoneButton = timeMachine.findElement(By.id("set-timezone-btn"));
        
        // Select the timezone
        selectByValue(timezoneSelect, "UTC+" + timezoneOffset);
        
        // Click set timezone
        click(setTimezoneButton);
        
        log.info("Set browser timezone to UTC+{}", timezoneOffset);
        return this;
    }

    /**
     * Get past artifact by traveling to 2001
     */
    @Step("Travel to 2001 and get past artifact")
    public TimeWarpChallengePage getPastArtifact() {
        // Set date to January 1, 2001 at noon
        setBrowserTime("2001-01-01T12:00:00Z");
        
        // Wait for past artifact to appear
        waitForElementToBeVisible(pastArtifact);
        
        // Click on the artifact to collect it
        click(pastArtifact);
        
        // Check for code display
        WebElement pastCodeElement = driver.findElement(By.id("past-code"));
        if (pastCodeElement.isDisplayed()) {
            String pastCode = getText(pastCodeElement);
            timeCodesList.add(pastCode);
            log.info("Collected past artifact code: {}", pastCode);
        }
        
        return this;
    }

    /**
     * Get future technology by traveling to 2029
     */
    @Step("Travel to 2029 and get future technology")
    public TimeWarpChallengePage getFutureTechnology() {
        // Set date to January 1, 2029 at noon
        setBrowserTime("2029-01-01T12:00:00Z");
        
        // Wait for future technology to appear
        waitForElementToBeVisible(futureTechnology);
        
        // Click on the technology to collect it
        click(futureTechnology);
        
        // Check for code display
        WebElement futureCodeElement = driver.findElement(By.id("future-code"));
        if (futureCodeElement.isDisplayed()) {
            String futureCode = getText(futureCodeElement);
            timeCodesList.add(futureCode);
            log.info("Collected future technology code: {}", futureCode);
        }
        
        return this;
    }

    /**
     * Solve timezone paradox with UTC+2
     */
    @Step("Solve timezone paradox with UTC+2")
    public TimeWarpChallengePage solveTimezoneParadox() {
        // Set timezone to UTC+2
        setTimezoneOffset(2);
        
        // Set date to current time to trigger the paradox check
        LocalDateTime now = LocalDateTime.now();
        setBrowserTime(now.format(DateTimeFormatter.ISO_DATE_TIME));
        
        // Wait for timezone paradox to be solved
        WebElement paradoxElement = driver.findElement(By.id("timezone-paradox"));
        waitForElementToBeVisible(paradoxElement);
        
        // Click on the paradox to resolve it
        click(paradoxElement);
        
        // Check for code display
        WebElement paradoxCodeElement = driver.findElement(By.id("paradox-code"));
        if (paradoxCodeElement.isDisplayed()) {
            String paradoxCode = getText(paradoxCodeElement);
            timeCodesList.add(paradoxCode);
            log.info("Solved timezone paradox and collected code: {}", paradoxCode);
        }
        
        return this;
    }

    /**
     * Stabilize the timeline
     */
    @Step("Stabilize the timeline")
    public TimeWarpChallengePage stabilizeTimeline() {
        // Verify we have all required codes
        if (timeCodesList.size() < 3) {
            log.warn("Missing some time codes, only have {}/3", timeCodesList.size());
        }
        
        // Enter collected codes into the stabilizer
        WebElement pastCodeInput = timelineStabilizer.findElement(By.id("past-code-input"));
        WebElement futureCodeInput = timelineStabilizer.findElement(By.id("future-code-input"));
        WebElement paradoxCodeInput = timelineStabilizer.findElement(By.id("paradox-code-input"));
        WebElement stabilizeButton = timelineStabilizer.findElement(By.id("stabilize-btn"));
        
        // Set codes if available
        if (timeCodesList.size() > 0) {
            pastCodeInput.sendKeys(timeCodesList.get(0));
        }
        if (timeCodesList.size() > 1) {
            futureCodeInput.sendKeys(timeCodesList.get(1));
        }
        if (timeCodesList.size() > 2) {
            paradoxCodeInput.sendKeys(timeCodesList.get(2));
        }
        
        // Click stabilize
        click(stabilizeButton);
        
        // Wait for challenge code to appear
        waitForElementToBeVisible(challengeCode);
        
        log.info("Stabilized timeline with codes: {}", timeCodesList);
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
    public TimeWarpChallengePage validateSolution(String code) {
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
     * Reset browser time
     */
    @Step("Reset browser time")
    public TimeWarpChallengePage resetBrowserTime() {
        js.executeScript("Date = window.originalDate || Date;");
        log.info("Reset browser time");
        return this;
    }
    
    /**
     * Reset browser timezone
     */
    @Step("Reset browser timezone")
    public TimeWarpChallengePage resetBrowserTimezone() {
        js.executeScript(
            "if (window.originalGetTimezoneOffset) {" +
            "  Date.prototype.getTimezoneOffset = window.originalGetTimezoneOffset;" +
            "}"
        );
        log.info("Reset browser timezone");
        return this;
    }
    
    /**
     * Run complete Time Warp challenge
     */
    @Step("Run complete Time Warp challenge")
    public String runCompleteChallenge() {
        try {
            navigateToPage();
            startChallenge();
            getPastArtifact();
            getFutureTechnology();
            solveTimezoneParadox();
            stabilizeTimeline();
            String code = getChallengeCode();
            validateSolution(code);
            return code;
        } finally {
            // Always reset time and timezone to avoid affecting other tests
            resetBrowserTime();
            resetBrowserTimezone();
        }
    }
}