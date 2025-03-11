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
 * API client for treasure endpoints
 */
@Slf4j
public class TreasureApiClient extends BaseApiClient {

    private static final String TREASURE_ENDPOINT = "/treasures";
    private final ObjectMapper objectMapper;
    private String authToken;

    public TreasureApiClient() {
        super();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Set authentication token for subsequent requests
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
        log.info("Set authentication token for treasure API client");
    }

    /**
     * Get all treasures
     */
    @Step("Get all treasures")
    public Response getAllTreasures() {
        log.info("Getting all treasures");
        
        if (authToken != null) {
            return getAuthSpec(authToken)
                    .when()
                    .get(TREASURE_ENDPOINT)
                    .then()
                    .extract()
                    .response();
        } else {
            return get(TREASURE_ENDPOINT);
        }
    }

    /**
     * Get treasure by ID
     */
    @Step("Get treasure by ID: {id}")
    public Response getTreasureById(long id) {
        log.info("Getting treasure by ID: {}", id);
        
        if (authToken != null) {
            return getAuthSpec(authToken)
                    .when()
                    .get(TREASURE_ENDPOINT + "/" + id)
                    .then()
                    .extract()
                    .response();
        } else {
            return get(TREASURE_ENDPOINT + "/" + id);
        }
    }

    /**
     * Create new treasure
     */
    @Step("Create new treasure")
    public Response createTreasure(Map<String, Object> treasureData) {
        log.info("Creating new treasure: {}", treasureData);
        
        if (authToken == null) {
            throw new IllegalStateException("Authentication token is required to create treasure");
        }
        
        return getAuthSpec(authToken)
                .body(treasureData)
                .when()
                .post(TREASURE_ENDPOINT)
                .then()
                .extract()
                .response();
    }

    /**
     * Update treasure
     */
    @Step("Update treasure with ID: {id}")
    public Response updateTreasure(long id, Map<String, Object> treasureData) {
        log.info("Updating treasure with ID {}: {}", id, treasureData);
        
        if (authToken == null) {
            throw new IllegalStateException("Authentication token is required to update treasure");
        }
        
        return getAuthSpec(authToken)
                .body(treasureData)
                .when()
                .put(TREASURE_ENDPOINT + "/" + id)
                .then()
                .extract()
                .response();
    }

    /**
     * Delete treasure
     */
    @Step("Delete treasure with ID: {id}")
    public Response deleteTreasure(long id) {
        log.info("Deleting treasure with ID: {}", id);
        
        if (authToken == null) {
            throw new IllegalStateException("Authentication token is required to delete treasure");
        }
        
        return getAuthSpec(authToken)
                .when()
                .delete(TREASURE_ENDPOINT + "/" + id)
                .then()
                .extract()
                .response();
    }

    /**
     * Search treasures by name
     */
    @Step("Search treasures by name: {name}")
    public Response searchTreasuresByName(String name) {
        log.info("Searching treasures by name: {}", name);
        
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("name", name);
        
        if (authToken != null) {
            return getAuthSpec(authToken)
                    .queryParams(queryParams)
                    .when()
                    .get(TREASURE_ENDPOINT + "/search")
                    .then()
                    .extract()
                    .response();
        } else {
            return get(TREASURE_ENDPOINT + "/search", queryParams);
        }
    }

    /**
     * Get discovered treasures
     */
    @Step("Get discovered treasures")
    public Response getDiscoveredTreasures() {
        log.info("Getting discovered treasures");
        
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("discovered", true);
        
        if (authToken != null) {
            return getAuthSpec(authToken)
                    .queryParams(queryParams)
                    .when()
                    .get(TREASURE_ENDPOINT)
                    .then()
                    .extract()
                    .response();
        } else {
            return get(TREASURE_ENDPOINT, queryParams);
        }
    }

    /**
     * Get undiscovered treasures
     */
    @Step("Get undiscovered treasures")
    public Response getUndiscoveredTreasures() {
        log.info("Getting undiscovered treasures");
        
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("discovered", false);
        
        if (authToken != null) {
            return getAuthSpec(authToken)
                    .queryParams(queryParams)
                    .when()
                    .get(TREASURE_ENDPOINT)
                    .then()
                    .extract()
                    .response();
        } else {
            return get(TREASURE_ENDPOINT, queryParams);
        }
    }

    /**
     * Create treasure with helper method
     */
    @Step("Create treasure with name: {name}, value: {value}")
    public Map<String, Object> createTreasureHelper(String name, int value, String description, String location, boolean discovered) {
        Map<String, Object> treasureData = new HashMap<>();
        treasureData.put("name", name);
        treasureData.put("value", value);
        treasureData.put("description", description);
        treasureData.put("location", location);
        treasureData.put("discovered", discovered);
        
        Response response = createTreasure(treasureData);
        
        if (response.getStatusCode() == 201) {
            try {
                return objectMapper.readValue(response.getBody().asString(), 
                                             new TypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                log.error("Error parsing response", e);
                return null;
            }
        } else {
            log.error("Failed to create treasure: {}", response.getStatusCode());
            return null;
        }
    }

    /**
     * Get all treasures as list
     */
    @Step("Get all treasures as list")
    public List<Map<String, Object>> getAllTreasuresAsList() {
        Response response = getAllTreasures();
        
        if (response.getStatusCode() == 200) {
            try {
                return objectMapper.readValue(response.getBody().asString(), 
                                             new TypeReference<List<Map<String, Object>>>() {});
            } catch (Exception e) {
                log.error("Error parsing response", e);
                return null;
            }
        } else {
            log.error("Failed to get treasures: {}", response.getStatusCode());
            return null;
        }
    }

    /**
     * Discover treasure
     */
    @Step("Mark treasure as discovered: {id}")
    public Response discoverTreasure(long id) {
        log.info("Marking treasure as discovered: {}", id);
        
        if (authToken == null) {
            throw new IllegalStateException("Authentication token is required to discover treasure");
        }
        
        // First get the treasure to ensure we don't overwrite other properties
        Response getTreasureResponse = getTreasureById(id);
        
        if (getTreasureResponse.getStatusCode() != 200) {
            log.error("Failed to get treasure: {}", getTreasureResponse.getStatusCode());
            return getTreasureResponse;
        }
        
        try {
            Map<String, Object> treasureData = objectMapper.readValue(
                    getTreasureResponse.getBody().asString(),
                    new TypeReference<Map<String, Object>>() {});
            
            // Update the discovered status
            treasureData.put("discovered", true);
            
            // Update the treasure
            return updateTreasure(id, treasureData);
            
        } catch (Exception e) {
            log.error("Error processing treasure data", e);
            throw new RuntimeException("Error processing treasure data", e);
        }
    }
}