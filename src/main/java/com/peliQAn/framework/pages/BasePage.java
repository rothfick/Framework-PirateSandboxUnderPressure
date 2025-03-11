package com.peliQAn.framework.pages;

import com.peliQAn.framework.config.PropertyManager;
import com.peliQAn.framework.core.DriverFactory;
import com.peliQAn.framework.utils.ScreenshotUtils;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Base class for all Page Objects with common methods
 */
@Slf4j
public abstract class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final FluentWait<WebDriver> fluentWait;
    protected final Actions actions;
    protected final JavascriptExecutor js;
    protected final String baseUrl;

    protected BasePage() {
        this.driver = DriverFactory.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        this.actions = new Actions(driver);
        this.js = (JavascriptExecutor) driver;
        this.baseUrl = PropertyManager.getInstance().getProperty("app.baseUrl");
        
        PageFactory.initElements(driver, this);
    }

    /**
     * Navigate to a page URL
     */
    @Step("Navigate to URL: {url}")
    public void navigateTo(String url) {
        log.info("Navigating to URL: {}", url);
        driver.get(url);
    }

    /**
     * Navigate to the application base URL
     */
    @Step("Navigate to base URL")
    public void navigateToBaseUrl() {
        log.info("Navigating to base URL: {}", baseUrl);
        driver.get(baseUrl);
    }

    /**
     * Wait for an element to be clickable
     */
    protected WebElement waitForElementToBeClickable(WebElement element) {
        log.debug("Waiting for element to be clickable: {}", element);
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Wait for an element to be clickable by locator
     */
    protected WebElement waitForElementToBeClickable(By locator) {
        log.debug("Waiting for element to be clickable by locator: {}", locator);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Wait for an element to be visible
     */
    protected WebElement waitForElementToBeVisible(WebElement element) {
        log.debug("Waiting for element to be visible: {}", element);
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Wait for an element to be visible by locator
     */
    protected WebElement waitForElementToBeVisible(By locator) {
        log.debug("Waiting for element to be visible by locator: {}", locator);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Click on an element with wait
     */
    @Step("Click on element: {element}")
    protected void click(WebElement element) {
        try {
            waitForElementToBeClickable(element).click();
            log.debug("Clicked on element: {}", element);
        } catch (StaleElementReferenceException e) {
            log.warn("StaleElementReferenceException occurred, retrying click operation", e);
            WebElement refreshedElement = waitForElementToBeClickable(element);
            refreshedElement.click();
        } catch (ElementClickInterceptedException e) {
            log.warn("ElementClickInterceptedException occurred, trying JavaScript click", e);
            jsClick(element);
        }
    }

    /**
     * Click on an element with JavaScript
     */
    @Step("JavaScript click on element: {element}")
    protected void jsClick(WebElement element) {
        waitForElementToBeVisible(element);
        log.debug("JavaScript click on element: {}", element);
        js.executeScript("arguments[0].click();", element);
    }

    /**
     * Type text into an element
     */
    @Step("Type text: {text} into element: {element}")
    protected void type(WebElement element, String text) {
        WebElement visibleElement = waitForElementToBeVisible(element);
        visibleElement.clear();
        visibleElement.sendKeys(text);
        log.debug("Typed text: '{}' into element: {}", text, element);
    }

    /**
     * Clear text from an element
     */
    @Step("Clear text from element: {element}")
    protected void clear(WebElement element) {
        waitForElementToBeVisible(element).clear();
        log.debug("Cleared text from element: {}", element);
    }

    /**
     * Get text from an element
     */
    @Step("Get text from element: {element}")
    protected String getText(WebElement element) {
        String text = waitForElementToBeVisible(element).getText();
        log.debug("Got text: '{}' from element: {}", text, element);
        return text;
    }

    /**
     * Check if an element is displayed
     */
    @Step("Check if element is displayed: {element}")
    protected boolean isElementDisplayed(WebElement element) {
        try {
            boolean isDisplayed = element.isDisplayed();
            log.debug("Element is displayed: {} - {}", isDisplayed, element);
            return isDisplayed;
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            log.debug("Element is not displayed: {}", element);
            return false;
        }
    }

    /**
     * Check if an element is displayed with wait
     */
    @Step("Check if element is displayed with wait: {locator}")
    protected boolean isElementDisplayed(By locator, int timeoutInSeconds) {
        try {
            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            longWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            log.debug("Element is displayed after waiting: {}", locator);
            return true;
        } catch (TimeoutException e) {
            log.debug("Element is not displayed after waiting: {}", locator);
            return false;
        }
    }

    /**
     * Wait for element to disappear
     */
    @Step("Wait for element to disappear: {locator}")
    protected boolean waitForElementToDisappear(By locator, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            log.debug("Element did not disappear: {}", locator);
            return false;
        }
    }

    /**
     * Wait for a condition to be true
     */
    @Step("Wait for condition: {condition}")
    protected <T> T waitFor(Function<WebDriver, T> condition, int timeoutInSeconds, String conditionDescription) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return wait.until(condition);
        } catch (TimeoutException e) {
            log.error("Timeout waiting for condition: {}", conditionDescription);
            throw new TimeoutException("Timeout waiting for: " + conditionDescription, e);
        }
    }

    /**
     * Select option by visible text
     */
    @Step("Select option by text: {text} from dropdown: {element}")
    protected void selectByVisibleText(WebElement element, String text) {
        Select select = new Select(waitForElementToBeVisible(element));
        select.selectByVisibleText(text);
        log.debug("Selected option by text: '{}' from dropdown: {}", text, element);
    }

    /**
     * Select option by value
     */
    @Step("Select option by value: {value} from dropdown: {element}")
    protected void selectByValue(WebElement element, String value) {
        Select select = new Select(waitForElementToBeVisible(element));
        select.selectByValue(value);
        log.debug("Selected option by value: '{}' from dropdown: {}", value, element);
    }

    /**
     * Get selected option text from dropdown
     */
    @Step("Get selected option text from dropdown: {element}")
    protected String getSelectedOptionText(WebElement element) {
        Select select = new Select(waitForElementToBeVisible(element));
        String text = select.getFirstSelectedOption().getText();
        log.debug("Selected option text: '{}' from dropdown: {}", text, element);
        return text;
    }

    /**
     * Hover over an element
     */
    @Step("Hover over element: {element}")
    protected void hoverOver(WebElement element) {
        actions.moveToElement(waitForElementToBeVisible(element)).perform();
        log.debug("Hovering over element: {}", element);
    }

    /**
     * Drag and drop element to target
     */
    @Step("Drag element: {sourceElement} and drop to: {targetElement}")
    protected void dragAndDrop(WebElement sourceElement, WebElement targetElement) {
        actions.dragAndDrop(
                waitForElementToBeVisible(sourceElement),
                waitForElementToBeVisible(targetElement)
        ).perform();
        log.debug("Dragged element: {} and dropped to: {}", sourceElement, targetElement);
    }

    /**
     * Drag and drop by offset
     */
    @Step("Drag element: {element} and drop by offset: ({xOffset}, {yOffset})")
    protected void dragAndDropByOffset(WebElement element, int xOffset, int yOffset) {
        actions.dragAndDropBy(waitForElementToBeVisible(element), xOffset, yOffset).perform();
        log.debug("Dragged element: {} and dropped by offset: ({}, {})", element, xOffset, yOffset);
    }

    /**
     * Scroll to element
     */
    @Step("Scroll to element: {element}")
    protected void scrollToElement(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
        log.debug("Scrolled to element: {}", element);
    }

    /**
     * Take screenshot
     */
    @Step("Take screenshot with name: {screenshotName}")
    protected void takeScreenshot(String screenshotName) {
        ScreenshotUtils.takeScreenshot(driver, screenshotName);
    }

    /**
     * Check if page title contains expected text
     */
    @Step("Check if page title contains: {expectedTitle}")
    protected boolean pageTitleContains(String expectedTitle) {
        try {
            wait.until(ExpectedConditions.titleContains(expectedTitle));
            log.debug("Page title contains: '{}'", expectedTitle);
            return true;
        } catch (TimeoutException e) {
            log.debug("Page title does not contain: '{}'", expectedTitle);
            return false;
        }
    }

    /**
     * Wait for page to load completely
     */
    @Step("Wait for page to load completely")
    protected void waitForPageToLoad() {
        wait.until(driver -> js.executeScript("return document.readyState").equals("complete"));
        log.debug("Page loaded completely");
    }

    /**
     * Switch to frame
     */
    @Step("Switch to frame: {frame}")
    protected void switchToFrame(WebElement frame) {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frame));
        log.debug("Switched to frame: {}", frame);
    }
    
    /**
     * Switch to frame by index
     */
    @Step("Switch to frame by index: {index}")
    protected void switchToFrame(int index) {
        driver.switchTo().frame(index);
        log.debug("Switched to frame by index: {}", index);
    }

    /**
     * Switch to frame by name or ID
     */
    @Step("Switch to frame by name or ID: {nameOrId}")
    protected void switchToFrame(String nameOrId) {
        driver.switchTo().frame(nameOrId);
        log.debug("Switched to frame by name or ID: {}", nameOrId);
    }

    /**
     * Switch to default content
     */
    @Step("Switch to default content")
    protected void switchToDefaultContent() {
        driver.switchTo().defaultContent();
        log.debug("Switched to default content");
    }

    /**
     * Switch to parent frame
     */
    @Step("Switch to parent frame")
    protected void switchToParentFrame() {
        driver.switchTo().parentFrame();
        log.debug("Switched to parent frame");
    }

    /**
     * Handle alert
     */
    @Step("Handle alert with action: {accept}")
    protected String handleAlert(boolean accept) {
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        if (accept) {
            alert.accept();
            log.debug("Alert accepted with text: {}", alertText);
        } else {
            alert.dismiss();
            log.debug("Alert dismissed with text: {}", alertText);
        }
        return alertText;
    }

    /**
     * Handle prompt
     */
    @Step("Handle prompt with text: {inputText}")
    protected String handlePrompt(String inputText, boolean accept) {
        wait.until(ExpectedConditions.alertIsPresent());
        Alert prompt = driver.switchTo().alert();
        String promptText = prompt.getText();
        
        if (inputText != null) {
            prompt.sendKeys(inputText);
        }
        
        if (accept) {
            prompt.accept();
            log.debug("Prompt accepted with text: {}", promptText);
        } else {
            prompt.dismiss();
            log.debug("Prompt dismissed with text: {}", promptText);
        }
        return promptText;
    }

    /**
     * Get current window handle
     */
    protected String getCurrentWindowHandle() {
        return driver.getWindowHandle();
    }

    /**
     * Get all window handles
     */
    protected Set<String> getAllWindowHandles() {
        return driver.getWindowHandles();
    }

    /**
     * Switch to window by handle
     */
    @Step("Switch to window with handle: {windowHandle}")
    protected void switchToWindow(String windowHandle) {
        driver.switchTo().window(windowHandle);
        log.debug("Switched to window with handle: {}", windowHandle);
    }

    /**
     * Switch to new window
     */
    @Step("Switch to new window")
    protected String switchToNewWindow(String currentWindowHandle) {
        Set<String> windowHandles = getAllWindowHandles();
        String newWindowHandle = windowHandles.stream()
                .filter(handle -> !handle.equals(currentWindowHandle))
                .findFirst()
                .orElseThrow(() -> new NoSuchWindowException("No new window found"));
        
        switchToWindow(newWindowHandle);
        log.debug("Switched to new window with handle: {}", newWindowHandle);
        return newWindowHandle;
    }

    /**
     * Close current window and switch back to parent
     */
    @Step("Close current window and switch to parent: {parentWindowHandle}")
    protected void closeWindowAndSwitchToParent(String parentWindowHandle) {
        driver.close();
        switchToWindow(parentWindowHandle);
        log.debug("Closed current window and switched to parent: {}", parentWindowHandle);
    }

    /**
     * Get all elements by locator
     */
    @Step("Get all elements by locator: {locator}")
    protected List<WebElement> findElements(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        log.debug("Found {} elements with locator: {}", elements.size(), locator);
        return elements;
    }

    // Shadow DOM Methods

    /**
     * Get Shadow Root of an element
     */
    @Step("Get Shadow Root of element: {element}")
    protected SearchContext getShadowRoot(WebElement element) {
        return (SearchContext) js.executeScript("return arguments[0].shadowRoot", element);
    }

    /**
     * Find element within Shadow DOM
     */
    @Step("Find element in Shadow DOM with selector: {cssSelector}")
    protected WebElement findElementInShadowDOM(WebElement hostElement, String cssSelector) {
        SearchContext shadowRoot = getShadowRoot(hostElement);
        if (shadowRoot == null) {
            throw new NoSuchElementException("Shadow root is null for host element: " + hostElement);
        }
        return shadowRoot.findElement(By.cssSelector(cssSelector));
    }

    /**
     * Find elements within Shadow DOM
     */
    @Step("Find elements in Shadow DOM with selector: {cssSelector}")
    protected List<WebElement> findElementsInShadowDOM(WebElement hostElement, String cssSelector) {
        SearchContext shadowRoot = getShadowRoot(hostElement);
        if (shadowRoot == null) {
            throw new NoSuchElementException("Shadow root is null for host element: " + hostElement);
        }
        return shadowRoot.findElements(By.cssSelector(cssSelector));
    }

    /**
     * Find element in nested Shadow DOM
     */
    @Step("Find element in nested Shadow DOM")
    protected WebElement findElementInNestedShadowDOM(WebElement rootHost, String... selectors) {
        if (selectors.length == 0) {
            throw new IllegalArgumentException("At least one selector must be provided");
        }
        
        SearchContext currentContext = getShadowRoot(rootHost);
        WebElement element = null;
        
        for (int i = 0; i < selectors.length; i++) {
            if (i < selectors.length - 1) {
                // Find the next shadow host
                element = currentContext.findElement(By.cssSelector(selectors[i]));
                currentContext = getShadowRoot(element);
                if (currentContext == null) {
                    throw new NoSuchElementException("Shadow root is null for element: " + element);
                }
            } else {
                // Find the final element
                element = currentContext.findElement(By.cssSelector(selectors[i]));
            }
        }
        
        return element;
    }

    /**
     * Type text into an element in Shadow DOM
     */
    @Step("Type text: {text} into Shadow DOM element")
    protected void typeInShadowDOM(WebElement hostElement, String cssSelector, String text) {
        WebElement element = findElementInShadowDOM(hostElement, cssSelector);
        element.clear();
        element.sendKeys(text);
        log.debug("Typed text: '{}' into Shadow DOM element: {}", text, cssSelector);
    }

    /**
     * Click on an element in Shadow DOM
     */
    @Step("Click on element in Shadow DOM")
    protected void clickInShadowDOM(WebElement hostElement, String cssSelector) {
        WebElement element = findElementInShadowDOM(hostElement, cssSelector);
        element.click();
        log.debug("Clicked on element in Shadow DOM: {}", cssSelector);
    }

    /**
     * JavaScript click on an element in Shadow DOM
     */
    @Step("JavaScript click on element in Shadow DOM")
    protected void jsClickInShadowDOM(WebElement hostElement, String cssSelector) {
        WebElement element = findElementInShadowDOM(hostElement, cssSelector);
        js.executeScript("arguments[0].click();", element);
        log.debug("JavaScript clicked on element in Shadow DOM: {}", cssSelector);
    }

    /**
     * Get text from an element in Shadow DOM
     */
    @Step("Get text from element in Shadow DOM")
    protected String getTextFromShadowDOM(WebElement hostElement, String cssSelector) {
        WebElement element = findElementInShadowDOM(hostElement, cssSelector);
        String text = element.getText();
        log.debug("Got text: '{}' from element in Shadow DOM: {}", text, cssSelector);
        return text;
    }

    /**
     * Wait for Shadow DOM to be attached to host element
     */
    @Step("Wait for Shadow DOM to be attached to host element")
    protected void waitForShadowDOM(WebElement hostElement, int timeoutInSeconds) {
        WebDriverWait shadowWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        shadowWait.until(driver -> getShadowRoot(hostElement) != null);
        log.debug("Shadow DOM attached to host element: {}", hostElement);
    }

    /**
     * Wait for element to be visible in Shadow DOM
     */
    @Step("Wait for element to be visible in Shadow DOM")
    protected WebElement waitForElementInShadowDOM(WebElement hostElement, String cssSelector, int timeoutInSeconds) {
        waitForShadowDOM(hostElement, timeoutInSeconds);
        
        WebDriverWait shadowWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        return shadowWait.until(driver -> {
            try {
                WebElement element = findElementInShadowDOM(hostElement, cssSelector);
                return element.isDisplayed() ? element : null;
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                return null;
            }
        });
    }

    /**
     * Execute JavaScript in Shadow DOM context
     */
    @Step("Execute JavaScript in Shadow DOM context")
    protected Object executeJsInShadowDOM(WebElement hostElement, String script, Object... args) {
        String shadowRootScript = "return arguments[0].shadowRoot." + script;
        return js.executeScript(shadowRootScript, hostElement, args);
    }

    /**
     * Recursively find all Shadow DOM elements
     */
    @Step("Find all elements matching selector in all Shadow DOMs: {selector}")
    protected List<WebElement> findAllElementsInAllShadowDOMs(String selector) {
        String script = 
            "function getAllShadowElementsBySelector(selector) {" +
            "  function getElementsFromShadowDOM(root, selector, result = []) {" +
            "    if (!root) return result;" +
            "    if (root.shadowRoot) {" +
            "      const shadowElements = Array.from(root.shadowRoot.querySelectorAll(selector));" +
            "      result.push(...shadowElements);" +
            "      const allElements = Array.from(root.shadowRoot.querySelectorAll('*'));" +
            "      allElements.forEach(el => getElementsFromShadowDOM(el, selector, result));" +
            "    }" +
            "    return result;" +
            "  }" +
            "  const result = [];" +
            "  document.querySelectorAll('*').forEach(el => {" +
            "    getElementsFromShadowDOM(el, selector, result);" +
            "  });" +
            "  return result;" +
            "}" +
            "return getAllShadowElementsBySelector(arguments[0]);";
        
        List<WebElement> elements = (List<WebElement>) js.executeScript(script, selector);
        log.debug("Found {} elements in all Shadow DOMs matching: {}", elements.size(), selector);
        return elements;
    }

    // Canvas methods

    /**
     * Click on Canvas at coordinates
     */
    @Step("Click on Canvas at coordinates: ({x}, {y})")
    protected void clickOnCanvas(WebElement canvas, int x, int y) {
        actions.moveToElement(canvas, x, y).click().perform();
        log.debug("Clicked on Canvas at coordinates: ({}, {})", x, y);
    }

    /**
     * Drag on Canvas from point to point
     */
    @Step("Drag on Canvas from ({startX}, {startY}) to ({endX}, {endY})")
    protected void dragOnCanvas(WebElement canvas, int startX, int startY, int endX, int endY) {
        actions.moveToElement(canvas, startX, startY)
               .clickAndHold()
               .moveByOffset(endX - startX, endY - startY)
               .release()
               .perform();
        log.debug("Dragged on Canvas from ({}, {}) to ({}, {})", startX, startY, endX, endY);
    }

    /**
     * Get Canvas context and execute function
     */
    @Step("Execute function on Canvas context")
    protected Object executeCanvasFunction(WebElement canvas, String functionScript) {
        String script = "const canvas = arguments[0];" +
                        "const ctx = canvas.getContext('2d');" +
                        functionScript;
        return js.executeScript(script, canvas);
    }

    /**
     * Get pixel data from Canvas
     */
    @Step("Get pixel data from Canvas at ({x}, {y})")
    protected Object getCanvasPixel(WebElement canvas, int x, int y) {
        String script = "const canvas = arguments[0];" +
                        "const ctx = canvas.getContext('2d');" +
                        "return ctx.getImageData(arguments[1], arguments[2], 1, 1).data;";
        return js.executeScript(script, canvas, x, y);
    }

    // Time manipulation methods

    /**
     * Override JavaScript date
     */
    @Step("Override JavaScript date to: {dateString}")
    protected void overrideJavaScriptDate(String dateString) {
        String script = "const originalDate = Date;" +
                        "const customDate = new Date('" + dateString + "');" +
                        "Date = class extends originalDate {" +
                        "  constructor() {" +
                        "    super();" +
                        "    return customDate;" +
                        "  }" +
                        "  static now() {" +
                        "    return customDate.getTime();" +
                        "  }" +
                        "}";
        js.executeScript(script);
        log.debug("Overridden JavaScript date to: {}", dateString);
    }

    /**
     * Reset JavaScript date
     */
    @Step("Reset JavaScript date")
    protected void resetJavaScriptDate() {
        js.executeScript("Date = window.originalDate || Date;");
        log.debug("Reset JavaScript date");
    }

    /**
     * Set timezone offset
     */
    @Step("Set timezone offset to: {offsetMinutes}")
    protected void setTimezoneOffset(int offsetMinutes) {
        // Store original methods
        js.executeScript(
            "if (!window.originalGetTimezoneOffset) {" +
            "  window.originalGetTimezoneOffset = Date.prototype.getTimezoneOffset;" +
            "}" +
            "Date.prototype.getTimezoneOffset = function() { return " + offsetMinutes + "; };"
        );
        log.debug("Set timezone offset to: {} minutes", offsetMinutes);
    }

    /**
     * Reset timezone offset
     */
    @Step("Reset timezone offset")
    protected void resetTimezoneOffset() {
        js.executeScript(
            "if (window.originalGetTimezoneOffset) {" +
            "  Date.prototype.getTimezoneOffset = window.originalGetTimezoneOffset;" +
            "}"
        );
        log.debug("Reset timezone offset");
    }
}