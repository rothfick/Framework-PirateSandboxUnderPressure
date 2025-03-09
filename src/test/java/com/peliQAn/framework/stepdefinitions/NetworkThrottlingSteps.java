package com.peliQAn.framework.stepdefinitions;

import com.peliQAn.framework.pages.basic.AllElementsPage;
import com.peliQAn.framework.utils.NetworkUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

/**
 * Step definitions for network throttling tests
 */
@Slf4j
public class NetworkThrottlingSteps {

    private AllElementsPage allElementsPage;
    private NetworkUtils networkUtils;
    private long startTime;
    private long endTime;
    private long loadTimeWithThrottling;
    private long loadTimeWithoutThrottling;
    
    public NetworkThrottlingSteps() {
        allElementsPage = new AllElementsPage();
        networkUtils = new NetworkUtils();
    }
    
    @When("I enable network throttling with {string} preset")
    public void iEnableNetworkThrottlingWithPreset(String preset) {
        networkUtils.throttleNetwork(preset);
        log.info("Enabled network throttling with preset: {}", preset);
    }
    
    @And("I click the load data button")
    public void iClickTheLoadDataButton() {
        startTime = System.currentTimeMillis();
        allElementsPage.clickLoadDataButton();
        log.info("Clicked load data button");
    }
    
    @Then("I should see the loading indicator")
    public void iShouldSeeTheLoadingIndicator() {
        boolean isLoadingIndicatorVisible = allElementsPage.isLoadingIndicatorVisible();
        Assert.assertTrue(isLoadingIndicatorVisible, "Loading indicator should be visible");
        log.info("Loading indicator is visible");
    }
    
    @And("the data should load within a reasonable time")
    public void theDataShouldLoadWithinAReasonableTime() {
        // Wait for data to load
        allElementsPage.waitForDataToLoad();
        endTime = System.currentTimeMillis();
        loadTimeWithThrottling = endTime - startTime;
        
        // Verify data loaded
        int dataItemsCount = allElementsPage.getDataItemsCount();
        Assert.assertTrue(dataItemsCount > 0, "At least one data item should be loaded");
        
        // Reasonable time for throttled connection (adjust as needed)
        Assert.assertTrue(loadTimeWithThrottling > 1000, 
                "Load time should be noticeable with throttling");
        
        log.info("Data loaded in {} ms with throttling", loadTimeWithThrottling);
    }
    
    @When("I disable network throttling")
    public void iDisableNetworkThrottling() {
        networkUtils.resetNetworkThrottling();
        log.info("Disabled network throttling");
    }
    
    @And("I reload the page")
    public void iReloadThePage() {
        allElementsPage.refreshPage();
        log.info("Reloaded the page");
        
        startTime = System.currentTimeMillis();
        allElementsPage.clickLoadDataButton();
        log.info("Clicked load data button again");
    }
    
    @Then("the data should load much faster")
    public void theDataShouldLoadMuchFaster() {
        // Wait for data to load
        allElementsPage.waitForDataToLoad();
        endTime = System.currentTimeMillis();
        loadTimeWithoutThrottling = endTime - startTime;
        
        // Verify data loaded
        int dataItemsCount = allElementsPage.getDataItemsCount();
        Assert.assertTrue(dataItemsCount > 0, "At least one data item should be loaded");
        
        // Compare load times
        log.info("Load time without throttling: {} ms", loadTimeWithoutThrottling);
        log.info("Load time with throttling: {} ms", loadTimeWithThrottling);
        
        // The test should show a significant difference in load times
        Assert.assertTrue(loadTimeWithoutThrottling < loadTimeWithThrottling, 
                "Load time without throttling should be less than with throttling");
        
        double speedImprovement = (double) loadTimeWithThrottling / loadTimeWithoutThrottling;
        log.info("Speed improvement factor: {}", speedImprovement);
        
        // Expect at least 1.5x speed improvement
        Assert.assertTrue(speedImprovement > 1.5, 
                "Speed improvement should be at least 1.5x when throttling is disabled");
    }
}