package com.peliQAn.framework.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Test runner for advanced API tests
 */
@CucumberOptions(
        features = {"src/test/resources/features/api_advanced.feature"},
        glue = {"com.peliQAn.framework.stepdefinitions"},
        plugin = {
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "pretty",
                "html:target/cucumber-reports/api-advanced-report.html",
                "json:target/cucumber-reports/api-advanced-report.json"
        },
        tags = "@api"
)
public class AdvancedApiTestRunner extends AbstractTestNGCucumberTests {
    
    /**
     * Runs tests in parallel
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}