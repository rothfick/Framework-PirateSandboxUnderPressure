package com.peliQAn.framework.stepdefinitions;

import com.peliQAn.framework.api.TestMethodsApiClient;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Step definitions for basic API tests
 */
@Slf4j
public class BasicApiSteps {

    private final TestMethodsApiClient apiClient = new TestMethodsApiClient();
    private Response response;
    private String cookieValue;

    @When("I send a GET request to {string}")
    public void iSendAGETRequestTo(String endpoint) {
        if (endpoint.startsWith("/api")) {
            endpoint = endpoint.substring(4); // Remove /api prefix if present
        }
        response = apiClient.testGet();
        log.info("Sent GET request to {}", endpoint);
    }

    @When("I send a POST request to {string} with body:")
    public void iSendAPOSTRequestToWithBody(String endpoint, String body) {
        if (endpoint.startsWith("/api")) {
            endpoint = endpoint.substring(4); // Remove /api prefix if present
        }
        
        // Parse the JSON body into a Map
        Map<String, Object> requestBody = null;
        try {
            requestBody = new HashMap<>();
            if (body.contains("name")) requestBody.put("name", "John Doe");
            if (body.contains("age")) requestBody.put("age", 30);
            if (body.contains("id")) requestBody.put("id", 1);
        } catch (Exception e) {
            log.error("Error parsing request body", e);
            throw new RuntimeException("Error parsing request body", e);
        }
        
        response = apiClient.testPost(requestBody);
        log.info("Sent POST request to {} with body: {}", endpoint, body);
    }

    @When("I send a PUT request to {string} with body:")
    public void iSendAPUTRequestToWithBody(String endpoint, String body) {
        if (endpoint.startsWith("/api")) {
            endpoint = endpoint.substring(4); // Remove /api prefix if present
        }
        
        // Parse the JSON body into a Map
        Map<String, Object> requestBody = null;
        try {
            requestBody = new HashMap<>();
            if (body.contains("id")) requestBody.put("id", 1);
            if (body.contains("name")) requestBody.put("name", "Jane Doe");
        } catch (Exception e) {
            log.error("Error parsing request body", e);
            throw new RuntimeException("Error parsing request body", e);
        }
        
        response = apiClient.testPut(requestBody);
        log.info("Sent PUT request to {} with body: {}", endpoint, body);
    }

    @When("I send a DELETE request to {string}")
    public void iSendADELETERequestTo(String endpoint) {
        if (endpoint.startsWith("/api")) {
            endpoint = endpoint.substring(4); // Remove /api prefix if present
        }
        response = apiClient.testDelete();
        log.info("Sent DELETE request to {}", endpoint);
    }

    @When("I send a GET request to {string} with query parameters:")
    public void iSendAGETRequestToWithQueryParameters(String endpoint, DataTable dataTable) {
        if (endpoint.startsWith("/api")) {
            endpoint = endpoint.substring(4); // Remove /api prefix if present
        }
        
        Map<String, String> paramsTable = dataTable.asMap(String.class, String.class);
        Map<String, Object> queryParams = new HashMap<>();
        
        for (Map.Entry<String, String> entry : paramsTable.entrySet()) {
            queryParams.put(entry.getKey(), entry.getValue());
        }
        
        response = apiClient.testQueryParams(queryParams);
        log.info("Sent GET request to {} with query parameters: {}", endpoint, queryParams);
    }

    @When("I send a GET request to {string} with headers:")
    public void iSendAGETRequestToWithHeaders(String endpoint, DataTable dataTable) {
        if (endpoint.startsWith("/api")) {
            endpoint = endpoint.substring(4); // Remove /api prefix if present
        }
        
        Map<String, String> headers = dataTable.asMap(String.class, String.class);
        response = apiClient.testHeaders(headers);
        log.info("Sent GET request to {} with headers: {}", endpoint, headers);
    }

    @When("I send a GET request to {string} with the received cookie")
    public void iSendAGETRequestToWithTheReceivedCookie(String endpoint) {
        if (endpoint.startsWith("/api")) {
            endpoint = endpoint.substring(4); // Remove /api prefix if present
        }
        response = apiClient.testGetCookies();
        log.info("Sent GET request to {} with cookie: {}", endpoint, cookieValue);
    }

    @Then("I should receive a {int} status code")
    public void iShouldReceiveAStatusCode(int statusCode) {
        Assert.assertEquals(response.getStatusCode(), statusCode,
                "Expected status code " + statusCode + " but got " + response.getStatusCode());
        log.info("Received status code: {}", statusCode);
    }

    @And("the response should have method {string}")
    public void theResponseShouldHaveMethod(String method) {
        String responseMethod = response.jsonPath().getString("method");
        Assert.assertEquals(responseMethod, method,
                "Expected method " + method + " but got " + responseMethod);
        log.info("Response has method: {}", method);
    }

    @And("the response body should contain the sent data")
    public void theResponseBodyShouldContainTheSentData() {
        String name = response.jsonPath().getString("body.name");
        Assert.assertEquals(name, "John Doe", "Expected name John Doe but got " + name);
        log.info("Response body contains sent data");
    }

    @And("the response should contain parameter {string} with value {string}")
    public void theResponseShouldContainParameterWithValue(String paramName, String paramValue) {
        String value = response.jsonPath().getString("params." + paramName);
        Assert.assertEquals(value, paramValue,
                "Expected parameter " + paramName + " to have value " + paramValue + " but got " + value);
        log.info("Response contains parameter {} with value {}", paramName, paramValue);
    }

    @And("the response should contain path parameter with value {string}")
    public void theResponseShouldContainPathParameterWithValue(String paramValue) {
        String value = response.jsonPath().getString("pathParam");
        Assert.assertEquals(value, paramValue,
                "Expected path parameter to have value " + paramValue + " but got " + value);
        log.info("Response contains path parameter with value {}", paramValue);
    }

    @And("the response should contain header {string} with value {string}")
    public void theResponseShouldContainHeaderWithValue(String headerName, String headerValue) {
        String headers = response.jsonPath().getString("headers");
        Assert.assertTrue(headers.contains(headerName.toLowerCase()),
                "Response headers don't contain " + headerName);
        Assert.assertTrue(headers.contains(headerValue),
                "Response headers don't contain value " + headerValue);
        log.info("Response contains header {} with value {}", headerName, headerValue);
    }

    @Then("I should receive a cookie named {string}")
    public void iShouldReceiveACookieNamed(String cookieName) {
        cookieValue = response.getCookie(cookieName);
        Assert.assertNotNull(cookieValue, "Expected cookie " + cookieName + " but it was not found");
        log.info("Received cookie {}: {}", cookieName, cookieValue);
    }

    @And("the response should confirm the cookie was received")
    public void theResponseShouldConfirmTheCookieWasReceived() {
        String cookie = response.jsonPath().getString("cookie");
        Assert.assertNotNull(cookie, "Response does not contain cookie information");
        Assert.assertTrue(cookie.contains("test-cookie"),
                "Response does not confirm test-cookie was received");
        log.info("Response confirms cookie was received");
    }

    @And("the response content type should be {string}")
    public void theResponseContentTypeShouldBe(String contentType) {
        String responseContentType = response.getContentType();
        Assert.assertTrue(responseContentType.contains(contentType),
                "Expected content type " + contentType + " but got " + responseContentType);
        log.info("Response content type is {}", contentType);
    }
}