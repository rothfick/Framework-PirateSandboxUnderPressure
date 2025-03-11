package com.peliQAn.framework.pages.hardcore;

import com.peliQAn.framework.pages.BasePage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Page object for Canvas Treasure Map Challenge (/test-ui/hardcore/canvas-map)
 */
@Slf4j
public class CanvasMapChallengePage extends BasePage {

    private static final String PAGE_URL = "/test-ui/hardcore/canvas-map";

    // Main elements
    @FindBy(id = "start-challenge-btn")
    private WebElement startChallengeButton;
    
    @FindBy(id = "treasure-map")
    private WebElement treasureMapCanvas;
    
    @FindBy(id = "map-context")
    private WebElement mapContext;
    
    @FindBy(id = "dig-btn")
    private WebElement digButton;
    
    @FindBy(id = "treasure-found")
    private WebElement treasureFound;
    
    @FindBy(id = "coordinates-display")
    private WebElement coordinatesDisplay;
    
    @FindBy(id = "challenge-result")
    private WebElement challengeResult;
    
    @FindBy(id = "challenge-code")
    private WebElement challengeCode;
    
    @FindBy(id = "solution-input")
    private WebElement solutionInput;
    
    @FindBy(id = "validate-btn")
    private WebElement validateButton;
    
    // Store the current coordinates
    private int currentX;
    private int currentY;
    
    // Store X marks locations
    private final List<Point> xMarksLocations = new ArrayList<>();

    /**
     * Navigate to Canvas Map Challenge page
     */
    @Step("Navigate to Canvas Map Challenge page")
    public CanvasMapChallengePage navigateToPage() {
        navigateTo(baseUrl + PAGE_URL);
        waitForPageToLoad();
        log.info("Navigated to Canvas Map Challenge page");
        return this;
    }

    /**
     * Start the challenge
     */
    @Step("Start the Canvas Map challenge")
    public CanvasMapChallengePage startChallenge() {
        click(startChallengeButton);
        waitForElementToBeVisible(treasureMapCanvas);
        log.info("Started Canvas Map challenge");
        return this;
    }

    /**
     * Find X marks on the map
     */
    @Step("Find X marks on the treasure map")
    public CanvasMapChallengePage findXMarksOnMap() {
        // This requires canvas pixel data analysis
        // We'll use JavaScript to scan the canvas for X marks
        // In a real implementation, this would analyze the canvas image data
        
        // Example implementation using JavaScript to detect X marks (simplified)
        String detectXMarksScript = 
            "const canvas = arguments[0];" +
            "const ctx = canvas.getContext('2d');" +
            "const width = canvas.width;" +
            "const height = canvas.height;" +
            "const imageData = ctx.getImageData(0, 0, width, height);" +
            "const data = imageData.data;" +
            "const xMarks = [];" +
            
            // This is a simplified algorithm to detect X marks
            // In reality, you would need a more sophisticated image processing algorithm
            "for (let y = 0; y < height; y += 5) {" +
            "  for (let x = 0; x < width; x += 5) {" +
            "    const index = (y * width + x) * 4;" +
            "    // Look for red color (X marks are often in red)" +
            "    if (data[index] > 200 && data[index+1] < 100 && data[index+2] < 100) {" +
            "      // Check surrounding pixels to confirm it's an X mark" +
            "      // This is a simplified approach" +
            "      xMarks.push({x: x, y: y});" +
            "      // Skip ahead to avoid detecting the same X mark multiple times" +
            "      x += 20;" +
            "    }" +
            "  }" +
            "}" +
            
            "return xMarks;";
        
        List<Map<String, Long>> xMarks = (List<Map<String, Long>>) js.executeScript(detectXMarksScript, treasureMapCanvas);
        
        // If we couldn't detect any X marks, use default locations for testing
        if (xMarks == null || xMarks.isEmpty()) {
            log.warn("Could not detect X marks, using default locations");
            // Add some default locations to test
            xMarksLocations.add(new Point(100, 100));
            xMarksLocations.add(new Point(200, 150));
            xMarksLocations.add(new Point(150, 200));
        } else {
            // Convert detected marks to Point objects
            for (Map<String, Long> mark : xMarks) {
                int x = mark.get("x").intValue();
                int y = mark.get("y").intValue();
                xMarksLocations.add(new Point(x, y));
                log.info("Found X mark at coordinates: ({}, {})", x, y);
            }
        }
        
        log.info("Found {} X marks on the map", xMarksLocations.size());
        return this;
    }

    /**
     * Navigate to a specific point on the map
     */
    @Step("Navigate to point ({x}, {y}) on the map")
    public CanvasMapChallengePage navigateToPoint(int x, int y) {
        // Move to the specified coordinates on the canvas
        actions.moveToElement(treasureMapCanvas, x, y).perform();
        
        // Update current coordinates
        currentX = x;
        currentY = y;
        
        // Optional: Wait for coordinate display to update
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("Navigated to point ({}, {}) on the map", x, y);
        return this;
    }

    /**
     * Dig at the current location
     */
    @Step("Dig at current location ({x}, {y})")
    public CanvasMapChallengePage digAtCurrentLocation() {
        // First make sure we're at the right coordinates by clicking on the canvas
        clickOnCanvas(treasureMapCanvas, currentX, currentY);
        
        // Now click the dig button
        click(digButton);
        
        // Check if treasure was found
        try {
            if (isElementDisplayed(treasureFound, 1)) {
                log.info("Treasure found at coordinates: ({}, {})", currentX, currentY);
            }
        } catch (Exception e) {
            log.info("No treasure at coordinates: ({}, {})", currentX, currentY);
        }
        
        return this;
    }

    /**
     * Dig at all X marks on the map
     */
    @Step("Dig at all X marks on the map")
    public CanvasMapChallengePage digAtAllXMarks() {
        // If we haven't found X marks yet, find them
        if (xMarksLocations.isEmpty()) {
            findXMarksOnMap();
        }
        
        // Dig at each location
        for (Point point : xMarksLocations) {
            navigateToPoint(point.getX(), point.getY());
            digAtCurrentLocation();
            
            // Give a short pause between digs
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        log.info("Completed digging at all X marks");
        return this;
    }

    /**
     * Collect treasure and get the code
     */
    @Step("Collect treasure and get the code")
    public String collectTreasureAndGetCode() {
        // Verify treasure was found
        waitForElementToBeVisible(treasureFound);
        
        // Look for any 'collect' button
        WebElement collectButton = null;
        try {
            collectButton = driver.findElement(By.id("collect-treasure-btn"));
        } catch (Exception e) {
            log.warn("Collect button not found, trying to proceed anyway");
        }
        
        // Click collect button if found
        if (collectButton != null && collectButton.isDisplayed()) {
            click(collectButton);
        }
        
        // Wait for challenge code to appear
        waitForElementToBeVisible(challengeCode);
        
        // Get the code
        String code = getText(challengeCode);
        log.info("Collected treasure and got code: {}", code);
        return code;
    }

    /**
     * Validate the solution with the code
     */
    @Step("Validate the solution with code: {code}")
    public CanvasMapChallengePage validateSolution(String code) {
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
     * Run complete Canvas Map challenge
     */
    @Step("Run complete Canvas Map challenge")
    public String runCompleteChallenge() {
        navigateToPage();
        startChallenge();
        findXMarksOnMap();
        digAtAllXMarks();
        String code = collectTreasureAndGetCode();
        validateSolution(code);
        return code;
    }
    
    /**
     * Explore map by moving cursor to find X marks
     * This is an alternative approach that scans the canvas visually
     */
    @Step("Explore map by moving cursor to find X marks")
    public CanvasMapChallengePage exploreMapByMovingCursor() {
        int width = treasureMapCanvas.getSize().getWidth();
        int height = treasureMapCanvas.getSize().getHeight();
        
        // Scan the canvas in a grid pattern
        int gridSize = 20; // Pixels between scan points
        
        for (int y = 0; y < height; y += gridSize) {
            for (int x = 0; x < width; x += gridSize) {
                navigateToPoint(x, y);
                
                // Check if we found an interesting point by examining coordinate display
                String coordinates = coordinatesDisplay.getText();
                if (coordinates.contains("X") || coordinates.contains("MARK")) {
                    log.info("Found interesting point at ({}, {}): {}", x, y, coordinates);
                    xMarksLocations.add(new Point(x, y));
                }
            }
        }
        
        log.info("Completed exploration of map, found {} interesting points", xMarksLocations.size());
        return this;
    }
}