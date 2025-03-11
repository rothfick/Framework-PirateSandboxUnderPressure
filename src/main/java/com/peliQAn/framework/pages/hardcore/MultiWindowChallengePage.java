package com.peliQAn.framework.pages.hardcore;

import com.peliQAn.framework.pages.BasePage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Page object for Multi-Window Treasure Hunt Challenge (/test-ui/hardcore/multi-window)
 */
@Slf4j
public class MultiWindowChallengePage extends BasePage {

    private static final String PAGE_URL = "/test-ui/hardcore/multi-window";

    // Main elements
    @FindBy(id = "start-challenge-btn")
    private WebElement startChallengeButton;

    @FindBy(css = ".challenge-description")
    private WebElement challengeDescription;

    @FindBy(css = ".clue-container")
    private WebElement clueContainer;

    @FindBy(id = "open-window-btn")
    private WebElement openWindowButton;

    @FindBy(id = "challenge-result")
    private WebElement challengeResult;

    @FindBy(id = "challenge-code")
    private WebElement challengeCode;

    @FindBy(id = "solution-input")
    private WebElement solutionInput;

    @FindBy(id = "validate-btn")
    private WebElement validateButton;

    // Map to store information from each window
    private final Map<String, String> collectedClues = new HashMap<>();
    private String mainWindowHandle;

    /**
     * Navigate to Multi-Window Challenge page
     */
    @Step("Navigate to Multi-Window Challenge page")
    public MultiWindowChallengePage navigateToPage() {
        navigateTo(baseUrl + PAGE_URL);
        waitForPageToLoad();
        mainWindowHandle = driver.getWindowHandle();
        log.info("Navigated to Multi-Window Challenge page");
        return this;
    }

    /**
     * Start the challenge
     */
    @Step("Start the Multi-Window challenge")
    public MultiWindowChallengePage startChallenge() {
        click(startChallengeButton);
        waitForElementToBeVisible(openWindowButton);
        log.info("Started Multi-Window challenge");
        return this;
    }

    /**
     * Open the first treasure window
     */
    @Step("Open first treasure window")
    public MultiWindowChallengePage openFirstWindow() {
        click(openWindowButton);
        // Wait for new window
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        log.info("Opened first treasure window");
        return this;
    }

    /**
     * Navigate through all windows collecting clues
     */
    @Step("Navigate through all windows collecting clues")
    public MultiWindowChallengePage navigateThroughAllWindows() {
        // Start from the first window
        openFirstWindow();
        
        int windowCounter = 1;
        Set<String> processedWindows = new HashSet<>();
        processedWindows.add(mainWindowHandle);
        
        // Keep processing windows until no new ones appear
        while (true) {
            Set<String> currentWindows = driver.getWindowHandles();
            List<String> unprocessedWindows = currentWindows.stream()
                .filter(handle -> !processedWindows.contains(handle))
                .collect(Collectors.toList());
            
            if (unprocessedWindows.isEmpty()) {
                break;
            }
            
            for (String windowHandle : unprocessedWindows) {
                processWindow(windowHandle, windowCounter++);
                processedWindows.add(windowHandle);
            }
        }
        
        // Return to main window
        driver.switchTo().window(mainWindowHandle);
        log.info("Collected clues from all windows: {}", collectedClues);
        return this;
    }
    
    /**
     * Process a single window to collect clues
     */
    private void processWindow(String windowHandle, int windowNumber) {
        // Switch to the window
        driver.switchTo().window(windowHandle);
        log.info("Processing window {}: {}", windowNumber, driver.getTitle());
        
        try {
            // Wait for content to load
            WebElement clueElement = waitForElementToBeVisible(By.cssSelector(".treasure-clue"));
            String clue = clueElement.getText();
            collectedClues.put("Window " + windowNumber, clue);
            log.info("Collected clue from window {}: {}", windowNumber, clue);
            
            // Check if there's a button to open another window
            List<WebElement> nextButtons = driver.findElements(By.cssSelector(".open-next-window-btn"));
            if (!nextButtons.isEmpty() && nextButtons.get(0).isDisplayed()) {
                WebElement nextButton = nextButtons.get(0);
                String currentWindowsCount = String.valueOf(driver.getWindowHandles().size());
                nextButton.click();
                // Wait for new window to open
                wait.until(ExpectedConditions.numberOfWindowsToBeGreaterThan(Integer.parseInt(currentWindowsCount)));
                log.info("Opened next window from window {}", windowNumber);
            }
            
            // Complete any tasks in this window if required
            completeTasks(windowNumber);
            
        } catch (Exception e) {
            log.error("Error processing window {}: {}", windowNumber, e.getMessage());
        }
    }
    
    /**
     * Complete any tasks required in the window
     */
    private void completeTasks(int windowNumber) {
        try {
            // Look for input fields that need to be filled
            List<WebElement> inputFields = driver.findElements(By.cssSelector("input[required]"));
            for (WebElement input : inputFields) {
                // If there's a clue about what to enter, use it, otherwise use default
                input.sendKeys("treasure" + windowNumber);
                log.info("Filled input field in window {}", windowNumber);
            }
            
            // Look for buttons to click (excluding next window buttons)
            List<WebElement> taskButtons = driver.findElements(
                By.cssSelector("button:not(.open-next-window-btn)"));
            
            for (WebElement button : taskButtons) {
                if (button.isDisplayed() && button.isEnabled() && 
                    !button.getAttribute("id").equals("validate-btn")) {
                    button.click();
                    log.info("Clicked task button in window {}: {}", windowNumber, button.getText());
                    // Wait a moment for any changes
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("Error completing tasks in window {}: {}", windowNumber, e.getMessage());
        }
    }

    /**
     * Solve the final puzzle with collected clues
     */
    @Step("Solve the final puzzle with collected clues")
    public MultiWindowChallengePage solveFinalPuzzle() {
        // Switch back to main window if not already there
        driver.switchTo().window(mainWindowHandle);
        
        // Find the solution form
        WebElement solutionForm = waitForElementToBeVisible(By.id("solution-form"));
        
        // Analyze clues to find the solution pattern
        String solution = analyzeCluesToFindSolution();
        
        // Enter solution
        WebElement finalInput = solutionForm.findElement(By.id("final-answer"));
        finalInput.clear();
        finalInput.sendKeys(solution);
        
        // Submit solution
        WebElement submitButton = solutionForm.findElement(By.id("submit-solution"));
        click(submitButton);
        
        // Wait for result
        waitForElementToBeVisible(challengeCode);
        
        log.info("Submitted final solution: {}", solution);
        return this;
    }
    
    /**
     * Analyze collected clues to find the solution
     */
    private String analyzeCluesToFindSolution() {
        // Placeholder for solution logic
        // In a real implementation, this would parse the clues and determine the correct answer
        
        // Example implementation:
        StringBuilder solution = new StringBuilder();
        List<String> sortedClues = new ArrayList<>();
        
        // Sort clues by window number
        for (int i = 1; i <= collectedClues.size(); i++) {
            String clue = collectedClues.get("Window " + i);
            if (clue != null) {
                sortedClues.add(clue);
            }
        }
        
        // Combine first letters of each clue
        for (String clue : sortedClues) {
            if (!clue.isEmpty()) {
                solution.append(clue.charAt(0));
            }
        }
        
        // Fallback if we couldn't determine a solution
        if (solution.length() == 0) {
            log.warn("Could not determine solution from clues, using default");
            return "TREASURE";
        }
        
        log.info("Analyzed clues to find solution: {}", solution);
        return solution.toString();
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
    public MultiWindowChallengePage validateSolution(String code) {
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
     * Close all windows except main window
     */
    @Step("Close all windows except main window")
    public MultiWindowChallengePage closeAllWindows() {
        Set<String> handles = driver.getWindowHandles();
        
        for (String handle : handles) {
            if (!handle.equals(mainWindowHandle)) {
                driver.switchTo().window(handle);
                driver.close();
                log.info("Closed window: {}", handle);
            }
        }
        
        driver.switchTo().window(mainWindowHandle);
        log.info("Switched back to main window");
        return this;
    }
    
    /**
     * Run complete Multi-Window challenge
     */
    @Step("Run complete Multi-Window challenge")
    public String runCompleteChallenge() {
        navigateToPage();
        startChallenge();
        navigateThroughAllWindows();
        solveFinalPuzzle();
        String code = getChallengeCode();
        validateSolution(code);
        closeAllWindows();
        return code;
    }
}