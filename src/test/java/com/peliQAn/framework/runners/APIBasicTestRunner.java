package com.peliQAn.framework.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Test runner for basic API tests
 */
@CucumberOptions(
        features = {"src/test/resources/features/api_basic.feature"},
        glue = {"com.peliQAn.framework.stepdefinitions"},
        plugin = {
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "pretty",
                "html:target/cucumber-reports/api-basic-report.html",
                "json:target/cucumber-reports/api-basic-report.json"
        },
        tags = "@api"
)
public class APIBasicTestRunner extends AbstractTestNGCucumberTests {
    
    /**
     * Runs tests in parallel
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}