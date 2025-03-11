package com.peliQAn.framework.pages.basic;

import com.peliQAn.framework.pages.BasePage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * Page object for Forms page (/test-ui/forms)
 */
@Slf4j
public class FormsPage extends BasePage {

    private static final String PAGE_URL = "/test-ui/forms";

    // Basic form elements
    @FindBy(id = "simple-form")
    private WebElement simpleForm;

    @FindBy(id = "name")
    private WebElement nameInput;

    @FindBy(id = "email")
    private WebElement emailInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "comments")
    private WebElement commentsTextarea;

    @FindBy(id = "submit-basic")
    private WebElement submitBasicButton;

    @FindBy(id = "form-result")
    private WebElement formResult;

    // Validation form elements
    @FindBy(id = "validation-form")
    private WebElement validationForm;

    @FindBy(id = "username")
    private WebElement usernameInput;

    @FindBy(id = "email-validation")
    private WebElement emailValidationInput;

    @FindBy(id = "password-validation")
    private WebElement passwordValidationInput;

    @FindBy(id = "confirm-password")
    private WebElement confirmPasswordInput;

    @FindBy(id = "submit-validation")
    private WebElement submitValidationButton;

    @FindBy(css = ".error-message")
    private List<WebElement> errorMessages;

    // Dynamic form elements
    @FindBy(id = "dynamic-form")
    private WebElement dynamicForm;

    @FindBy(id = "product-type")
    private WebElement productTypeSelect;

    @FindBy(id = "product-options")
    private WebElement productOptionsContainer;

    @FindBy(css = "#product-options .option-field")
    private List<WebElement> productOptions;

    @FindBy(id = "submit-dynamic")
    private WebElement submitDynamicButton;

    /**
     * Navigate to Forms page
     */
    @Step("Navigate to Forms page")
    public FormsPage navigateToPage() {
        navigateTo(baseUrl + PAGE_URL);
        waitForPageToLoad();
        log.info("Navigated to Forms page");
        return this;
    }

    // Simple form methods

    /**
     * Fill simple form with data
     */
    @Step("Fill simple form with data")
    public FormsPage fillSimpleForm(String name, String email, String password, String comments) {
        type(nameInput, name);
        type(emailInput, email);
        type(passwordInput, password);
        type(commentsTextarea, comments);
        log.info("Filled simple form with data: name={}, email={}", name, email);
        return this;
    }

    /**
     * Submit simple form
     */
    @Step("Submit simple form")
    public FormsPage submitSimpleForm() {
        click(submitBasicButton);
        log.info("Submitted simple form");
        return this;
    }

    /**
     * Get form result message
     */
    @Step("Get form result message")
    public String getFormResultMessage() {
        waitForElementToBeVisible(formResult);
        String message = getText(formResult);
        log.info("Got form result message: {}", message);
        return message;
    }

    // Validation form methods

    /**
     * Fill validation form with data
     */
    @Step("Fill validation form with data")
    public FormsPage fillValidationForm(String username, String email, String password, String confirmPassword) {
        type(usernameInput, username);
        type(emailValidationInput, email);
        type(passwordValidationInput, password);
        type(confirmPasswordInput, confirmPassword);
        log.info("Filled validation form with data: username={}, email={}", username, email);
        return this;
    }

    /**
     * Submit validation form
     */
    @Step("Submit validation form")
    public FormsPage submitValidationForm() {
        click(submitValidationButton);
        log.info("Submitted validation form");
        return this;
    }

    /**
     * Get all error messages from validation form
     */
    @Step("Get all error messages from validation form")
    public List<String> getErrorMessages() {
        List<String> messages = errorMessages.stream()
                .filter(this::isElementDisplayed)
                .map(this::getText)
                .collect(java.util.stream.Collectors.toList());
        
        log.info("Got {} error messages", messages.size());
        return messages;
    }

    /**
     * Check if validation form has error
     */
    @Step("Check if validation form has error for field: {fieldId}")
    public boolean hasErrorFor(String fieldId) {
        WebElement errorElement = driver.findElement(By.cssSelector("#" + fieldId + " + .error-message"));
        boolean hasError = isElementDisplayed(errorElement);
        log.info("Field {} has error: {}", fieldId, hasError);
        return hasError;
    }

    // Dynamic form methods

    /**
     * Select product type
     */
    @Step("Select product type: {productType}")
    public FormsPage selectProductType(String productType) {
        selectByVisibleText(productTypeSelect, productType);
        log.info("Selected product type: {}", productType);
        // Wait for dynamic options to appear
        try {
            Thread.sleep(500); // Short delay for options to appear
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return this;
    }

    /**
     * Get available product options
     */
    @Step("Get available product options")
    public List<WebElement> getProductOptions() {
        return productOptions.stream()
                .filter(this::isElementDisplayed)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Fill product option
     */
    @Step("Fill product option: {optionName} with value: {optionValue}")
    public FormsPage fillProductOption(String optionName, String optionValue) {
        WebElement optionInput = driver.findElement(By.cssSelector(
                "#product-options [data-option='" + optionName + "'] input"));
        type(optionInput, optionValue);
        log.info("Filled product option: {} with value: {}", optionName, optionValue);
        return this;
    }

    /**
     * Submit dynamic form
     */
    @Step("Submit dynamic form")
    public FormsPage submitDynamicForm() {
        click(submitDynamicButton);
        log.info("Submitted dynamic form");
        return this;
    }

    /**
     * Check if form is successfully submitted (no visible errors)
     */
    @Step("Check if form is successfully submitted")
    public boolean isFormSuccessfullySubmitted() {
        boolean noErrors = errorMessages.stream().noneMatch(this::isElementDisplayed);
        boolean hasResult = isElementDisplayed(formResult);
        boolean success = noErrors && hasResult;
        log.info("Form is successfully submitted: {}", success);
        return success;
    }
}