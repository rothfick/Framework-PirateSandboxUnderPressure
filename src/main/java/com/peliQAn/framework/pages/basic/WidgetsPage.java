package com.peliQAn.framework.pages.basic;

import com.peliQAn.framework.pages.BasePage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Page object for Widgets page (/test-ui/widgets)
 */
@Slf4j
public class WidgetsPage extends BasePage {

    private static final String PAGE_URL = "/test-ui/widgets";

    // Datepicker
    @FindBy(id = "datepicker")
    private WebElement datepicker;

    @FindBy(id = "datepicker-result")
    private WebElement datepickerResult;

    // Slider
    @FindBy(id = "slider")
    private WebElement slider;

    @FindBy(id = "slider-value")
    private WebElement sliderValue;

    @FindBy(id = "range-slider")
    private WebElement rangeSlider;

    @FindBy(id = "range-slider-min")
    private WebElement rangeSliderMin;

    @FindBy(id = "range-slider-max")
    private WebElement rangeSliderMax;

    // Autocomplete
    @FindBy(id = "autocomplete-input")
    private WebElement autocompleteInput;

    @FindBy(css = ".autocomplete-suggestions")
    private WebElement autocompleteSuggestions;

    @FindBy(css = ".autocomplete-suggestion")
    private List<WebElement> autocompleteSuggestionItems;

    @FindBy(id = "autocomplete-result")
    private WebElement autocompleteResult;

    // Navigation tree
    @FindBy(id = "tree-container")
    private WebElement treeContainer;

    @FindBy(css = ".tree-node.parent > .node-content")
    private List<WebElement> treeParentNodes;

    @FindBy(css = ".tree-node.leaf > .node-content")
    private List<WebElement> treeLeafNodes;

    @FindBy(id = "tree-result")
    private WebElement treeResult;

    // Carousel
    @FindBy(id = "carousel")
    private WebElement carousel;

    @FindBy(id = "carousel-prev")
    private WebElement carouselPrev;

    @FindBy(id = "carousel-next")
    private WebElement carouselNext;

    @FindBy(css = ".carousel-slide.active")
    private WebElement activeCarouselSlide;

    /**
     * Navigate to Widgets page
     */
    @Step("Navigate to Widgets page")
    public WidgetsPage navigateToPage() {
        navigateTo(baseUrl + PAGE_URL);
        waitForPageToLoad();
        log.info("Navigated to Widgets page");
        return this;
    }

    /**
     * Select date from datepicker
     */
    @Step("Select date: {date} from datepicker")
    public WidgetsPage selectDate(LocalDate date) {
        click(datepicker);
        
        // Format date components
        int year = date.getYear();
        String month = date.getMonth().toString();
        int day = date.getDayOfMonth();
        
        // Navigate to the year and month
        WebElement yearMonthDisplay = driver.findElement(By.cssSelector(".ui-datepicker-title"));
        
        // Click on month/year to get to the month/year selection view
        click(yearMonthDisplay);
        
        // Select year
        WebElement yearElement = driver.findElement(By.cssSelector(".ui-datepicker-year option[value='" + year + "']"));
        click(yearElement);
        
        // Select month
        WebElement monthElement = driver.findElement(By.cssSelector(".ui-datepicker-month option[value='" + (date.getMonthValue() - 1) + "']"));
        click(monthElement);
        
        // Select day
        WebElement dayElement = driver.findElement(By.xpath("//td[@data-date='" + day + "']"));
        click(dayElement);
        
        log.info("Selected date: {} from datepicker", date);
        return this;
    }

    /**
     * Get selected date from datepicker result
     */
    @Step("Get selected date from datepicker result")
    public String getSelectedDate() {
        String date = getText(datepickerResult);
        log.info("Selected date: {}", date);
        return date;
    }

    /**
     * Set slider value
     */
    @Step("Set slider value: {value}")
    public WidgetsPage setSliderValue(int value) {
        // Calculate the move offset based on value (assuming 0-100 range)
        int min = 0;
        int max = 100;
        int width = slider.getSize().getWidth();
        
        // Calculate the position to click on
        int offset = (int) ((double) (value - min) / (max - min) * width);
        
        // Use Actions to move slider to specific position
        actions.moveToElement(slider, 0, 0)  // Move to the leftmost point
               .moveByOffset(offset, 0)      // Move to the desired point
               .click()
               .perform();
        
        log.info("Set slider value to: {}", value);
        return this;
    }

    /**
     * Get slider value
     */
    @Step("Get slider value")
    public int getSliderValue() {
        String valueText = getText(sliderValue);
        int value = Integer.parseInt(valueText);
        log.info("Slider value: {}", value);
        return value;
    }

    /**
     * Set range slider values
     */
    @Step("Set range slider values: min={minValue}, max={maxValue}")
    public WidgetsPage setRangeSliderValues(int minValue, int maxValue) {
        // Get the range slider handles
        WebElement minHandle = driver.findElement(By.cssSelector(".range-slider .ui-slider-handle:nth-child(2)"));
        WebElement maxHandle = driver.findElement(By.cssSelector(".range-slider .ui-slider-handle:nth-child(3)"));
        
        // Calculate positions
        int min = 0;
        int max = 100;
        int width = rangeSlider.getSize().getWidth();
        
        // Calculate offsets
        int minOffset = (int) ((double) (minValue - min) / (max - min) * width);
        int maxOffset = (int) ((double) (maxValue - min) / (max - min) * width);
        
        // Move min handle
        actions.clickAndHold(minHandle)
               .moveByOffset(minOffset - minHandle.getLocation().getX(), 0)
               .release()
               .perform();
        
        // Move max handle
        actions.clickAndHold(maxHandle)
               .moveByOffset(maxOffset - maxHandle.getLocation().getX(), 0)
               .release()
               .perform();
        
        log.info("Set range slider values to: min={}, max={}", minValue, maxValue);
        return this;
    }

    /**
     * Get range slider min value
     */
    @Step("Get range slider min value")
    public int getRangeSliderMinValue() {
        String valueText = getText(rangeSliderMin);
        int value = Integer.parseInt(valueText);
        log.info("Range slider min value: {}", value);
        return value;
    }

    /**
     * Get range slider max value
     */
    @Step("Get range slider max value")
    public int getRangeSliderMaxValue() {
        String valueText = getText(rangeSliderMax);
        int value = Integer.parseInt(valueText);
        log.info("Range slider max value: {}", value);
        return value;
    }

    /**
     * Enter text in autocomplete input
     */
    @Step("Enter text in autocomplete input: {text}")
    public WidgetsPage enterAutocompleteText(String text) {
        type(autocompleteInput, text);
        
        // Wait for suggestions to appear
        waitForElementToBeVisible(autocompleteSuggestions);
        
        log.info("Entered text in autocomplete input: {}", text);
        return this;
    }

    /**
     * Get autocomplete suggestions
     */
    @Step("Get autocomplete suggestions")
    public List<String> getAutocompleteSuggestions() {
        waitForElementToBeVisible(autocompleteSuggestions);
        
        List<String> suggestions = autocompleteSuggestionItems.stream()
                .map(this::getText)
                .collect(Collectors.toList());
        
        log.info("Got {} autocomplete suggestions", suggestions.size());
        return suggestions;
    }

    /**
     * Select autocomplete suggestion by index
     */
    @Step("Select autocomplete suggestion by index: {index}")
    public WidgetsPage selectAutocompleteSuggestion(int index) {
        waitForElementToBeVisible(autocompleteSuggestions);
        
        if (index < 0 || index >= autocompleteSuggestionItems.size()) {
            throw new IllegalArgumentException("Invalid suggestion index: " + index);
        }
        
        WebElement suggestion = autocompleteSuggestionItems.get(index);
        click(suggestion);
        
        log.info("Selected autocomplete suggestion by index: {}", index);
        return this;
    }

    /**
     * Get autocomplete result
     */
    @Step("Get autocomplete result")
    public String getAutocompleteResult() {
        String result = getText(autocompleteResult);
        log.info("Autocomplete result: {}", result);
        return result;
    }

    /**
     * Expand tree node
     */
    @Step("Expand tree node: {nodeName}")
    public WidgetsPage expandTreeNode(String nodeName) {
        WebElement node = treeParentNodes.stream()
                .filter(n -> getText(n).contains(nodeName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tree node not found: " + nodeName));
        
        // Check if already expanded
        WebElement parentElement = (WebElement) js.executeScript(
                "return arguments[0].closest('.tree-node')", node);
        
        if (!parentElement.getAttribute("class").contains("expanded")) {
            click(node);
        }
        
        log.info("Expanded tree node: {}", nodeName);
        return this;
    }

    /**
     * Collapse tree node
     */
    @Step("Collapse tree node: {nodeName}")
    public WidgetsPage collapseTreeNode(String nodeName) {
        WebElement node = treeParentNodes.stream()
                .filter(n -> getText(n).contains(nodeName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tree node not found: " + nodeName));
        
        // Check if not already collapsed
        WebElement parentElement = (WebElement) js.executeScript(
                "return arguments[0].closest('.tree-node')", node);
        
        if (parentElement.getAttribute("class").contains("expanded")) {
            click(node);
        }
        
        log.info("Collapsed tree node: {}", nodeName);
        return this;
    }

    /**
     * Select tree leaf node
     */
    @Step("Select tree leaf node: {nodeName}")
    public WidgetsPage selectTreeLeafNode(String nodeName) {
        WebElement node = treeLeafNodes.stream()
                .filter(n -> getText(n).contains(nodeName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tree leaf node not found: " + nodeName));
        
        click(node);
        
        log.info("Selected tree leaf node: {}", nodeName);
        return this;
    }

    /**
     * Get tree result
     */
    @Step("Get tree result")
    public String getTreeResult() {
        String result = getText(treeResult);
        log.info("Tree result: {}", result);
        return result;
    }

    /**
     * Navigate carousel to next slide
     */
    @Step("Navigate carousel to next slide")
    public WidgetsPage navigateCarouselToNextSlide() {
        click(carouselNext);
        
        // Wait for animation to complete
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("Navigated carousel to next slide");
        return this;
    }

    /**
     * Navigate carousel to previous slide
     */
    @Step("Navigate carousel to previous slide")
    public WidgetsPage navigateCarouselToPreviousSlide() {
        click(carouselPrev);
        
        // Wait for animation to complete
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("Navigated carousel to previous slide");
        return this;
    }

    /**
     * Get active carousel slide content
     */
    @Step("Get active carousel slide content")
    public String getActiveCarouselSlideContent() {
        String content = getText(activeCarouselSlide);
        log.info("Active carousel slide content: {}", content);
        return content;
    }

    /**
     * Get active carousel slide index
     */
    @Step("Get active carousel slide index")
    public int getActiveCarouselSlideIndex() {
        String index = activeCarouselSlide.getAttribute("data-slide-index");
        int slideIndex = Integer.parseInt(index);
        log.info("Active carousel slide index: {}", slideIndex);
        return slideIndex;
    }
}