package com.peliQAn.framework.pages.basic;

import com.peliQAn.framework.pages.BasePage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page object for All Elements page (/test-ui/all)
 * Contains all the basic UI elements in one page
 */
@Slf4j
public class AllElementsPage extends BasePage {

    private static final String PAGE_URL = "/test-ui/all";

    // Text elements
    @FindBy(id = "paragraph-1")
    private WebElement paragraph1;

    @FindBy(id = "highlighted-text")
    private WebElement highlightedText;

    @FindBy(id = "toggle-text-btn")
    private WebElement toggleTextBtn;

    @FindBy(id = "hidden-text")
    private WebElement hiddenText;

    // Form elements
    @FindBy(id = "text-input")
    private WebElement textInput;

    @FindBy(id = "password-input")
    private WebElement passwordInput;

    @FindBy(id = "email-input")
    private WebElement emailInput;

    @FindBy(id = "textarea-input")
    private WebElement textareaInput;

    @FindBy(id = "checkbox-1")
    private WebElement checkbox1;

    @FindBy(id = "radio-1")
    private WebElement radio1;

    @FindBy(id = "radio-2")
    private WebElement radio2;

    @FindBy(id = "dropdown")
    private WebElement dropdown;

    @FindBy(id = "submit-btn")
    private WebElement submitBtn;

    // Dynamic elements
    @FindBy(id = "loading-element")
    private WebElement loadingElement;

    @FindBy(id = "load-data-btn")
    private WebElement loadDataBtn;

    @FindBy(id = "data-container")
    private WebElement dataContainer;

    /**
     * Navigate to All Elements page
     */
    @Step("Navigate to All Elements page")
    public AllElementsPage navigateToPage() {
        navigateTo(baseUrl + PAGE_URL);
        waitForPageToLoad();
        log.info("Navigated to All Elements page");
        return this;
    }

    /**
     * Get paragraph text
     */
    @Step("Get paragraph text")
    public String getParagraphText() {
        return getText(paragraph1);
    }

    /**
     * Get highlighted text
     */
    @Step("Get highlighted text")
    public String getHighlightedText() {
        return getText(highlightedText);
    }

    /**
     * Toggle text visibility
     */
    @Step("Toggle text visibility")
    public AllElementsPage toggleTextVisibility() {
        click(toggleTextBtn);
        return this;
    }

    /**
     * Check if hidden text is displayed
     */
    @Step("Check if hidden text is displayed")
    public boolean isHiddenTextDisplayed() {
        return isElementDisplayed(hiddenText);
    }

    /**
     * Enter text in text input field
     */
    @Step("Enter text in text input field: {text}")
    public AllElementsPage enterText(String text) {
        type(textInput, text);
        return this;
    }

    /**
     * Enter password in password input field
     */
    @Step("Enter password in password input field: {password}")
    public AllElementsPage enterPassword(String password) {
        type(passwordInput, password);
        return this;
    }

    /**
     * Enter email in email input field
     */
    @Step("Enter email in email input field: {email}")
    public AllElementsPage enterEmail(String email) {
        type(emailInput, email);
        return this;
    }

    /**
     * Enter text in textarea input field
     */
    @Step("Enter text in textarea input field: {text}")
    public AllElementsPage enterTextarea(String text) {
        type(textareaInput, text);
        return this;
    }

    /**
     * Check/uncheck checkbox
     */
    @Step("Set checkbox checked: {checked}")
    public AllElementsPage setCheckbox(boolean checked) {
        if (checkbox1.isSelected() != checked) {
            click(checkbox1);
        }
        return this;
    }

    /**
     * Select radio button option
     */
    @Step("Select radio button option: {option}")
    public AllElementsPage selectRadioOption(int option) {
        if (option == 1) {
            click(radio1);
        } else if (option == 2) {
            click(radio2);
        } else {
            log.warn("Invalid radio option: {}", option);
        }
        return this;
    }

    /**
     * Select option from dropdown
     */
    @Step("Select option from dropdown: {optionText}")
    public AllElementsPage selectDropdownOption(String optionText) {
        selectByVisibleText(dropdown, optionText);
        return this;
    }

    /**
     * Submit form
     */
    @Step("Submit form")
    public AllElementsPage submitForm() {
        click(submitBtn);
        return this;
    }

    /**
     * Load data asynchronously
     */
    @Step("Load data asynchronously")
    public AllElementsPage loadData() {
        click(loadDataBtn);
        waitForElementToBeVisible(By.cssSelector("#data-container .data-item"), 10);
        return this;
    }

    /**
     * Get loaded data count
     */
    @Step("Get loaded data count")
    public int getLoadedDataCount() {
        return findElements(By.cssSelector("#data-container .data-item")).size();
    }

    /**
     * Fill form with test data
     */
    @Step("Fill form with test data")
    public AllElementsPage fillFormWithTestData(String name, String password, String email, String comments) {
        enterText(name);
        enterPassword(password);
        enterEmail(email);
        enterTextarea(comments);
        setCheckbox(true);
        selectRadioOption(1);
        selectDropdownOption("Option 2");
        return this;
    }
}