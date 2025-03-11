package com.peliQAn.framework.pages.basic;

import com.peliQAn.framework.pages.BasePage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * Page object for Drag & Drop page (/test-ui/drag-drop)
 */
@Slf4j
public class DragDropPage extends BasePage {

    private static final String PAGE_URL = "/test-ui/drag-drop";

    // Sortable items
    @FindBy(css = "#sortable-list .sortable-item")
    private List<WebElement> sortableItems;

    // Treasure and chest
    @FindBy(id = "treasure-item")
    private WebElement treasureItem;

    @FindBy(id = "treasure-chest")
    private WebElement treasureChest;

    @FindBy(id = "treasure-result")
    private WebElement treasureResult;

    // Resizable elements
    @FindBy(id = "resizable-element")
    private WebElement resizableElement;

    @FindBy(css = "#resizable-element .resize-handle")
    private WebElement resizeHandle;

    // Trash items and bin
    @FindBy(css = ".trash-item")
    private List<WebElement> trashItems;

    @FindBy(id = "trash-bin")
    private WebElement trashBin;

    @FindBy(id = "trash-count")
    private WebElement trashCount;

    /**
     * Navigate to Drag & Drop page
     */
    @Step("Navigate to Drag & Drop page")
    public DragDropPage navigateToPage() {
        navigateTo(baseUrl + PAGE_URL);
        waitForPageToLoad();
        log.info("Navigated to Drag & Drop page");
        return this;
    }

    /**
     * Get sortable item by index
     */
    private WebElement getSortableItem(int index) {
        if (index < 0 || index >= sortableItems.size()) {
            throw new IllegalArgumentException("Invalid sortable item index: " + index);
        }
        return sortableItems.get(index);
    }

    /**
     * Drag sortable item from one position to another
     */
    @Step("Drag sortable item from position {fromIndex} to position {toIndex}")
    public DragDropPage dragSortableItem(int fromIndex, int toIndex) {
        WebElement fromElement = getSortableItem(fromIndex);
        WebElement toElement = getSortableItem(toIndex);
        
        dragAndDrop(fromElement, toElement);
        log.info("Dragged sortable item from position {} to position {}", fromIndex, toIndex);
        return this;
    }

    /**
     * Get text of sortable item at index
     */
    @Step("Get text of sortable item at index {index}")
    public String getSortableItemText(int index) {
        String text = getText(getSortableItem(index));
        log.info("Got text of sortable item at index {}: {}", index, text);
        return text;
    }

    /**
     * Drag treasure to chest
     */
    @Step("Drag treasure to chest")
    public DragDropPage dragTreasureToChest() {
        dragAndDrop(treasureItem, treasureChest);
        log.info("Dragged treasure to chest");
        return this;
    }

    /**
     * Get treasure result message
     */
    @Step("Get treasure result message")
    public String getTreasureResultMessage() {
        String message = getText(treasureResult);
        log.info("Got treasure result message: {}", message);
        return message;
    }

    /**
     * Resize element by offset
     */
    @Step("Resize element by offset ({xOffset}, {yOffset})")
    public DragDropPage resizeElement(int xOffset, int yOffset) {
        dragAndDropByOffset(resizeHandle, xOffset, yOffset);
        log.info("Resized element by offset ({}, {})", xOffset, yOffset);
        return this;
    }

    /**
     * Get width of resizable element
     */
    @Step("Get width of resizable element")
    public int getResizableElementWidth() {
        int width = resizableElement.getSize().getWidth();
        log.info("Got width of resizable element: {}", width);
        return width;
    }

    /**
     * Get height of resizable element
     */
    @Step("Get height of resizable element")
    public int getResizableElementHeight() {
        int height = resizableElement.getSize().getHeight();
        log.info("Got height of resizable element: {}", height);
        return height;
    }

    /**
     * Get trash item by index
     */
    private WebElement getTrashItem(int index) {
        if (index < 0 || index >= trashItems.size()) {
            throw new IllegalArgumentException("Invalid trash item index: " + index);
        }
        return trashItems.get(index);
    }

    /**
     * Drag trash item to trash bin
     */
    @Step("Drag trash item at index {index} to trash bin")
    public DragDropPage dragTrashItemToBin(int index) {
        WebElement trashItem = getTrashItem(index);
        dragAndDrop(trashItem, trashBin);
        log.info("Dragged trash item at index {} to trash bin", index);
        return this;
    }

    /**
     * Get trash count
     */
    @Step("Get trash count")
    public int getTrashCount() {
        String countText = getText(trashCount);
        int count = Integer.parseInt(countText);
        log.info("Got trash count: {}", count);
        return count;
    }

    /**
     * Get number of visible trash items
     */
    @Step("Get number of visible trash items")
    public int getVisibleTrashItemsCount() {
        int count = 0;
        for (WebElement item : trashItems) {
            if (isElementDisplayed(item)) {
                count++;
            }
        }
        log.info("Got visible trash items count: {}", count);
        return count;
    }
}