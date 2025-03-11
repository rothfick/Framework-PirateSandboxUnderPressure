package com.peliQAn.framework.utils;

import io.qameta.allure.Attachment;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for taking and handling screenshots
 */
@Slf4j
public class ScreenshotUtils {
    private static final String SCREENSHOT_DIR = "target/screenshots/";

    // Private constructor to prevent instantiation
    private ScreenshotUtils() {
    }

    /**
     * Take a screenshot and save it to the specified directory
     */
    public static String takeScreenshot(WebDriver driver, String screenshotName) {
        if (driver == null) {
            log.error("Driver is null. Cannot take screenshot.");
            return null;
        }

        if (!(driver instanceof TakesScreenshot)) {
            log.error("Driver doesn't support taking screenshots");
            return null;
        }

        try {
            createScreenshotDir();
            
            // Take screenshot
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            
            // Generate unique filename
            String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
            String filename = screenshotName + "_" + timestamp + ".png";
            
            // Save screenshot to file
            Path destination = Paths.get(SCREENSHOT_DIR, filename);
            Files.copy(scrFile.toPath(), destination);
            
            // Attach to Allure report
            attachScreenshotToAllure(scrFile);
            
            log.info("Screenshot saved: {}", destination);
            return destination.toString();
        } catch (IOException e) {
            log.error("Failed to take screenshot", e);
            return null;
        }
    }

    /**
     * Create screenshots directory if it doesn't exist
     */
    private static void createScreenshotDir() throws IOException {
        Path screenshotDir = Paths.get(SCREENSHOT_DIR);
        if (!Files.exists(screenshotDir)) {
            Files.createDirectories(screenshotDir);
        }
    }

    /**
     * Attach screenshot to Allure report
     */
    @Attachment(value = "Screenshot", type = "image/png")
    private static byte[] attachScreenshotToAllure(File screenshot) {
        try {
            return Files.readAllBytes(screenshot.toPath());
        } catch (IOException e) {
            log.error("Failed to attach screenshot to Allure report", e);
            return new byte[0];
        }
    }
}