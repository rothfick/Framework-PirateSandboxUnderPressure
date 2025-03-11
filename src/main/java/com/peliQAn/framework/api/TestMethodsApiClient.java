package com.peliQAn.framework.api;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * API client for testing different HTTP methods and parameters
 */
@Slf4j
public class TestMethodsApiClient extends BaseApiClient {

    private static final String METHODS_ENDPOINT = "/test/methods";
    private static final String PARAMS_ENDPOINT = "/test/params";
    private static final String HEADERS_ENDPOINT = "/test/headers";
    private static final String COOKIES_ENDPOINT = "/test/cookies";
    private static final String SET_COOKIE_ENDPOINT = "/test/set-cookie";
    private static final String CONTENT_TYPES_ENDPOINT = "/test/content-types";
    private static final String UPLOAD_ENDPOINT = "/test/upload";

    /**
     * Test GET method
     */
    @Step("Test GET method")
    public Response testGet() {
        log.info("Testing GET method");
        return get(METHODS_ENDPOINT);
    }

    /**
     * Test POST method
     */
    @Step("Test POST method with body: {requestBody}")
    public Response testPost(Map<String, Object> requestBody) {
        log.info("Testing POST method");
        return post(METHODS_ENDPOINT, requestBody);
    }

    /**
     * Test PUT method
     */
    @Step("Test PUT method with body: {requestBody}")
    public Response testPut(Map<String, Object> requestBody) {
        log.info("Testing PUT method");
        return put(METHODS_ENDPOINT, requestBody);
    }

    /**
     * Test PATCH method
     */
    @Step("Test PATCH method with body: {requestBody}")
    public Response testPatch(Map<String, Object> requestBody) {
        log.info("Testing PATCH method");
        return patch(METHODS_ENDPOINT, requestBody);
    }

    /**
     * Test DELETE method
     */
    @Step("Test DELETE method")
    public Response testDelete() {
        log.info("Testing DELETE method");
        return delete(METHODS_ENDPOINT);
    }

    /**
     * Test query parameters
     */
    @Step("Test query parameters: {queryParams}")
    public Response testQueryParams(Map<String, Object> queryParams) {
        log.info("Testing query parameters: {}", queryParams);
        return get(PARAMS_ENDPOINT, queryParams);
    }

    /**
     * Test path parameters
     */
    @Step("Test path parameter: {pathParam}")
    public Response testPathParam(String pathParam) {
        log.info("Testing path parameter: {}", pathParam);
        return get(PARAMS_ENDPOINT + "/" + pathParam);
    }

    /**
     * Test headers
     */
    @Step("Test headers: {headers}")
    public Response testHeaders(Map<String, String> headers) {
        log.info("Testing headers");
        
        return RestAssured.given()
                .spec(requestSpec)
                .headers(headers)
                .when()
                .get(HEADERS_ENDPOINT)
                .then()
                .spec(responseSpec)
                .extract()
                .response();
    }

    /**
     * Test setting cookies
     */
    @Step("Test setting cookie")
    public Response testSetCookie() {
        log.info("Testing setting cookie");
        return get(SET_COOKIE_ENDPOINT);
    }

    /**
     * Test getting cookies
     */
    @Step("Test getting cookies")
    public Response testGetCookies() {
        log.info("Testing getting cookies");
        
        // First set the cookie
        Response setCookieResponse = testSetCookie();
        
        // Extract the cookie
        String cookieValue = setCookieResponse.getCookie("test-cookie");
        
        // Make a request with the cookie
        return RestAssured.given()
                .spec(requestSpec)
                .cookie("test-cookie", cookieValue)
                .when()
                .get(COOKIES_ENDPOINT)
                .then()
                .spec(responseSpec)
                .extract()
                .response();
    }

    /**
     * Test JSON content type
     */
    @Step("Test JSON content type")
    public Response testJsonContentType() {
        log.info("Testing JSON content type");
        return get(CONTENT_TYPES_ENDPOINT + "/json");
    }

    /**
     * Test XML content type
     */
    @Step("Test XML content type")
    public Response testXmlContentType() {
        log.info("Testing XML content type");
        
        return RestAssured.given()
                .spec(requestSpec)
                .accept(ContentType.XML)
                .when()
                .get(CONTENT_TYPES_ENDPOINT + "/xml")
                .then()
                .contentType(ContentType.XML)
                .extract()
                .response();
    }

    /**
     * Test text content type
     */
    @Step("Test text content type")
    public Response testTextContentType() {
        log.info("Testing text content type");
        
        return RestAssured.given()
                .spec(requestSpec)
                .accept(ContentType.TEXT)
                .when()
                .get(CONTENT_TYPES_ENDPOINT + "/text")
                .then()
                .contentType(ContentType.TEXT)
                .extract()
                .response();
    }

    /**
     * Test file upload
     */
    @Step("Test file upload: {filePath}")
    public Response testFileUpload(String filePath) {
        log.info("Testing file upload: {}", filePath);
        
        return RestAssured.given()
                .contentType("multipart/form-data")
                .multiPart("file", new File(filePath))
                .when()
                .post(UPLOAD_ENDPOINT)
                .then()
                .log().all()
                .extract()
                .response();
    }

    /**
     * Test multiple file upload
     */
    @Step("Test multiple file upload: {filePaths}")
    public Response testMultipleFileUpload(List<String> filePaths) {
        log.info("Testing multiple file upload");
        
        RequestSpecification request = RestAssured.given()
                .contentType("multipart/form-data");
        
        for (String filePath : filePaths) {
            request.multiPart("files", new File(filePath));
        }
        
        return request
                .when()
                .post(UPLOAD_ENDPOINT + "/multiple")
                .then()
                .log().all()
                .extract()
                .response();
    }

    /**
     * Test all HTTP methods in sequence (helper method)
     */
    @Step("Test all HTTP methods in sequence")
    public Map<String, Response> testAllHttpMethods() {
        Map<String, Response> responses = new HashMap<>();
        
        // Create test data
        Map<String, Object> testData = new HashMap<>();
        testData.put("key1", "value1");
        testData.put("key2", 123);
        testData.put("key3", true);
        
        // Test each method
        responses.put("GET", testGet());
        responses.put("POST", testPost(testData));
        responses.put("PUT", testPut(testData));
        responses.put("PATCH", testPatch(testData));
        responses.put("DELETE", testDelete());
        
        log.info("Completed testing all HTTP methods");
        return responses;
    }

    /**
     * Test various parameter types (helper method)
     */
    @Step("Test various parameter types")
    public Map<String, Response> testVariousParameterTypes() {
        Map<String, Response> responses = new HashMap<>();
        
        // Test query parameters
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("param1", "value1");
        queryParams.put("param2", "123");
        queryParams.put("param3", "a,b,c");
        responses.put("QUERY_PARAMS", testQueryParams(queryParams));
        
        // Test path parameter
        responses.put("PATH_PARAM", testPathParam("testValue"));
        
        // Test headers
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Custom-Header1", "Header-Value-1");
        headers.put("X-Custom-Header2", "Header-Value-2");
        responses.put("HEADERS", testHeaders(headers));
        
        // Test cookies
        responses.put("COOKIES", testGetCookies());
        
        log.info("Completed testing various parameter types");
        return responses;
    }

    /**
     * Test various content types (helper method)
     */
    @Step("Test various content types")
    public Map<String, Response> testVariousContentTypes() {
        Map<String, Response> responses = new HashMap<>();
        
        responses.put("JSON", testJsonContentType());
        responses.put("XML", testXmlContentType());
        responses.put("TEXT", testTextContentType());
        
        log.info("Completed testing various content types");
        return responses;
    }
}