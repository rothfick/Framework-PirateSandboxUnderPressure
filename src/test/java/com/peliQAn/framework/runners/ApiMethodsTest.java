package com.peliQAn.framework.runners;

import com.peliQAn.framework.api.TestMethodsApiClient;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * Test class for API methods endpoints
 */
@Epic("API Tests")
@Feature("API Methods")
public class ApiMethodsTest {

    private TestMethodsApiClient apiClient;

    @BeforeClass
    public void setup() {
        apiClient = new TestMethodsApiClient();
    }

    /**
     * Simple approach: Test basic HTTP methods
     */
    @Test(description = "Test basic HTTP methods")
    @Severity(SeverityLevel.CRITICAL)
    @Story("HTTP Methods")
    @Description("Test basic HTTP methods with simple approach")
    public void testBasicHttpMethods() {
        // Test GET
        Response getResponse = apiClient.testGet();
        assertEquals(getResponse.getStatusCode(), 200, "GET should return 200");
        assertEquals(getResponse.jsonPath().getString("method"), "GET", "Response should show GET method");
        
        // Test POST
        Map<String, Object> postData = new HashMap<>();
        postData.put("name", "John Doe");
        postData.put("age", 30);
        
        Response postResponse = apiClient.testPost(postData);
        assertEquals(postResponse.getStatusCode(), 200, "POST should return 200");
        assertEquals(postResponse.jsonPath().getString("method"), "POST", "Response should show POST method");
        assertEquals(postResponse.jsonPath().getString("body.name"), "John Doe", "Response should include posted data");
        
        // Test PUT
        Map<String, Object> putData = new HashMap<>();
        putData.put("id", 1);
        putData.put("name", "Jane Doe");
        
        Response putResponse = apiClient.testPut(putData);
        assertEquals(putResponse.getStatusCode(), 200, "PUT should return 200");
        assertEquals(putResponse.jsonPath().getString("method"), "PUT", "Response should show PUT method");
        
        // Test PATCH
        Map<String, Object> patchData = new HashMap<>();
        patchData.put("name", "Jane Smith");
        
        Response patchResponse = apiClient.testPatch(patchData);
        assertEquals(patchResponse.getStatusCode(), 200, "PATCH should return 200");
        assertEquals(patchResponse.jsonPath().getString("method"), "PATCH", "Response should show PATCH method");
        
        // Test DELETE
        Response deleteResponse = apiClient.testDelete();
        assertEquals(deleteResponse.getStatusCode(), 200, "DELETE should return 200");
        assertEquals(deleteResponse.jsonPath().getString("method"), "DELETE", "Response should show DELETE method");
    }

    /**
     * Advanced approach: Test parameters and headers
     */
    @Test(description = "Test API parameters and headers")
    @Severity(SeverityLevel.CRITICAL)
    @Story("API Parameters")
    @Description("Test API parameters and headers with advanced approach")
    public void testAdvancedParameters() {
        // Test query parameters
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("param1", "value1");
        queryParams.put("param2", 123);
        queryParams.put("param3", "a,b,c");
        
        Response queryParamsResponse = apiClient.testQueryParams(queryParams);
        assertEquals(queryParamsResponse.getStatusCode(), 200, "Query params request should return 200");
        assertEquals(queryParamsResponse.jsonPath().getString("params.param1"), "value1", 
                    "Response should include query param1");
        assertEquals(queryParamsResponse.jsonPath().getInt("params.param2"), 123, 
                    "Response should include query param2");
        
        // Test path parameter
        String pathValue = "testPathValue";
        Response pathParamResponse = apiClient.testPathParam(pathValue);
        assertEquals(pathParamResponse.getStatusCode(), 200, "Path param request should return 200");
        assertEquals(pathParamResponse.jsonPath().getString("pathParam"), pathValue, 
                    "Response should include path parameter");
        
        // Test headers
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Custom-Header1", "Header-Value-1");
        headers.put("X-Custom-Header2", "Header-Value-2");
        
        Response headersResponse = apiClient.testHeaders(headers);
        assertEquals(headersResponse.getStatusCode(), 200, "Headers request should return 200");
        
        // Verify headers are returned in the response (headers are typically lowercase in response)
        assertTrue(headersResponse.jsonPath().getString("headers").toLowerCase()
                  .contains("x-custom-header1"), "Response should include custom header1");
        assertTrue(headersResponse.jsonPath().getString("headers").toLowerCase()
                  .contains("header-value-1"), "Response should include custom header1 value");
        
        // Test cookies
        Response cookiesResponse = apiClient.testGetCookies();
        assertEquals(cookiesResponse.getStatusCode(), 200, "Cookies request should return 200");
        assertTrue(cookiesResponse.jsonPath().getString("cookie").contains("test-cookie"), 
                  "Response should include test cookie");
    }

    /**
     * Test content types
     */
    @Test(description = "Test different content types")
    @Severity(SeverityLevel.NORMAL)
    @Story("Content Types")
    @Description("Test different content types response handling")
    public void testContentTypes() {
        // Test JSON content type
        Response jsonResponse = apiClient.testJsonContentType();
        assertEquals(jsonResponse.getStatusCode(), 200, "JSON content type should return 200");
        assertEquals(jsonResponse.getContentType(), "application/json", "Response should be JSON");
        assertNotNull(jsonResponse.jsonPath().get("message"), "JSON response should have message field");
        
        // Test XML content type
        Response xmlResponse = apiClient.testXmlContentType();
        assertEquals(xmlResponse.getStatusCode(), 200, "XML content type should return 200");
        assertTrue(xmlResponse.getContentType().contains("application/xml"), "Response should be XML");
        
        // Test text content type
        Response textResponse = apiClient.testTextContentType();
        assertEquals(textResponse.getStatusCode(), 200, "Text content type should return 200");
        assertTrue(textResponse.getContentType().contains("text/plain"), "Response should be plain text");
    }

    /**
     * Comprehensive test using helper methods
     */
    @Test(description = "Comprehensive API test using helper methods")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Comprehensive API Test")
    @Description("Test multiple API aspects using helper methods in a single test")
    public void testComprehensiveApi() {
        // Test all HTTP methods
        Map<String, Response> methodResponses = apiClient.testAllHttpMethods();
        
        for (Map.Entry<String, Response> entry : methodResponses.entrySet()) {
            String method = entry.getKey();
            Response response = entry.getValue();
            
            assertEquals(response.getStatusCode(), 200, method + " should return 200");
            assertEquals(response.jsonPath().getString("method"), method, 
                        "Response should show " + method + " method");
        }
        
        // Test various parameter types
        Map<String, Response> paramResponses = apiClient.testVariousParameterTypes();
        
        // Verify query params response
        Response queryParamsResponse = paramResponses.get("QUERY_PARAMS");
        assertEquals(queryParamsResponse.getStatusCode(), 200, "Query params should return 200");
        
        // Verify path param response
        Response pathParamResponse = paramResponses.get("PATH_PARAM");
        assertEquals(pathParamResponse.getStatusCode(), 200, "Path param should return 200");
        
        // Verify headers response
        Response headersResponse = paramResponses.get("HEADERS");
        assertEquals(headersResponse.getStatusCode(), 200, "Headers should return 200");
        
        // Test content types
        Map<String, Response> contentTypeResponses = apiClient.testVariousContentTypes();
        
        // Verify JSON response
        Response jsonResponse = contentTypeResponses.get("JSON");
        assertEquals(jsonResponse.getStatusCode(), 200, "JSON content should return 200");
        assertEquals(jsonResponse.getContentType(), "application/json", "Response should be JSON");
        
        // Verify XML response
        Response xmlResponse = contentTypeResponses.get("XML");
        assertEquals(xmlResponse.getStatusCode(), 200, "XML content should return 200");
        assertTrue(xmlResponse.getContentType().contains("application/xml"), "Response should be XML");
        
        // Verify text response
        Response textResponse = contentTypeResponses.get("TEXT");
        assertEquals(textResponse.getStatusCode(), 200, "Text content should return 200");
        assertTrue(textResponse.getContentType().contains("text/plain"), "Response should be plain text");
    }

    /**
     * Edge case tests for API methods
     */
    @Test(description = "Edge cases for API methods")
    @Severity(SeverityLevel.NORMAL)
    @Story("API Methods Edge Cases")
    @Description("Test edge cases for API methods")
    public void testApiMethodsEdgeCases() {
        // Test empty body
        Map<String, Object> emptyData = new HashMap<>();
        Response emptyPostResponse = apiClient.testPost(emptyData);
        assertEquals(emptyPostResponse.getStatusCode(), 200, "POST with empty body should return 200");
        
        // Test large payload
        Map<String, Object> largeData = new HashMap<>();
        StringBuilder largeString = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeString.append("Lorem ipsum dolor sit amet. ");
        }
        largeData.put("largeText", largeString.toString());
        
        Response largePostResponse = apiClient.testPost(largeData);
        assertEquals(largePostResponse.getStatusCode(), 200, "POST with large body should return 200");
        
        // Test special characters in parameters
        Map<String, Object> specialCharsParams = new HashMap<>();
        specialCharsParams.put("specialChars", "!@#$%^&*()_+-=[]{}|;':\",./<>?");
        
        Response specialCharsResponse = apiClient.testQueryParams(specialCharsParams);
        assertEquals(specialCharsResponse.getStatusCode(), 200, 
                    "Query params with special chars should return 200");
        
        // Test Unicode characters
        Map<String, Object> unicodeData = new HashMap<>();
        unicodeData.put("unicodeText", "こんにちは世界 • 你好世界 • مرحبا بالعالم");
        
        Response unicodeResponse = apiClient.testPost(unicodeData);
        assertEquals(unicodeResponse.getStatusCode(), 200, "POST with Unicode text should return 200");
        
        // Test very long header value
        Map<String, String> longHeaders = new HashMap<>();
        StringBuilder longHeaderValue = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longHeaderValue.append("ABCDEFGHIJ");
        }
        longHeaders.put("X-Long-Header", longHeaderValue.toString());
        
        Response longHeaderResponse = apiClient.testHeaders(longHeaders);
        assertEquals(longHeaderResponse.getStatusCode(), 200, "Headers with long value should return 200");
    }
}