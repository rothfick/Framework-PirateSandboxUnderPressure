package com.peliQAn.framework.api;

import com.peliQAn.framework.config.PropertyManager;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Base class for API clients with common methods
 */
@Slf4j
public abstract class BaseApiClient {
    protected final String baseUrl;
    protected final int timeout;
    protected final int retryCount;
    protected final int retryDelayMs;
    protected final RequestSpecification requestSpec;
    protected final ResponseSpecification responseSpec;

    protected BaseApiClient() {
        PropertyManager propertyManager = PropertyManager.getInstance();
        this.baseUrl = propertyManager.getProperty("api.baseUrl");
        this.timeout = propertyManager.getIntProperty("api.timeout", 30);
        this.retryCount = propertyManager.getIntProperty("api.retryCount", 3);
        this.retryDelayMs = propertyManager.getIntProperty("api.retryDelayMs", 1000);

        // Set up base request specification
        this.requestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .log(LogDetail.ALL)
                .build();

        // Set up base response specification
        this.responseSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
        
        log.info("Initialized API client with base URL: {}", baseUrl);
    }

    /**
     * Send GET request to endpoint
     *
     * @param endpoint API endpoint path
     * @return Response object
     */
    @Step("Send GET request to {endpoint}")
    protected Response get(String endpoint) {
        log.info("Sending GET request to: {}{}", baseUrl, endpoint);
        return RestAssured.given()
                .spec(requestSpec)
                .when()
                .get(endpoint)
                .then()
                .spec(responseSpec)
                .extract()
                .response();
    }

    /**
     * Send GET request to endpoint with query parameters
     *
     * @param endpoint API endpoint path
     * @param queryParams Query parameters
     * @return Response object
     */
    @Step("Send GET request to {endpoint} with query parameters")
    protected Response get(String endpoint, Map<String, Object> queryParams) {
        log.info("Sending GET request to: {}{} with query parameters: {}", baseUrl, endpoint, queryParams);
        return RestAssured.given()
                .spec(requestSpec)
                .queryParams(queryParams)
                .when()
                .get(endpoint)
                .then()
                .spec(responseSpec)
                .extract()
                .response();
    }

    /**
     * Send POST request to endpoint with request body
     *
     * @param endpoint API endpoint path
     * @param requestBody Request body object
     * @return Response object
     */
    @Step("Send POST request to {endpoint}")
    protected Response post(String endpoint, Object requestBody) {
        log.info("Sending POST request to: {}{}", baseUrl, endpoint);
        return RestAssured.given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post(endpoint)
                .then()
                .spec(responseSpec)
                .extract()
                .response();
    }

    /**
     * Send PUT request to endpoint with request body
     *
     * @param endpoint API endpoint path
     * @param requestBody Request body object
     * @return Response object
     */
    @Step("Send PUT request to {endpoint}")
    protected Response put(String endpoint, Object requestBody) {
        log.info("Sending PUT request to: {}{}", baseUrl, endpoint);
        return RestAssured.given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .put(endpoint)
                .then()
                .spec(responseSpec)
                .extract()
                .response();
    }

    /**
     * Send PATCH request to endpoint with request body
     *
     * @param endpoint API endpoint path
     * @param requestBody Request body object
     * @return Response object
     */
    @Step("Send PATCH request to {endpoint}")
    protected Response patch(String endpoint, Object requestBody) {
        log.info("Sending PATCH request to: {}{}", baseUrl, endpoint);
        return RestAssured.given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .patch(endpoint)
                .then()
                .spec(responseSpec)
                .extract()
                .response();
    }

    /**
     * Send DELETE request to endpoint
     *
     * @param endpoint API endpoint path
     * @return Response object
     */
    @Step("Send DELETE request to {endpoint}")
    protected Response delete(String endpoint) {
        log.info("Sending DELETE request to: {}{}", baseUrl, endpoint);
        return RestAssured.given()
                .spec(requestSpec)
                .when()
                .delete(endpoint)
                .then()
                .spec(responseSpec)
                .extract()
                .response();
    }

    /**
     * Create custom request specification with authentication
     *
     * @param token Authentication token
     * @return RequestSpecification
     */
    protected RequestSpecification getAuthSpec(String token) {
        return RestAssured.given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token);
    }

    /**
     * Get response time in specified time unit
     *
     * @param response Response object
     * @param timeUnit Time unit
     * @return Response time in specified time unit
     */
    protected long getResponseTime(Response response, TimeUnit timeUnit) {
        return response.getTimeIn(timeUnit);
    }

    /**
     * Validate response against JSON schema
     *
     * @param response Response object
     * @param schemaPath Path to JSON schema file
     */
    @Step("Validate response against JSON schema")
    protected void validateResponseSchema(Response response, String schemaPath) {
        response.then().assertThat().body(io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath(schemaPath));
        log.info("Response validated against schema: {}", schemaPath);
    }
    
    /**
     * Upload file
     * 
     * @param endpoint API endpoint path
     * @param filePath Path to file
     * @param fileParameterName Name of file parameter
     * @return Response object
     */
    @Step("Upload file to {endpoint}")
    protected Response uploadFile(String endpoint, String filePath, String fileParameterName) {
        log.info("Uploading file to: {}{}", baseUrl, endpoint);
        return RestAssured.given()
                .contentType("multipart/form-data")
                .multiPart(fileParameterName, new File(filePath))
                .when()
                .post(endpoint)
                .then()
                .log().all()
                .extract()
                .response();
    }
    
    /**
     * Upload multiple files
     * 
     * @param endpoint API endpoint path
     * @param filePaths List of file paths
     * @param fileParameterName Name of file parameter
     * @return Response object
     */
    @Step("Upload multiple files to {endpoint}")
    protected Response uploadMultipleFiles(String endpoint, List<String> filePaths, String fileParameterName) {
        log.info("Uploading multiple files to: {}{}", baseUrl, endpoint);
        RequestSpecification request = RestAssured.given().contentType("multipart/form-data");
        
        for (String filePath : filePaths) {
            request.multiPart(fileParameterName, new File(filePath));
        }
        
        return request
                .when()
                .post(endpoint)
                .then()
                .log().all()
                .extract()
                .response();
    }
    
    /**
     * Execute request with retry
     * 
     * @param request Function to execute request
     * @param expectedStatusCode Expected status code
     * @return Response object
     */
    @Step("Execute request with retry")
    protected Response executeWithRetry(Function<Void, Response> request, int expectedStatusCode) {
        int attempts = 0;
        Response response = null;
        boolean success = false;
        
        while (attempts < retryCount && !success) {
            try {
                if (attempts > 0) {
                    log.info("Retrying request (attempt {} of {})", attempts + 1, retryCount);
                    Thread.sleep(retryDelayMs);
                }
                
                response = request.apply(null);
                if (response.getStatusCode() == expectedStatusCode) {
                    success = true;
                } else {
                    log.warn("Request failed with status code: {}, expected: {}", 
                            response.getStatusCode(), expectedStatusCode);
                }
            } catch (Exception e) {
                log.error("Request failed with exception", e);
            }
            
            attempts++;
        }
        
        if (!success && response == null) {
            throw new RuntimeException("All retry attempts failed with exceptions");
        }
        
        return response;
    }
}