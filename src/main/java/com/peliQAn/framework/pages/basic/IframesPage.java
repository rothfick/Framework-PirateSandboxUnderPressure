package com.peliQAn.framework.pages.basic;

import com.peliQAn.framework.pages.BasePage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page object for iframes page (/test-ui/iframes)
 */
@Slf4j
public class IframesPage extends BasePage {

    private static final String PAGE_URL = "/test-ui/iframes";

    // Simple iframe
    @FindBy(id = "simple-iframe")
    private WebElement simpleIframe;

    // Nested iframes
    @FindBy(id = "parent-iframe")
    private WebElement parentIframe;

    // Dynamic iframe
    @FindBy(id = "load-dynamic-iframe-btn")
    private WebElement loadDynamicIframeBtn;

    @FindBy(id = "dynamic-iframe-container")
    private WebElement dynamicIframeContainer;

    // Cross-origin iframe
    @FindBy(id = "cross-origin-iframe")
    private WebElement crossOriginIframe;

    /**
     * Navigate to iframes page
     */
    @Step("Navigate to iframes page")
    public IframesPage navigateToPage() {
        navigateTo(baseUrl + PAGE_URL);
        waitForPageToLoad();
        log.info("Navigated to iframes page");
        return this;
    }

    /**
     * Switch to simple iframe and get text
     */
    @Step("Switch to simple iframe and get text")
    public String getSimpleIframeText() {
        switchToFrame(simpleIframe);
        WebElement textElement = waitForElementToBeVisible(By.id("iframe-text"));
        String text = getText(textElement);
        switchToDefaultContent();
        log.info("Got text from simple iframe: {}", text);
        return text;
    }

    /**
     * Enter text in simple iframe
     */
    @Step("Enter text in simple iframe: {text}")
    public IframesPage enterTextInSimpleIframe(String text) {
        switchToFrame(simpleIframe);
        WebElement inputElement = waitForElementToBeVisible(By.id("iframe-input"));
        type(inputElement, text);
        switchToDefaultContent();
        log.info("Entered text in simple iframe: {}", text);
        return this;
    }

    /**
     * Click button in simple iframe
     */
    @Step("Click button in simple iframe")
    public IframesPage clickButtonInSimpleIframe() {
        switchToFrame(simpleIframe);
        WebElement buttonElement = waitForElementToBeVisible(By.id("iframe-button"));
        click(buttonElement);
        switchToDefaultContent();
        log.info("Clicked button in simple iframe");
        return this;
    }

    /**
     * Navigate through nested iframes and get text
     */
    @Step("Navigate through nested iframes and get text")
    public String getNestedIframesText() {
        // Switch to parent iframe
        switchToFrame(parentIframe);
        
        // Find and switch to child iframe
        WebElement childIframe = waitForElementToBeVisible(By.id("child-iframe"));
        switchToFrame(childIframe);
        
        // Get text from child iframe
        WebElement textElement = waitForElementToBeVisible(By.id("nested-iframe-text"));
        String text = getText(textElement);
        
        // Switch back to default content
        switchToDefaultContent();
        
        log.info("Got text from nested iframes: {}", text);
        return text;
    }

    /**
     * Enter text in nested iframe
     */
    @Step("Enter text in nested iframe: {text}")
    public IframesPage enterTextInNestedIframe(String text) {
        // Switch to parent iframe
        switchToFrame(parentIframe);
        
        // Find and switch to child iframe
        WebElement childIframe = waitForElementToBeVisible(By.id("child-iframe"));
        switchToFrame(childIframe);
        
        // Enter text in input field
        WebElement inputElement = waitForElementToBeVisible(By.id("nested-iframe-input"));
        type(inputElement, text);
        
        // Switch back to default content
        switchToDefaultContent();
        
        log.info("Entered text in nested iframe: {}", text);
        return this;
    }

    /**
     * Load dynamic iframe
     */
    @Step("Load dynamic iframe")
    public IframesPage loadDynamicIframe() {
        click(loadDynamicIframeBtn);
        waitForElementToBeVisible(By.id("dynamic-iframe"));
        log.info("Loaded dynamic iframe");
        return this;
    }

    /**
     * Get text from dynamic iframe
     */
    @Step("Get text from dynamic iframe")
    public String getDynamicIframeText() {
        WebElement dynamicIframe = waitForElementToBeVisible(By.id("dynamic-iframe"));
        
        switchToFrame(dynamicIframe);
        WebElement textElement = waitForElementToBeVisible(By.id("dynamic-iframe-text"));
        String text = getText(textElement);
        switchToDefaultContent();
        
        log.info("Got text from dynamic iframe: {}", text);
        return text;
    }

    /**
     * Switch to cross-origin iframe
     * Note: Due to same-origin policy, we might have limited interaction capabilities
     */
    @Step("Switch to cross-origin iframe")
    public IframesPage switchToCrossOriginIframe() {
        switchToFrame(crossOriginIframe);
        log.info("Switched to cross-origin iframe");
        return this;
    }

    /**
     * Switch back to default content
     */
    @Step("Switch back to default content")
    public IframesPage switchBackToDefaultContent() {
        switchToDefaultContent();
        log.info("Switched back to default content");
        return this;
    }

    /**
     * Verify presence of element in current frame
     */
    @Step("Verify presence of element with ID: {elementId}")
    public boolean isElementPresentInCurrentFrame(String elementId) {
        try {
            WebElement element = driver.findElement(By.id(elementId));
            boolean isPresent = element.isDisplayed();
            log.info("Element with ID {} is present: {}", elementId, isPresent);
            return isPresent;
        } catch (Exception e) {
            log.info("Element with ID {} is not present", elementId);
            return false;
        }
    }
}