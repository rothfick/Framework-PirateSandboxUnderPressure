package com.peliQAn.framework.runners;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Parameters;
import org.testng.TestNG;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Comprehensive test suite that can run various test categories
 */
@Epic("Test Suites")
@Feature("Comprehensive Test Suite")
public class ComprehensiveTestSuite {
    
    private TestNG testNG;
    private Map<String, Class<?>[]> testGroups;
    
    @BeforeClass
    public void setup() {
        testNG = new TestNG();
        testGroups = new HashMap<>();
        
        // Define test categories
        testGroups.put("ui-basic", new Class<?>[] { UIBasicTestRunner.class });
        testGroups.put("ui-advanced", new Class<?>[] { UIAdvancedTestRunner.class });
        testGroups.put("ui-hardcore", new Class<?>[] { HardcoreChallengesTestRunner.class });
        testGroups.put("api-basic", new Class<?>[] { APIBasicTestRunner.class });
        testGroups.put("api-advanced", new Class<?>[] { AdvancedApiTestRunner.class });
        testGroups.put("api-treasures", new Class<?>[] { TreasureApiTest.class });
        testGroups.put("api-wiremock", new Class<?>[] { APIWireMockTestRunner.class });
        testGroups.put("api-pact", new Class<?>[] { APIPactTestRunner.class });
        testGroups.put("all-ui", new Class<?>[] { 
            UIBasicTestRunner.class, 
            UIAdvancedTestRunner.class, 
            HardcoreChallengesTestRunner.class 
        });
        testGroups.put("all-api", new Class<?>[] { 
            APIBasicTestRunner.class,
            AdvancedApiTestRunner.class,
            TreasureApiTest.class,
            APIWireMockTestRunner.class,
            APIPactTestRunner.class
        });
        testGroups.put("all", new Class<?>[] { 
            UIBasicTestRunner.class,
            UIAdvancedTestRunner.class,
            HardcoreChallengesTestRunner.class,
            APIBasicTestRunner.class,
            AdvancedApiTestRunner.class,
            TreasureApiTest.class,
            APIWireMockTestRunner.class,
            APIPactTestRunner.class
        });
    }
    
    @Parameters({"testGroup"})
    @Test
    public void runTestGroup(String testGroup) {
        if (!testGroups.containsKey(testGroup)) {
            throw new IllegalArgumentException("Unknown test group: " + testGroup);
        }
        
        Class<?>[] testClasses = testGroups.get(testGroup);
        runTests(testGroup, testClasses);
    }
    
    @Step("Running test group: {testGroup}")
    private void runTests(String testGroup, Class<?>[] testClasses) {
        System.out.println("Running test group: " + testGroup);
        
        testNG.setTestClasses(testClasses);
        
        List<String> suites = new ArrayList<>();
        testNG.setTestSuites(suites);
        
        testNG.run();
        
        System.out.println("Finished running test group: " + testGroup);
    }
    
    @AfterClass
    public void tearDown() {
        testNG = null;
        testGroups = null;
    }
}