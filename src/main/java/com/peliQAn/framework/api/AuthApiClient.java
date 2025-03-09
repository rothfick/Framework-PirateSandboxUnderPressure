package com.peliQAn.framework.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * API client for authentication endpoints
 */
@Slf4j
public class AuthApiClient extends BaseApiClient {

    private static final String AUTH_ENDPOINT = "/auth";
    private static final String REGISTER_ENDPOINT = AUTH_ENDPOINT + "/register";
    private static final String LOGIN_ENDPOINT = AUTH_ENDPOINT + "/login";
    private static final String REFRESH_ENDPOINT = AUTH_ENDPOINT + "/refresh";

    /**
     * Register a new user
     */
    @Step("Register new user with username: {username}")
    public Response registerUser(String username, String email, String password) {
        log.info("Registering new user: {}, {}", username, email);
        
        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", email);
        userData.put("password", password);
        
        return post(REGISTER_ENDPOINT, userData);
    }

    /**
     * Login user
     */
    @Step("Login user with username: {username}")
    public Response login(String username, String password) {
        log.info("Logging in user: {}", username);
        
        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", username);
        loginData.put("password", password);
        
        return post(LOGIN_ENDPOINT, loginData);
    }

    /**
     * Refresh authentication token
     */
    @Step("Refresh authentication token")
    public Response refreshToken(String refreshToken) {
        log.info("Refreshing authentication token");
        
        Map<String, String> refreshData = new HashMap<>();
        refreshData.put("refreshToken", refreshToken);
        
        return post(REFRESH_ENDPOINT, refreshData);
    }

    /**
     * Login and extract token (helper method)
     */
    @Step("Login and extract token for username: {username}")
    public String loginAndExtractToken(String username, String password) {
        Response response = login(username, password);
        
        if (response.getStatusCode() == 200) {
            String token = response.jsonPath().getString("token");
            log.info("Successfully extracted token for user: {}", username);
            return token;
        } else {
            log.error("Failed to login user. Status code: {}", response.getStatusCode());
            return null;
        }
    }

    /**
     * Register and login (helper method)
     */
    @Step("Register and login user with username: {username}")
    public String registerAndLogin(String username, String email, String password) {
        Response registerResponse = registerUser(username, email, password);
        
        if (registerResponse.getStatusCode() == 201) {
            log.info("Successfully registered user: {}", username);
            return loginAndExtractToken(username, password);
        } else {
            log.error("Failed to register user. Status code: {}", registerResponse.getStatusCode());
            return null;
        }
    }

    /**
     * Register with random credentials (helper method)
     */
    @Step("Register with random credentials")
    public Map<String, String> registerWithRandomCredentials() {
        String randomSuffix = String.valueOf(System.currentTimeMillis());
        String username = "user_" + randomSuffix;
        String email = "user_" + randomSuffix + "@example.com";
        String password = "Pass_" + randomSuffix;
        
        Response registerResponse = registerUser(username, email, password);
        
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("email", email);
        credentials.put("password", password);
        
        if (registerResponse.getStatusCode() == 201) {
            log.info("Successfully registered random user: {}", username);
            credentials.put("status", "success");
        } else {
            log.error("Failed to register random user. Status code: {}", registerResponse.getStatusCode());
            credentials.put("status", "failed");
            credentials.put("statusCode", String.valueOf(registerResponse.getStatusCode()));
            credentials.put("responseBody", registerResponse.getBody().asString());
        }
        
        return credentials;
    }

    /**
     * Login with invalid credentials (negative test)
     */
    @Step("Login with invalid credentials")
    public Response loginWithInvalidCredentials(String username, String password) {
        log.info("Attempting login with invalid credentials: {}", username);
        
        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", username);
        loginData.put("password", password);
        
        return post(LOGIN_ENDPOINT, loginData);
    }

    /**
     * Register with existing username (negative test)
     */
    @Step("Register with existing username: {username}")
    public Response registerWithExistingUsername(String username, String email, String password) {
        log.info("Attempting to register with existing username: {}", username);
        
        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", email);
        userData.put("password", password);
        
        return post(REGISTER_ENDPOINT, userData);
    }

    /**
     * Refresh with invalid token (negative test)
     */
    @Step("Refresh with invalid token")
    public Response refreshWithInvalidToken(String invalidToken) {
        log.info("Attempting to refresh with invalid token");
        
        Map<String, String> refreshData = new HashMap<>();
        refreshData.put("refreshToken", invalidToken);
        
        return post(REFRESH_ENDPOINT, refreshData);
    }
}