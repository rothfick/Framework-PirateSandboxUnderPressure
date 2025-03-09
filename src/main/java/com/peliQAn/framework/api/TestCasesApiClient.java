package com.peliQAn.framework.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API client for test cases endpoints
 */
@Slf4j
public class TestCasesApiClient extends BaseApiClient {

    private static final String TESTING_ENDPOINT = "/testing";
    private static final String RUNS_ENDPOINT = "/runs";
    private static final String REPORTS_ENDPOINT = "/reports";
    private static final String STATISTICS_ENDPOINT = "/statistics";
    
    private final ObjectMapper objectMapper;
    private String authToken;

    public TestCasesApiClient() {
        super();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Set authentication token for subsequent requests
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
        log.info("Set authentication token for test cases API client");
    }

    /**
     * Get all test cases
     */
    @Step("Get all test cases")
    public Response getAllTestCases() {
        log.info("Getting all test cases");
        
        if (authToken != null) {
            return getAuthSpec(authToken)
                    .when()
                    .get(TESTING_ENDPOINT)
                    .then()
                    .extract()
                    .response();
        } else {
            return get(TESTING_ENDPOINT);
        }
    }

    /**
     * Get test case by ID
     */
    @Step("Get test case by ID: {id}")
    public Response getTestCaseById(long id) {
        log.info("Getting test case by ID: {}", id);
        
        if (authToken != null) {
            return getAuthSpec(authToken)
                    .when()
                    .get(TESTING_ENDPOINT + "/" + id)
                    .then()
                    .extract()
                    .response();
        } else {
            return get(TESTING_ENDPOINT + "/" + id);
        }
    }

    /**
     * Create new test case
     */
    @Step("Create new test case")
    public Response createTestCase(Map<String, Object> testCaseData) {
        log.info("Creating new test case: {}", testCaseData);
        
        if (authToken == null) {
            throw new IllegalStateException("Authentication token is required to create test case");
        }
        
        return getAuthSpec(authToken)
                .body(testCaseData)
                .when()
                .post(TESTING_ENDPOINT)
                .then()
                .extract()
                .response();
    }

    /**
     * Update test case
     */
    @Step("Update test case with ID: {id}")
    public Response updateTestCase(long id, Map<String, Object> testCaseData) {
        log.info("Updating test case with ID {}: {}", id, testCaseData);
        
        if (authToken == null) {
            throw new IllegalStateException("Authentication token is required to update test case");
        }
        
        return getAuthSpec(authToken)
                .body(testCaseData)
                .when()
                .put(TESTING_ENDPOINT + "/" + id)
                .then()
                .extract()
                .response();
    }

    /**
     * Delete test case
     */
    @Step("Delete test case with ID: {id}")
    public Response deleteTestCase(long id) {
        log.info("Deleting test case with ID: {}", id);
        
        if (authToken == null) {
            throw new IllegalStateException("Authentication token is required to delete test case");
        }
        
        return getAuthSpec(authToken)
                .when()
                .delete(TESTING_ENDPOINT + "/" + id)
                .then()
                .extract()
                .response();
    }

    /**
     * Register test run
     */
    @Step("Register test run for test case ID: {testCaseId}")
    public Response registerTestRun(long testCaseId, Map<String, Object> runData) {
        log.info("Registering test run for test case ID: {}", testCaseId);
        
        if (authToken == null) {
            throw new IllegalStateException("Authentication token is required to register test run");
        }
        
        return getAuthSpec(authToken)
                .body(runData)
                .when()
                .post(TESTING_ENDPOINT + "/" + testCaseId + "/run")
                .then()
                .extract()
                .response();
    }

    /**
     * Get test case runs
     */
    @Step("Get runs for test case ID: {testCaseId}")
    public Response getTestCaseRuns(long testCaseId) {
        log.info("Getting runs for test case ID: {}", testCaseId);
        
        if (authToken != null) {
            return getAuthSpec(authToken)
                    .when()
                    .get(TESTING_ENDPOINT + "/" + testCaseId + RUNS_ENDPOINT)
                    .then()
                    .extract()
                    .response();
        } else {
            return get(TESTING_ENDPOINT + "/" + testCaseId + RUNS_ENDPOINT);
        }
    }

    /**
     * Get all test runs
     */
    @Step("Get all test runs")
    public Response getAllTestRuns() {
        log.info("Getting all test runs");
        
        if (authToken != null) {
            return getAuthSpec(authToken)
                    .when()
                    .get(TESTING_ENDPOINT + RUNS_ENDPOINT)
                    .then()
                    .extract()
                    .response();
        } else {
            return get(TESTING_ENDPOINT + RUNS_ENDPOINT);
        }
    }

    /**
     * Generate test report
     */
    @Step("Generate test report")
    public Response generateTestReport(Map<String, Object> reportData) {
        log.info("Generating test report");
        
        if (authToken == null) {
            throw new IllegalStateException("Authentication token is required to generate test report");
        }
        
        return getAuthSpec(authToken)
                .body(reportData)
                .when()
                .post(TESTING_ENDPOINT + REPORTS_ENDPOINT)
                .then()
                .extract()
                .response();
    }

    /**
     * Get all test reports
     */
    @Step("Get all test reports")
    public Response getAllTestReports() {
        log.info("Getting all test reports");
        
        if (authToken != null) {
            return getAuthSpec(authToken)
                    .when()
                    .get(TESTING_ENDPOINT + REPORTS_ENDPOINT)
                    .then()
                    .extract()
                    .response();
        } else {
            return get(TESTING_ENDPOINT + REPORTS_ENDPOINT);
        }
    }

    /**
     * Get test statistics
     */
    @Step("Get test statistics")
    public Response getTestStatistics() {
        log.info("Getting test statistics");
        
        if (authToken != null) {
            return getAuthSpec(authToken)
                    .when()
                    .get(TESTING_ENDPOINT + STATISTICS_ENDPOINT)
                    .then()
                    .extract()
                    .response();
        } else {
            return get(TESTING_ENDPOINT + STATISTICS_ENDPOINT);
        }
    }

    /**
     * Create test case with helper method
     */
    @Step("Create test case with name: {name}")
    public Map<String, Object> createTestCaseHelper(String name, String description, String status, String priority) {
        Map<String, Object> testCaseData = new HashMap<>();
        testCaseData.put("name", name);
        testCaseData.put("description", description);
        testCaseData.put("status", status);
        testCaseData.put("priority", priority);
        
        Response response = createTestCase(testCaseData);
        
        if (response.getStatusCode() == 201) {
            try {
                return objectMapper.readValue(response.getBody().asString(), 
                                             new TypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                log.error("Error parsing response", e);
                return null;
            }
        } else {
            log.error("Failed to create test case: {}", response.getStatusCode());
            return null;
        }
    }

    /**
     * Get all test cases as list
     */
    @Step("Get all test cases as list")
    public List<Map<String, Object>> getAllTestCasesAsList() {
        Response response = getAllTestCases();
        
        if (response.getStatusCode() == 200) {
            try {
                return objectMapper.readValue(response.getBody().asString(), 
                                             new TypeReference<List<Map<String, Object>>>() {});
            } catch (Exception e) {
                log.error("Error parsing response", e);
                return null;
            }
        } else {
            log.error("Failed to get test cases: {}", response.getStatusCode());
            return null;
        }
    }

    /**
     * Register test run with helper method
     */
    @Step("Register test run for test case ID: {testCaseId} with status: {status}")
    public Map<String, Object> registerTestRunHelper(long testCaseId, String status, long duration) {
        Map<String, Object> runData = new HashMap<>();
        runData.put("status", status);
        runData.put("duration", duration);
        
        Response response = registerTestRun(testCaseId, runData);
        
        if (response.getStatusCode() == 201) {
            try {
                return objectMapper.readValue(response.getBody().asString(), 
                                             new TypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                log.error("Error parsing response", e);
                return null;
            }
        } else {
            log.error("Failed to register test run: {}", response.getStatusCode());
            return null;
        }
    }
}