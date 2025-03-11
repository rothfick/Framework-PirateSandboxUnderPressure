package com.peliQAn.framework.pages.basic;

import com.peliQAn.framework.pages.BasePage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.Set;

/**
 * Page object for Windows page (/test-ui/windows)
 */
@Slf4j
public class WindowsPage extends BasePage {

    private static final String PAGE_URL = "/test-ui/windows";

    @FindBy(id = "new-window-btn")
    private WebElement newWindowBtn;

    @FindBy(id = "new-tab-btn")
    private WebElement newTabBtn;

    @FindBy(id = "delayed-window-btn")
    private WebElement delayedWindowBtn;

    @FindBy(id = "window-with-form-btn")
    private WebElement windowWithFormBtn;

    @FindBy(id = "data-transfer-input")
    private WebElement dataTransferInput;

    @FindBy(id = "send-data-btn")
    private WebElement sendDataBtn;

    @FindBy(id = "received-data")
    private WebElement receivedData;

    /**
     * Navigate to Windows page
     */
    @Step("Navigate to Windows page")
    public WindowsPage navigateToPage() {
        navigateTo(baseUrl + PAGE_URL);
        waitForPageToLoad();
        log.info("Navigated to Windows page");
        return this;
    }

    /**
     * Open new window
     */
    @Step("Open new window")
    public String openNewWindow() {
        String originalWindowHandle = getCurrentWindowHandle();
        click(newWindowBtn);
        
        // Wait for new window to open and switch to it
        String newWindowHandle = waitFor(driver -> {
            Set<String> windowHandles = driver.getWindowHandles();
            return windowHandles.size() > 1 ? 
                   windowHandles.stream().filter(handle -> !handle.equals(originalWindowHandle)).findFirst().orElse(null) : 
                   null;
        }, 10, "new window to open");
        
        switchToWindow(newWindowHandle);
        log.info("Opened and switched to new window with handle: {}", newWindowHandle);
        return newWindowHandle;
    }

    /**
     * Open new tab
     */
    @Step("Open new tab")
    public String openNewTab() {
        String originalWindowHandle = getCurrentWindowHandle();
        click(newTabBtn);
        
        // Wait for new tab to open and switch to it
        String newTabHandle = waitFor(driver -> {
            Set<String> windowHandles = driver.getWindowHandles();
            return windowHandles.size() > 1 ? 
                   windowHandles.stream().filter(handle -> !handle.equals(originalWindowHandle)).findFirst().orElse(null) : 
                   null;
        }, 10, "new tab to open");
        
        switchToWindow(newTabHandle);
        log.info("Opened and switched to new tab with handle: {}", newTabHandle);
        return newTabHandle;
    }

    /**
     * Open delayed window
     */
    @Step("Open delayed window")
    public String openDelayedWindow() {
        String originalWindowHandle = getCurrentWindowHandle();
        click(delayedWindowBtn);
        
        // Wait for delayed window to open and switch to it
        String newWindowHandle = waitFor(driver -> {
            Set<String> windowHandles = driver.getWindowHandles();
            return windowHandles.size() > 1 ? 
                   windowHandles.stream().filter(handle -> !handle.equals(originalWindowHandle)).findFirst().orElse(null) : 
                   null;
        }, 15, "delayed window to open");
        
        switchToWindow(newWindowHandle);
        log.info("Opened and switched to delayed window with handle: {}", newWindowHandle);
        return newWindowHandle;
    }

    /**
     * Open window with form
     */
    @Step("Open window with form")
    public String openWindowWithForm() {
        String originalWindowHandle = getCurrentWindowHandle();
        click(windowWithFormBtn);
        
        // Wait for window with form to open and switch to it
        String newWindowHandle = waitFor(driver -> {
            Set<String> windowHandles = driver.getWindowHandles();
            return windowHandles.size() > 1 ? 
                   windowHandles.stream().filter(handle -> !handle.equals(originalWindowHandle)).findFirst().orElse(null) : 
                   null;
        }, 10, "window with form to open");
        
        switchToWindow(newWindowHandle);
        log.info("Opened and switched to window with form, handle: {}", newWindowHandle);
        return newWindowHandle;
    }

    /**
     * Fill form in child window
     */
    @Step("Fill form in child window with name: {name}, email: {email}")
    public WindowsPage fillFormInChildWindow(String name, String email) {
        WebElement nameInput = waitForElementToBeVisible(By.id("name"));
        WebElement emailInput = waitForElementToBeVisible(By.id("email"));
        WebElement submitButton = waitForElementToBeVisible(By.id("submit-form"));
        
        type(nameInput, name);
        type(emailInput, email);
        click(submitButton);
        
        log.info("Filled form in child window with name: {}, email: {}", name, email);
        return this;
    }

    /**
     * Switch back to parent window
     */
    @Step("Switch back to parent window")
    public WindowsPage switchBackToParentWindow(String parentWindowHandle) {
        switchToWindow(parentWindowHandle);
        log.info("Switched back to parent window with handle: {}", parentWindowHandle);
        return this;
    }

    /**
     * Close current window and switch to parent
     */
    @Step("Close current window and switch to parent")
    public WindowsPage closeWindowAndSwitchToParent(String parentWindowHandle) {
        driver.close();
        switchToWindow(parentWindowHandle);
        log.info("Closed current window and switched to parent with handle: {}", parentWindowHandle);
        return this;
    }

    /**
     * Enter data for transfer
     */
    @Step("Enter data for transfer: {data}")
    public WindowsPage enterDataForTransfer(String data) {
        type(dataTransferInput, data);
        log.info("Entered data for transfer: {}", data);
        return this;
    }

    /**
     * Send data to new window
     */
    @Step("Send data to new window")
    public String sendDataToNewWindow() {
        String originalWindowHandle = getCurrentWindowHandle();
        click(sendDataBtn);
        
        // Wait for new window to open and switch to it
        String newWindowHandle = waitFor(driver -> {
            Set<String> windowHandles = driver.getWindowHandles();
            return windowHandles.size() > 1 ? 
                   windowHandles.stream().filter(handle -> !handle.equals(originalWindowHandle)).findFirst().orElse(null) : 
                   null;
        }, 10, "window for data transfer to open");
        
        switchToWindow(newWindowHandle);
        log.info("Sent data to new window with handle: {}", newWindowHandle);
        return newWindowHandle;
    }

    /**
     * Get received data in child window
     */
    @Step("Get received data in child window")
    public String getReceivedDataInChildWindow() {
        WebElement receivedDataElement = waitForElementToBeVisible(By.id("received-data"));
        String data = getText(receivedDataElement);
        log.info("Got received data in child window: {}", data);
        return data;
    }

    /**
     * Get current window title
     */
    @Step("Get current window title")
    public String getCurrentWindowTitle() {
        String title = driver.getTitle();
        log.info("Current window title: {}", title);
        return title;
    }

    /**
     * Get current window URL
     */
    @Step("Get current window URL")
    public String getCurrentWindowURL() {
        String url = driver.getCurrentUrl();
        log.info("Current window URL: {}", url);
        return url;
    }

    /**
     * Check if element exists in current window
     */
    @Step("Check if element with ID {elementId} exists in current window")
    public boolean elementExistsInCurrentWindow(String elementId) {
        try {
            return driver.findElement(By.id(elementId)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}