package com.peliQAn.framework.utils;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v121.network.Network;
import org.openqa.selenium.devtools.v121.network.model.ConnectionType;

import java.util.Optional;

/**
 * Utility class for network-related operations
 */
@Slf4j
public class NetworkUtils {

    private NetworkUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Throttle network for Chrome browser
     * Note: This requires Chrome DevTools Protocol (CDP) support
     */
    @Step("Throttle network with download: {downloadKbps} Kbps, upload: {uploadKbps} Kbps, latency: {latencyMs} ms")
    public static void throttleNetwork(WebDriver driver, int downloadKbps, int uploadKbps, int latencyMs) {
        if (!(driver instanceof ChromeDriver)) {
            log.warn("Network throttling is only supported for Chrome browser");
            return;
        }

        try {
            ChromeDriver chromeDriver = (ChromeDriver) driver;
            DevTools devTools = chromeDriver.getDevTools();
            devTools.createSession();
            devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
            
            devTools.send(Network.emulateNetworkConditions(
                    false,                           // offline
                    latencyMs,                       // latency (ms)
                    downloadKbps * 1024 / 8,         // download throughput (bytes/s)
                    uploadKbps * 1024 / 8,           // upload throughput (bytes/s)
                    Optional.of(ConnectionType.CELLULAR3G) // connection type
            ));
            
            log.info("Network throttled with download: {} Kbps, upload: {} Kbps, latency: {} ms", 
                    downloadKbps, uploadKbps, latencyMs);
        } catch (Exception e) {
            log.error("Failed to throttle network: {}", e.getMessage());
        }
    }

    /**
     * Reset network throttling
     */
    @Step("Reset network throttling")
    public static void resetNetworkThrottling(WebDriver driver) {
        if (!(driver instanceof ChromeDriver)) {
            log.warn("Network throttling is only supported for Chrome browser");
            return;
        }

        try {
            ChromeDriver chromeDriver = (ChromeDriver) driver;
            DevTools devTools = chromeDriver.getDevTools();
            devTools.createSession();
            devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
            
            devTools.send(Network.emulateNetworkConditions(
                    false,      // offline
                    0,          // latency (ms)
                    -1,         // download throughput (bytes/s)
                    -1,         // upload throughput (bytes/s)
                    Optional.of(ConnectionType.NONE) // connection type
            ));
            
            log.info("Network throttling reset");
        } catch (Exception e) {
            log.error("Failed to reset network throttling: {}", e.getMessage());
        }
    }

    /**
     * Simulate network error for specific URL (using JavaScript)
     */
    @Step("Simulate network error for URL: {url}")
    public static void simulateNetworkError(WebDriver driver, String url) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        String script = 
            "const originalFetch = window.fetch;" +
            "window.fetch = function(resource, init) {" +
            "  if (resource.includes('" + url + "')) {" +
            "    return Promise.reject(new Error('Simulated network error'));" +
            "  }" +
            "  return originalFetch(resource, init);" +
            "};";
        
        js.executeScript(script);
        log.info("Simulated network error for URL: {}", url);
    }

    /**
     * Simulate random network errors (random failure rate)
     */
    @Step("Simulate random network errors with failure rate: {failureRatePercent}%")
    public static void simulateRandomNetworkErrors(WebDriver driver, int failureRatePercent) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        String script = 
            "const originalFetch = window.fetch;" +
            "window.fetch = function(resource, init) {" +
            "  if (Math.random() * 100 < " + failureRatePercent + ") {" +
            "    return Promise.reject(new Error('Random network error'));" +
            "  }" +
            "  return originalFetch(resource, init);" +
            "};";
        
        js.executeScript(script);
        log.info("Simulated random network errors with failure rate: {}%", failureRatePercent);
    }

    /**
     * Reset network error simulation
     */
    @Step("Reset network error simulation")
    public static void resetNetworkErrorSimulation(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        String script = 
            "if (window.originalFetch) {" +
            "  window.fetch = window.originalFetch;" +
            "}";
        
        js.executeScript(script);
        log.info("Reset network error simulation");
    }
}