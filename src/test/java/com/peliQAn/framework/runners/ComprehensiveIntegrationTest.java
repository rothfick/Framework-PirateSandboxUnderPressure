package com.peliQAn.framework.runners;

import com.peliQAn.framework.api.AuthApiClient;
import com.peliQAn.framework.api.TestCasesApiClient;
import com.peliQAn.framework.api.TreasureApiClient;
import com.peliQAn.framework.core.DriverFactory;
import com.peliQAn.framework.pages.basic.*;
import com.peliQAn.framework.pages.hardcore.*;
import io.qameta.allure.*;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * Comprehensive integration test for the entire application
 * This test covers multiple scenarios across different parts of the application
 */
@Epic("Integration Tests")
@Feature("End-to-End Application Testing")
@Slf4j
public class ComprehensiveIntegrationTest {

    private WebDriver driver;
    private final AuthApiClient authApiClient = new AuthApiClient();
    private final TreasureApiClient treasureApiClient = new TreasureApiClient();
    private final TestCasesApiClient testCasesApiClient = new TestCasesApiClient();
    private String authToken;
    private Map<String, Object> createdTreasure;
    private Map<String, Object> createdTestCase;

    @BeforeMethod
    public void setUp() {
        driver = DriverFactory.initDriver();
        
        // Register and authenticate a user for API tests
        String username = "testuser_" + System.currentTimeMillis();
        String email = "testuser_" + System.currentTimeMillis() + "@example.com";
        String password = "Password123";
        
        authToken = authApiClient.registerAndLogin(username, email, password);
        treasureApiClient.setAuthToken(authToken);
        testCasesApiClient.setAuthToken(authToken);
        
        assertNotNull(authToken, "Authentication token should not be null");
    }

    @AfterMethod
    public void tearDown() {
        DriverFactory.quitDriver();
        
        // Clean up API resources
        if (createdTreasure != null) {
            try {
                Long treasureId = Long.valueOf(createdTreasure.get("id").toString());
                treasureApiClient.deleteTreasure(treasureId);
            } catch (Exception e) {
                log.warn("Error cleaning up treasure: {}", e.getMessage());
            }
        }
        
        if (createdTestCase != null) {
            try {
                Long testCaseId = Long.valueOf(createdTestCase.get("id").toString());
                testCasesApiClient.deleteTestCase(testCaseId);
            } catch (Exception e) {
                log.warn("Error cleaning up test case: {}", e.getMessage());
            }
        }
    }

    /**
     * This test performs a comprehensive integration test of the application:
     * 1. Create treasure via API
     * 2. Verify treasure via UI
     * 3. Create test case via API
     * 4. Register test run for test case via API
     * 5. Perform UI interactions with forms and tables
     * 6. Complete a hardcore challenge
     * 7. Update treasure status via API
     * 8. Generate test report via API
     */
    @Test(description = "Comprehensive end-to-end integration test")
    @Severity(SeverityLevel.BLOCKER)
    @Story("End-to-End Application Flow")
    @Description("Test that verifies the full flow of the application across different components")
    public void testEndToEndIntegration() {
        // Step 1: Create treasure via API
        createTreasureViaAPI();
        
        // Step 2: Create test case via API
        createTestCaseViaAPI();
        
        // Step 3: Perform UI interactions - Forms
        performUIInteractionWithForms();
        
        // Step 4: Perform UI interactions - Tables
        performUIInteractionWithTables();
        
        // Step 5: Complete a hardcore challenge
        completeShadowDOMChallenge();
        
        // Step 6: Register test run for test case via API
        registerTestRunForTestCase();
        
        // Step 7: Update treasure status via API
        updateTreasureStatus();
        
        // Step 8: Generate test report via API
        generateTestReport();
    }
    
    /**
     * Create treasure via API
     */
    @Step("Create treasure via API")
    private void createTreasureViaAPI() {
        // Create treasure data
        Map<String, Object> treasureData = new HashMap<>();
        treasureData.put("name", "Integration Test Treasure");
        treasureData.put("value", 1000);
        treasureData.put("description", "A treasure for integration testing");
        treasureData.put("location", "Test Island");
        treasureData.put("discovered", false);
        
        // Create treasure
        Response response = treasureApiClient.createTreasure(treasureData);
        assertEquals(response.getStatusCode(), 201, "Create treasure should return 201");
        
        // Save created treasure for later use
        createdTreasure = response.as(Map.class);
        assertNotNull(createdTreasure.get("id"), "Created treasure should have ID");
        log.info("Created treasure with ID: {}", createdTreasure.get("id"));
        
        // Verify treasure details
        assertEquals(createdTreasure.get("name"), "Integration Test Treasure", "Created treasure should have correct name");
        assertEquals(createdTreasure.get("description"), "A treasure for integration testing", 
                    "Created treasure should have correct description");
    }
    
    /**
     * Create test case via API
     */
    @Step("Create test case via API")
    private void createTestCaseViaAPI() {
        // Create test case data
        Map<String, Object> testCaseData = new HashMap<>();
        testCaseData.put("name", "Integration Test Case");
        testCaseData.put("description", "A test case for integration testing");
        testCaseData.put("status", "ACTIVE");
        testCaseData.put("priority", "HIGH");
        
        // Create test case
        Response response = testCasesApiClient.createTestCase(testCaseData);
        assertEquals(response.getStatusCode(), 201, "Create test case should return 201");
        
        // Save created test case for later use
        createdTestCase = response.as(Map.class);
        assertNotNull(createdTestCase.get("id"), "Created test case should have ID");
        log.info("Created test case with ID: {}", createdTestCase.get("id"));
        
        // Verify test case details
        assertEquals(createdTestCase.get("name"), "Integration Test Case", "Created test case should have correct name");
        assertEquals(createdTestCase.get("status"), "ACTIVE", "Created test case should have correct status");
    }
    
    /**
     * Perform UI interaction with forms
     */
    @Step("Perform UI interaction with forms")
    private void performUIInteractionWithForms() {
        FormsPage formsPage = new FormsPage();
        
        // Navigate to forms page
        formsPage.navigateToPage();
        
        // Fill and submit form
        formsPage.fillSimpleForm("John Doe", "john.doe@example.com", "Password123", "Integration test comment")
                .submitSimpleForm();
        
        // Verify form submission result
        String formResult = formsPage.getFormResultMessage();
        assertTrue(formResult.contains("success") || formResult.contains("submitted"), 
                  "Form result should indicate success");
        log.info("Form submitted successfully with result: {}", formResult);
    }
    
    /**
     * Perform UI interaction with tables
     */
    @Step("Perform UI interaction with tables")
    private void performUIInteractionWithTables() {
        TablesPage tablesPage = new TablesPage();
        
        // Navigate to tables page
        tablesPage.navigateToPage();
        
        // Get table data
        List<Map<String, String>> tableData = tablesPage.getBasicTableData();
        assertFalse(tableData.isEmpty(), "Table should not be empty");
        log.info("Retrieved {} rows from table", tableData.size());
        
        // Sort table
        tablesPage.sortTableByColumn("Name");
        
        // Verify sort direction
        String sortDirection = tablesPage.getSortDirectionForColumn("Name");
        assertEquals(sortDirection, "asc", "Table should be sorted in ascending order");
        
        // Sort again to change direction
        tablesPage.sortTableByColumn("Name");
        
        // Verify new sort direction
        sortDirection = tablesPage.getSortDirectionForColumn("Name");
        assertEquals(sortDirection, "desc", "Table should be sorted in descending order");
    }
    
    /**
     * Complete Shadow DOM Challenge
     */
    @Step("Complete Shadow DOM Challenge")
    private void completeShadowDOMChallenge() {
        ShadowDomChallengePage shadowDomPage = new ShadowDomChallengePage();
        
        // Run the complete challenge
        String challengeCode = shadowDomPage.runCompleteChallenge(
                "Integration Test", "integration@test.com", "Password123");
        
        // Verify challenge completion
        assertFalse(challengeCode.isEmpty(), "Challenge code should not be empty");
        assertTrue(shadowDomPage.isValidationSuccessful(), "Challenge should be successfully validated");
        log.info("Completed Shadow DOM challenge with code: {}", challengeCode);
    }
    
    /**
     * Register test run for test case
     */
    @Step("Register test run for test case")
    private void registerTestRunForTestCase() {
        // Get test case ID
        Long testCaseId = Long.valueOf(createdTestCase.get("id").toString());
        
        // Create test run data
        Map<String, Object> testRunData = new HashMap<>();
        testRunData.put("status", "PASSED");
        testRunData.put("duration", 1500);
        testRunData.put("timestamp", System.currentTimeMillis());
        
        // Register test run
        Response response = testCasesApiClient.registerTestRun(testCaseId, testRunData);
        assertEquals(response.getStatusCode(), 201, "Register test run should return 201");
        
        // Verify test run details
        Map<String, Object> testRun = response.as(Map.class);
        assertNotNull(testRun.get("id"), "Test run should have ID");
        assertEquals(testRun.get("status"), "PASSED", "Test run should have correct status");
        log.info("Registered test run with ID: {}", testRun.get("id"));
    }
    
    /**
     * Update treasure status
     */
    @Step("Update treasure status")
    private void updateTreasureStatus() {
        // Get treasure ID
        Long treasureId = Long.valueOf(createdTreasure.get("id").toString());
        
        // Update treasure data (mark as discovered)
        createdTreasure.put("discovered", true);
        
        // Update treasure
        Response response = treasureApiClient.updateTreasure(treasureId, createdTreasure);
        assertEquals(response.getStatusCode(), 200, "Update treasure should return 200");
        
        // Verify updated treasure
        Map<String, Object> updatedTreasure = response.as(Map.class);
        assertTrue((Boolean) updatedTreasure.get("discovered"), "Updated treasure should be marked as discovered");
        log.info("Updated treasure with ID: {}, now discovered: {}", treasureId, updatedTreasure.get("discovered"));
    }
    
    /**
     * Generate test report
     */
    @Step("Generate test report")
    private void generateTestReport() {
        // Create report data
        Map<String, Object> reportData = new HashMap<>();
        reportData.put("name", "Integration Test Report");
        reportData.put("description", "A report for integration testing");
        reportData.put("testCaseIds", List.of(createdTestCase.get("id")));
        
        // Generate report
        Response response = testCasesApiClient.generateTestReport(reportData);
        assertEquals(response.getStatusCode(), 201, "Generate report should return 201");
        
        // Verify report details
        Map<String, Object> report = response.as(Map.class);
        assertNotNull(report.get("id"), "Report should have ID");
        assertEquals(report.get("name"), "Integration Test Report", "Report should have correct name");
        log.info("Generated test report with ID: {}", report.get("id"));
    }
}