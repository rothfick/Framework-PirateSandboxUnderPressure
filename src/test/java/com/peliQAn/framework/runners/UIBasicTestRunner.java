package com.peliQAn.framework.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Test runner for basic UI tests
 */
@CucumberOptions(
        features = {"src/test/resources/features/ui_basic.feature"},
        glue = {"com.peliQAn.framework.stepdefinitions"},
        plugin = {
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "pretty",
                "html:target/cucumber-reports/ui-basic-report.html",
                "json:target/cucumber-reports/ui-basic-report.json"
        },
        tags = "@ui"
)
public class UIBasicTestRunner extends AbstractTestNGCucumberTests {
    
    /**
     * Runs tests in parallel
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}