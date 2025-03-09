package com.peliQAn.framework.runners;

import com.peliQAn.framework.api.advanced.wiremock.WireMockTest;
import org.testng.annotations.Test;

/**
 * Test runner for WireMock API mocking tests
 */
public class APIWireMockTestRunner {
    
    @Test(description = "Run WireMock basic stubbing test", groups = {"api", "wiremock"})
    public void testBasicGetStubbing() {
        WireMockTest wireMockTest = new WireMockTest();
        wireMockTest.setup();
        try {
            wireMockTest.testBasicGetStubbing();
        } finally {
            wireMockTest.tearDown();
        }
    }
    
    @Test(description = "Run WireMock request body matching test", groups = {"api", "wiremock"})
    public void testPostRequestWithBodyMatching() {
        WireMockTest wireMockTest = new WireMockTest();
        wireMockTest.setup();
        try {
            wireMockTest.testPostRequestWithBodyMatching();
        } finally {
            wireMockTest.tearDown();
        }
    }
    
    @Test(description = "Run WireMock HTTP status simulation test", groups = {"api", "wiremock"})
    public void testDifferentHttpStatusCodes() {
        WireMockTest wireMockTest = new WireMockTest();
        wireMockTest.setup();
        try {
            wireMockTest.testDifferentHttpStatusCodes();
        } finally {
            wireMockTest.tearDown();
        }
    }
    
    @Test(description = "Run WireMock network delay simulation test", groups = {"api", "wiremock"})
    public void testNetworkDelays() {
        WireMockTest wireMockTest = new WireMockTest();
        wireMockTest.setup();
        try {
            wireMockTest.testNetworkDelays();
        } finally {
            wireMockTest.tearDown();
        }
    }
    
    @Test(description = "Run WireMock stateful behavior test", groups = {"api", "wiremock"})
    public void testStatefulBehavior() {
        WireMockTest wireMockTest = new WireMockTest();
        wireMockTest.setup();
        try {
            wireMockTest.testStatefulBehavior();
        } finally {
            wireMockTest.tearDown();
        }
    }
    
    @Test(description = "Run WireMock response templating test", groups = {"api", "wiremock"})
    public void testResponseTemplating() {
        WireMockTest wireMockTest = new WireMockTest();
        wireMockTest.setup();
        try {
            wireMockTest.testResponseTemplating();
        } finally {
            wireMockTest.tearDown();
        }
    }
    
    @Test(description = "Run WireMock advanced request matching test", groups = {"api", "wiremock"})
    public void testAdvancedRequestMatching() {
        WireMockTest wireMockTest = new WireMockTest();
        wireMockTest.setup();
        try {
            wireMockTest.testAdvancedRequestMatching();
        } finally {
            wireMockTest.tearDown();
        }
    }
    
    @Test(description = "Run WireMock fault simulation test", groups = {"api", "wiremock"})
    public void testFaultSimulation() {
        WireMockTest wireMockTest = new WireMockTest();
        wireMockTest.setup();
        try {
            wireMockTest.testFaultSimulation();
        } finally {
            wireMockTest.tearDown();
        }
    }
}