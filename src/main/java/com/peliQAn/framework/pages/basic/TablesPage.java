package com.peliQAn.framework.pages.basic;

import com.peliQAn.framework.pages.BasePage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Page object for Tables page (/test-ui/tables)
 */
@Slf4j
public class TablesPage extends BasePage {

    private static final String PAGE_URL = "/test-ui/tables";

    // Basic table
    @FindBy(id = "basic-table")
    private WebElement basicTable;

    // Sortable table
    @FindBy(id = "sortable-table")
    private WebElement sortableTable;

    @FindBy(css = "#sortable-table th.sortable")
    private List<WebElement> sortableColumns;

    // Table with pagination
    @FindBy(id = "paginated-table")
    private WebElement paginatedTable;

    @FindBy(css = ".pagination-container .page-link")
    private List<WebElement> paginationLinks;

    @FindBy(id = "page-size-select")
    private WebElement pageSizeSelect;

    @FindBy(css = ".pagination-info")
    private WebElement paginationInfo;

    // Filterable table
    @FindBy(id = "filterable-table")
    private WebElement filterableTable;

    @FindBy(id = "table-search")
    private WebElement tableSearchInput;

    @FindBy(id = "clear-search")
    private WebElement clearSearchButton;

    // Editable table
    @FindBy(id = "editable-table")
    private WebElement editableTable;

    @FindBy(css = "#editable-table td.editable")
    private List<WebElement> editableCells;

    @FindBy(id = "save-edits")
    private WebElement saveEditsButton;

    @FindBy(id = "edit-result")
    private WebElement editResult;

    /**
     * Navigate to Tables page
     */
    @Step("Navigate to Tables page")
    public TablesPage navigateToPage() {
        navigateTo(baseUrl + PAGE_URL);
        waitForPageToLoad();
        log.info("Navigated to Tables page");
        return this;
    }

    /**
     * Get all rows from basic table
     */
    @Step("Get all rows from basic table")
    public List<Map<String, String>> getBasicTableData() {
        List<WebElement> headerCells = basicTable.findElements(By.cssSelector("thead th"));
        List<String> headers = headerCells.stream().map(WebElement::getText).collect(Collectors.toList());
        
        List<WebElement> rows = basicTable.findElements(By.cssSelector("tbody tr"));
        List<Map<String, String>> tableData = rows.stream().map(row -> {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            Map<String, String> rowData = new HashMap<>();
            
            for (int i = 0; i < headers.size() && i < cells.size(); i++) {
                rowData.put(headers.get(i), cells.get(i).getText());
            }
            
            return rowData;
        }).collect(Collectors.toList());
        
        log.info("Got {} rows from basic table", tableData.size());
        return tableData;
    }

    /**
     * Get cell data from basic table
     */
    @Step("Get cell data from basic table at row {rowIndex}, column {columnIndex}")
    public String getBasicTableCellData(int rowIndex, int columnIndex) {
        WebElement cell = basicTable.findElements(By.cssSelector("tbody tr")).get(rowIndex)
                        .findElements(By.tagName("td")).get(columnIndex);
        String cellData = cell.getText();
        log.info("Got cell data from basic table at row {}, column {}: {}", rowIndex, columnIndex, cellData);
        return cellData;
    }

    /**
     * Sort sortable table by column name
     */
    @Step("Sort sortable table by column: {columnName}")
    public TablesPage sortTableByColumn(String columnName) {
        WebElement columnHeader = sortableColumns.stream()
                .filter(column -> column.getText().equals(columnName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Column not found: " + columnName));
        
        click(columnHeader);
        waitForPageToLoad(); // Wait for sorting to complete
        log.info("Sorted table by column: {}", columnName);
        return this;
    }

    /**
     * Get current sort direction for column
     */
    @Step("Get current sort direction for column: {columnName}")
    public String getSortDirectionForColumn(String columnName) {
        WebElement columnHeader = sortableColumns.stream()
                .filter(column -> column.getText().equals(columnName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Column not found: " + columnName));
        
        String direction = columnHeader.getAttribute("data-sort-direction");
        log.info("Sort direction for column {}: {}", columnName, direction);
        return direction != null ? direction : "none";
    }

    /**
     * Get data from sortable table
     */
    @Step("Get data from sortable table")
    public List<Map<String, String>> getSortableTableData() {
        List<WebElement> headerCells = sortableTable.findElements(By.cssSelector("thead th"));
        List<String> headers = headerCells.stream().map(WebElement::getText).collect(Collectors.toList());
        
        List<WebElement> rows = sortableTable.findElements(By.cssSelector("tbody tr"));
        List<Map<String, String>> tableData = rows.stream().map(row -> {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            Map<String, String> rowData = new HashMap<>();
            
            for (int i = 0; i < headers.size() && i < cells.size(); i++) {
                rowData.put(headers.get(i), cells.get(i).getText());
            }
            
            return rowData;
        }).collect(Collectors.toList());
        
        log.info("Got {} rows from sortable table", tableData.size());
        return tableData;
    }

    /**
     * Navigate to pagination page
     */
    @Step("Navigate to pagination page: {pageNumber}")
    public TablesPage navigateToPage(int pageNumber) {
        WebElement pageLink = paginationLinks.stream()
                .filter(link -> link.getText().equals(String.valueOf(pageNumber)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Page not found: " + pageNumber));
        
        click(pageLink);
        waitForPageToLoad(); // Wait for page change
        log.info("Navigated to pagination page: {}", pageNumber);
        return this;
    }

    /**
     * Navigate to next page
     */
    @Step("Navigate to next page")
    public TablesPage navigateToNextPage() {
        WebElement nextLink = paginationLinks.stream()
                .filter(link -> link.getText().equals("Next") || link.getText().equals("»"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Next page link not found"));
        
        click(nextLink);
        waitForPageToLoad(); // Wait for page change
        log.info("Navigated to next page");
        return this;
    }

    /**
     * Navigate to previous page
     */
    @Step("Navigate to previous page")
    public TablesPage navigateToPreviousPage() {
        WebElement prevLink = paginationLinks.stream()
                .filter(link -> link.getText().equals("Previous") || link.getText().equals("«"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Previous page link not found"));
        
        click(prevLink);
        waitForPageToLoad(); // Wait for page change
        log.info("Navigated to previous page");
        return this;
    }

    /**
     * Change page size
     */
    @Step("Change page size to: {pageSize}")
    public TablesPage changePageSize(int pageSize) {
        selectByValue(pageSizeSelect, String.valueOf(pageSize));
        waitForPageToLoad(); // Wait for table to update
        log.info("Changed page size to: {}", pageSize);
        return this;
    }

    /**
     * Get current page info
     */
    @Step("Get current page info")
    public String getCurrentPageInfo() {
        String info = getText(paginationInfo);
        log.info("Current page info: {}", info);
        return info;
    }

    /**
     * Get data from current page of paginated table
     */
    @Step("Get data from current page of paginated table")
    public List<Map<String, String>> getPaginatedTableData() {
        List<WebElement> headerCells = paginatedTable.findElements(By.cssSelector("thead th"));
        List<String> headers = headerCells.stream().map(WebElement::getText).collect(Collectors.toList());
        
        List<WebElement> rows = paginatedTable.findElements(By.cssSelector("tbody tr"));
        List<Map<String, String>> tableData = rows.stream().map(row -> {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            Map<String, String> rowData = new HashMap<>();
            
            for (int i = 0; i < headers.size() && i < cells.size(); i++) {
                rowData.put(headers.get(i), cells.get(i).getText());
            }
            
            return rowData;
        }).collect(Collectors.toList());
        
        log.info("Got {} rows from current page of paginated table", tableData.size());
        return tableData;
    }

    /**
     * Filter table by search term
     */
    @Step("Filter table by search term: {searchTerm}")
    public TablesPage filterTable(String searchTerm) {
        type(tableSearchInput, searchTerm);
        // Press enter to apply filter
        tableSearchInput.sendKeys(org.openqa.selenium.Keys.ENTER);
        waitForPageToLoad(); // Wait for table to update
        log.info("Filtered table by search term: {}", searchTerm);
        return this;
    }

    /**
     * Clear table filter
     */
    @Step("Clear table filter")
    public TablesPage clearTableFilter() {
        click(clearSearchButton);
        waitForPageToLoad(); // Wait for table to update
        log.info("Cleared table filter");
        return this;
    }

    /**
     * Get data from filtered table
     */
    @Step("Get data from filtered table")
    public List<Map<String, String>> getFilteredTableData() {
        List<WebElement> headerCells = filterableTable.findElements(By.cssSelector("thead th"));
        List<String> headers = headerCells.stream().map(WebElement::getText).collect(Collectors.toList());
        
        List<WebElement> rows = filterableTable.findElements(By.cssSelector("tbody tr"));
        List<Map<String, String>> tableData = rows.stream()
                .filter(row -> row.isDisplayed()) // Only get visible rows
                .map(row -> {
                    List<WebElement> cells = row.findElements(By.tagName("td"));
                    Map<String, String> rowData = new HashMap<>();
                    
                    for (int i = 0; i < headers.size() && i < cells.size(); i++) {
                        rowData.put(headers.get(i), cells.get(i).getText());
                    }
                    
                    return rowData;
                }).collect(Collectors.toList());
        
        log.info("Got {} rows from filtered table", tableData.size());
        return tableData;
    }

    /**
     * Edit cell in editable table
     */
    @Step("Edit cell in editable table at row {rowIndex}, column {columnIndex} with value: {value}")
    public TablesPage editCell(int rowIndex, int columnIndex, String value) {
        List<WebElement> rows = editableTable.findElements(By.cssSelector("tbody tr"));
        if (rowIndex >= rows.size()) {
            throw new IllegalArgumentException("Row index out of bounds: " + rowIndex);
        }
        
        List<WebElement> cells = rows.get(rowIndex).findElements(By.cssSelector("td.editable"));
        if (columnIndex >= cells.size()) {
            throw new IllegalArgumentException("Column index out of bounds: " + columnIndex);
        }
        
        WebElement cell = cells.get(columnIndex);
        
        // Double-click to activate cell for editing
        actions.doubleClick(cell).perform();
        
        // Find the input element that appears for editing
        WebElement input = cell.findElement(By.tagName("input"));
        type(input, value);
        
        // Press enter to confirm edit
        input.sendKeys(org.openqa.selenium.Keys.ENTER);
        
        log.info("Edited cell at row {}, column {} with value: {}", rowIndex, columnIndex, value);
        return this;
    }

    /**
     * Save edits in editable table
     */
    @Step("Save edits in editable table")
    public TablesPage saveEdits() {
        click(saveEditsButton);
        waitForElementToBeVisible(editResult);
        log.info("Saved edits in editable table");
        return this;
    }

    /**
     * Get edit result message
     */
    @Step("Get edit result message")
    public String getEditResultMessage() {
        String message = getText(editResult);
        log.info("Edit result message: {}", message);
        return message;
    }
}