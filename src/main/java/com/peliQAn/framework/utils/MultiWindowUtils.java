package com.peliQAn.framework.utils;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Utility class for managing multiple windows
 */
@Slf4j
public class MultiWindowUtils {

    private MultiWindowUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Get all window handles with titles
     */
    @Step("Get all window handles with titles")
    public static Map<String, String> getAllWindowHandlesWithTitles(WebDriver driver) {
        String currentHandle = driver.getWindowHandle();
        Set<String> handles = driver.getWindowHandles();
        Map<String, String> handleTitleMap = new HashMap<>();
        
        for (String handle : handles) {
            driver.switchTo().window(handle);
            handleTitleMap.put(handle, driver.getTitle());
        }
        
        // Switch back to original window
        driver.switchTo().window(currentHandle);
        
        log.info("Found {} windows: {}", handleTitleMap.size(), handleTitleMap);
        return handleTitleMap;
    }

    /**
     * Find window handle by title
     */
    @Step("Find window handle by title: {title}")
    public static String findWindowHandleByTitle(WebDriver driver, String title) {
        String currentHandle = driver.getWindowHandle();
        Set<String> handles = driver.getWindowHandles();
        String targetHandle = null;
        
        for (String handle : handles) {
            driver.switchTo().window(handle);
            if (driver.getTitle().contains(title)) {
                targetHandle = handle;
                break;
            }
        }
        
        // Switch back to original window
        driver.switchTo().window(currentHandle);
        
        if (targetHandle != null) {
            log.info("Found window with title '{}', handle: {}", title, targetHandle);
        } else {
            log.warn("No window found with title: {}", title);
        }
        
        return targetHandle;
    }

    /**
     * Find window handle by URL
     */
    @Step("Find window handle by URL: {urlPart}")
    public static String findWindowHandleByUrl(WebDriver driver, String urlPart) {
        String currentHandle = driver.getWindowHandle();
        Set<String> handles = driver.getWindowHandles();
        String targetHandle = null;
        
        for (String handle : handles) {
            driver.switchTo().window(handle);
            if (driver.getCurrentUrl().contains(urlPart)) {
                targetHandle = handle;
                break;
            }
        }
        
        // Switch back to original window
        driver.switchTo().window(currentHandle);
        
        if (targetHandle != null) {
            log.info("Found window with URL containing '{}', handle: {}", urlPart, targetHandle);
        } else {
            log.warn("No window found with URL containing: {}", urlPart);
        }
        
        return targetHandle;
    }

    /**
     * Execute function in window
     */
    @Step("Execute function in window with handle: {windowHandle}")
    public static <T> T executeInWindow(WebDriver driver, String windowHandle, Function<WebDriver, T> function) {
        String currentHandle = driver.getWindowHandle();
        T result = null;
        
        try {
            driver.switchTo().window(windowHandle);
            result = function.apply(driver);
        } finally {
            if (windowExists(driver, currentHandle)) {
                driver.switchTo().window(currentHandle);
            }
        }
        
        return result;
    }

    /**
     * Check if window exists
     */
    @Step("Check if window exists: {windowHandle}")
    public static boolean windowExists(WebDriver driver, String windowHandle) {
        return driver.getWindowHandles().contains(windowHandle);
    }

    /**
     * Close all windows except current
     */
    @Step("Close all windows except current")
    public static void closeAllWindowsExceptCurrent(WebDriver driver) {
        String currentHandle = driver.getWindowHandle();
        Set<String> handles = driver.getWindowHandles();
        
        for (String handle : handles) {
            if (!handle.equals(currentHandle)) {
                driver.switchTo().window(handle);
                driver.close();
                log.info("Closed window with handle: {}", handle);
            }
        }
        
        driver.switchTo().window(currentHandle);
        log.info("Closed all windows except current window");
    }

    /**
     * Close all windows except specified
     */
    @Step("Close all windows except: {windowHandle}")
    public static void closeAllWindowsExcept(WebDriver driver, String windowHandle) {
        Set<String> handles = driver.getWindowHandles();
        
        for (String handle : handles) {
            if (!handle.equals(windowHandle)) {
                driver.switchTo().window(handle);
                driver.close();
                log.info("Closed window with handle: {}", handle);
            }
        }
        
        driver.switchTo().window(windowHandle);
        log.info("Closed all windows except window with handle: {}", windowHandle);
    }

    /**
     * Open new window with JavaScript
     */
    @Step("Open new window with URL: {url}")
    public static String openNewWindow(WebDriver driver, String url) {
        String currentHandle = driver.getWindowHandle();
        Set<String> handlesBefore = driver.getWindowHandles();
        
        ((JavascriptExecutor) driver).executeScript("window.open('" + url + "', '_blank');");
        
        // Wait for new window to open
        Set<String> handlesAfter = waitForNewWindow(driver, handlesBefore);
        
        // Find new window handle
        handlesAfter.removeAll(handlesBefore);
        String newHandle = handlesAfter.iterator().next();
        
        log.info("Opened new window with URL: {}, handle: {}", url, newHandle);
        
        return newHandle;
    }

    /**
     * Wait for new window to open
     */
    private static Set<String> waitForNewWindow(WebDriver driver, Set<String> handlesBefore) {
        Set<String> handlesAfter = driver.getWindowHandles();
        int attempts = 0;
        
        while (handlesAfter.size() <= handlesBefore.size() && attempts < 20) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            handlesAfter = driver.getWindowHandles();
            attempts++;
        }
        
        return handlesAfter;
    }

    /**
     * Transfer data between windows
     */
    @Step("Transfer data from current window to: {targetWindowHandle}")
    public static void transferDataBetweenWindows(WebDriver driver, String targetWindowHandle, String key, String value) {
        // Store data in localStorage
        ((JavascriptExecutor) driver).executeScript(
            "localStorage.setItem('" + key + "', '" + value + "');"
        );
        
        // Switch to target window
        driver.switchTo().window(targetWindowHandle);
        
        // Retrieve data
        ((JavascriptExecutor) driver).executeScript(
            "localStorage.setItem('" + key + "', '" + value + "');"
        );
        
        log.info("Transferred data from current window to window {}: {}={}", targetWindowHandle, key, value);
    }

    /**
     * Get all open window handles
     */
    @Step("Get all open window handles")
    public static List<String> getAllOpenWindowHandles(WebDriver driver) {
        return new ArrayList<>(driver.getWindowHandles());
    }

    /**
     * Check if element exists in window
     */
    @Step("Check if element exists in window: {windowHandle}")
    public static boolean elementExistsInWindow(WebDriver driver, String windowHandle, WebElement element) {
        return executeInWindow(driver, windowHandle, d -> {
            try {
                return element.isDisplayed();
            } catch (Exception e) {
                return false;
            }
        });
    }
}