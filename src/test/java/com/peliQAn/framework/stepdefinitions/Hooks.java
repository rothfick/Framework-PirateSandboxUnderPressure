package com.peliQAn.framework.stepdefinitions;

import com.peliQAn.framework.config.PropertyManager;
import com.peliQAn.framework.core.DriverFactory;
import com.peliQAn.framework.utils.ScreenshotUtils;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

/**
 * Cucumber hooks for setup and teardown
 */
@Slf4j
public class Hooks {
    
    private WebDriver driver;
    private final PropertyManager propertyManager = PropertyManager.getInstance();
    
    /**
     * Setup before each scenario
     */
    @Before(value = "@ui", order = 1)
    public void setupUI() {
        log.info("Setting up WebDriver for UI test");
        driver = DriverFactory.initDriver();
    }
    
    /**
     * Setup before API scenarios
     */
    @Before(value = "@api", order = 1)
    public void setupAPI() {
        log.info("Setting up API test");
        // Any setup specific to API tests
    }
    
    /**
     * Take screenshot after each UI test step if enabled
     */
    @AfterStep(value = "@ui")
    public void takeScreenshotAfterStep(Scenario scenario) {
        if (propertyManager.getBooleanProperty("screenshot.on.step", false)) {
            driver = DriverFactory.getDriver();
            if (driver != null) {
                String screenshotName = scenario.getName() + "_" + System.currentTimeMillis();
                ScreenshotUtils.takeScreenshot(driver, screenshotName);
            }
        }
    }
    
    /**
     * Take screenshot after failed UI test if enabled
     */
    @After(value = "@ui")
    public void tearDownUI(Scenario scenario) {
        try {
            driver = DriverFactory.getDriver();
            if (driver != null) {
                if (scenario.isFailed() && propertyManager.getBooleanProperty("screenshot.on.failure", true)) {
                    String screenshotName = "failure_" + scenario.getName();
                    ScreenshotUtils.takeScreenshot(driver, screenshotName);
                    log.info("Captured failure screenshot: {}", screenshotName);
                }
            }
        } finally {
            // Always quit the driver
            DriverFactory.quitDriver();
            log.info("WebDriver closed after UI test");
        }
    }
    
    /**
     * Teardown after API test
     */
    @After(value = "@api")
    public void tearDownAPI() {
        log.info("Tearing down API test");
        // Any teardown specific to API tests
    }
}