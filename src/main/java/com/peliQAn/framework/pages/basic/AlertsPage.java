package com.peliQAn.framework.pages.basic;

import com.peliQAn.framework.pages.BasePage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page object for Alerts page (/test-ui/alerts)
 */
@Slf4j
public class AlertsPage extends BasePage {

    private static final String PAGE_URL = "/test-ui/alerts";

    // Standard JavaScript alerts
    @FindBy(id = "alert-btn")
    private WebElement alertBtn;

    @FindBy(id = "confirm-btn")
    private WebElement confirmBtn;

    @FindBy(id = "prompt-btn")
    private WebElement promptBtn;

    @FindBy(id = "confirm-result")
    private WebElement confirmResult;

    @FindBy(id = "prompt-result")
    private WebElement promptResult;

    // Timed alerts
    @FindBy(id = "timed-alert-btn")
    private WebElement timedAlertBtn;

    // Custom notifications
    @FindBy(id = "success-notification-btn")
    private WebElement successNotificationBtn;

    @FindBy(id = "error-notification-btn")
    private WebElement errorNotificationBtn;

    @FindBy(id = "warning-notification-btn")
    private WebElement warningNotificationBtn;

    @FindBy(id = "info-notification-btn")
    private WebElement infoNotificationBtn;

    @FindBy(css = ".toast")
    private WebElement toast;

    @FindBy(css = ".toast .toast-close")
    private WebElement toastCloseBtn;

    /**
     * Navigate to Alerts page
     */
    @Step("Navigate to Alerts page")
    public AlertsPage navigateToPage() {
        navigateTo(baseUrl + PAGE_URL);
        waitForPageToLoad();
        log.info("Navigated to Alerts page");
        return this;
    }

    /**
     * Click alert button and handle alert
     */
    @Step("Click alert button and handle alert")
    public String triggerAndAcceptAlert() {
        click(alertBtn);
        String alertText = handleAlert(true);
        log.info("Triggered and accepted alert with text: {}", alertText);
        return alertText;
    }

    /**
     * Click confirm button and accept confirm dialog
     */
    @Step("Click confirm button and accept confirm dialog")
    public String triggerAndAcceptConfirm() {
        click(confirmBtn);
        String confirmText = handleAlert(true);
        log.info("Triggered and accepted confirm dialog with text: {}", confirmText);
        return confirmText;
    }

    /**
     * Click confirm button and dismiss confirm dialog
     */
    @Step("Click confirm button and dismiss confirm dialog")
    public String triggerAndDismissConfirm() {
        click(confirmBtn);
        String confirmText = handleAlert(false);
        log.info("Triggered and dismissed confirm dialog with text: {}", confirmText);
        return confirmText;
    }

    /**
     * Get confirm result text
     */
    @Step("Get confirm result text")
    public String getConfirmResult() {
        return getText(confirmResult);
    }

    /**
     * Click prompt button, enter text and accept prompt
     */
    @Step("Click prompt button, enter text: {text} and accept prompt")
    public String triggerPromptAndEnterText(String text) {
        click(promptBtn);
        String promptText = handlePrompt(text, true);
        log.info("Triggered prompt, entered text: {} and accepted with prompt text: {}", text, promptText);
        return promptText;
    }

    /**
     * Click prompt button and dismiss prompt
     */
    @Step("Click prompt button and dismiss prompt")
    public String triggerAndDismissPrompt() {
        click(promptBtn);
        String promptText = handlePrompt(null, false);
        log.info("Triggered and dismissed prompt with text: {}", promptText);
        return promptText;
    }

    /**
     * Get prompt result text
     */
    @Step("Get prompt result text")
    public String getPromptResult() {
        return getText(promptResult);
    }

    /**
     * Click timed alert button and handle alert after delay
     */
    @Step("Click timed alert button and handle alert after delay")
    public String triggerAndAcceptTimedAlert() {
        click(timedAlertBtn);
        
        try {
            // Wait for alert to appear
            wait.until(driver -> {
                try {
                    driver.switchTo().alert();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });
            
            String alertText = handleAlert(true);
            log.info("Triggered and accepted timed alert with text: {}", alertText);
            return alertText;
        } catch (Exception e) {
            log.error("Error handling timed alert", e);
            throw e;
        }
    }

    /**
     * Show success notification
     */
    @Step("Show success notification")
    public AlertsPage showSuccessNotification() {
        click(successNotificationBtn);
        waitForElementToBeVisible(toast);
        log.info("Showed success notification");
        return this;
    }

    /**
     * Show error notification
     */
    @Step("Show error notification")
    public AlertsPage showErrorNotification() {
        click(errorNotificationBtn);
        waitForElementToBeVisible(toast);
        log.info("Showed error notification");
        return this;
    }

    /**
     * Show warning notification
     */
    @Step("Show warning notification")
    public AlertsPage showWarningNotification() {
        click(warningNotificationBtn);
        waitForElementToBeVisible(toast);
        log.info("Showed warning notification");
        return this;
    }

    /**
     * Show info notification
     */
    @Step("Show info notification")
    public AlertsPage showInfoNotification() {
        click(infoNotificationBtn);
        waitForElementToBeVisible(toast);
        log.info("Showed info notification");
        return this;
    }

    /**
     * Get notification text
     */
    @Step("Get notification text")
    public String getNotificationText() {
        String text = getText(toast);
        log.info("Got notification text: {}", text);
        return text;
    }

    /**
     * Close notification
     */
    @Step("Close notification")
    public AlertsPage closeNotification() {
        click(toastCloseBtn);
        waitForElementToDisappear(By.cssSelector(".toast"), 5);
        log.info("Closed notification");
        return this;
    }

    /**
     * Wait for notification to disappear automatically
     */
    @Step("Wait for notification to disappear automatically")
    public boolean waitForNotificationToDisappear() {
        return waitForElementToDisappear(By.cssSelector(".toast"), 10);
    }
}