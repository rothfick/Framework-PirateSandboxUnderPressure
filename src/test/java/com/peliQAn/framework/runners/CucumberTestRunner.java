package com.peliQAn.framework.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * TestNG runner for Cucumber tests
 * Configures and runs Cucumber tests using TestNG
 */
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"com.peliQAn.framework.stepdefinitions"},
    plugin = {
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
        "pretty",
        "html:target/cucumber-reports/cucumber-pretty.html",
        "json:target/cucumber-reports/CucumberTestReport.json"
    },
    tags = "not @ignore"
)
public class CucumberTestRunner extends AbstractTestNGCucumberTests {
    
    /**
     * Runs tests in parallel if parallel parameter is set
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}