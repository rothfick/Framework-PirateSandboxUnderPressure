package com.peliQAn.framework.utils;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for time-related operations
 */
@Slf4j
public class TimeUtils {

    private TimeUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Set browser date and time
     */
    @Step("Set browser date and time to: {dateTime}")
    public static void setBrowserDateTime(WebDriver driver, LocalDateTime dateTime) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        // Store original Date constructor
        js.executeScript(
            "if (!window.originalDate) {" +
            "  window.originalDate = Date;" +
            "}"
        );
        
        // Calculate milliseconds since epoch
        long timestamp = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        
        // Override JavaScript Date
        String script = 
            "const timestamp = " + timestamp + ";" +
            "const customDate = new Date(timestamp);" +
            "Date = class extends window.originalDate {" +
            "  constructor() {" +
            "    super();" +
            "    return customDate;" +
            "  }" +
            "  static now() {" +
            "    return timestamp;" +
            "  }" +
            "};";
        
        js.executeScript(script);
        log.info("Set browser date and time to: {}", dateTime);
    }

    /**
     * Set browser date and time from ISO string
     */
    @Step("Set browser date and time to: {isoDateTimeString}")
    public static void setBrowserDateTime(WebDriver driver, String isoDateTimeString) {
        LocalDateTime dateTime = LocalDateTime.parse(isoDateTimeString, DateTimeFormatter.ISO_DATE_TIME);
        setBrowserDateTime(driver, dateTime);
    }

    /**
     * Set browser timezone offset
     */
    @Step("Set browser timezone offset to: {offsetMinutes} minutes")
    public static void setBrowserTimezoneOffset(WebDriver driver, int offsetMinutes) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        // Store original method
        js.executeScript(
            "if (!window.originalGetTimezoneOffset) {" +
            "  window.originalGetTimezoneOffset = Date.prototype.getTimezoneOffset;" +
            "}" +
            "Date.prototype.getTimezoneOffset = function() { return " + (-offsetMinutes) + "; };"
        );
        
        log.info("Set browser timezone offset to: {} minutes", offsetMinutes);
    }

    /**
     * Reset browser date and time
     */
    @Step("Reset browser date and time")
    public static void resetBrowserDateTime(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        js.executeScript(
            "if (window.originalDate) {" +
            "  Date = window.originalDate;" +
            "}"
        );
        
        log.info("Reset browser date and time");
    }

    /**
     * Reset browser timezone offset
     */
    @Step("Reset browser timezone offset")
    public static void resetBrowserTimezoneOffset(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        js.executeScript(
            "if (window.originalGetTimezoneOffset) {" +
            "  Date.prototype.getTimezoneOffset = window.originalGetTimezoneOffset;" +
            "}"
        );
        
        log.info("Reset browser timezone offset");
    }

    /**
     * Reset all time-related modifications
     */
    @Step("Reset all time-related modifications")
    public static void resetAllTimeModifications(WebDriver driver) {
        resetBrowserDateTime(driver);
        resetBrowserTimezoneOffset(driver);
        log.info("Reset all time-related modifications");
    }

    /**
     * Pause execution for specified milliseconds
     */
    @Step("Pause execution for {milliseconds} ms")
    public static void pause(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
            log.debug("Paused execution for {} ms", milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Pause interrupted", e);
        }
    }
}