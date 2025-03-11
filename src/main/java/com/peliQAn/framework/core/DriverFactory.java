package com.peliQAn.framework.core;

import com.peliQAn.framework.config.PropertyManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;

import java.time.Duration;

/**
 * Factory class to create WebDriver instances
 */
@Slf4j
public class DriverFactory {
    private static final PropertyManager propertyManager = PropertyManager.getInstance();
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverFactory() {
        // Private constructor to prevent instantiation
    }

    /**
     * Initializes a WebDriver instance based on configuration
     */
    public static WebDriver initDriver() {
        String browser = propertyManager.getProperty("browser", "chrome").toLowerCase();
        boolean headless = propertyManager.getBooleanProperty("headless", false);
        
        WebDriver driver;
        
        log.info("Initializing {} browser (headless: {})", browser, headless);
        
        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) {
                    chromeOptions.addArguments("--headless=new");
                }
                chromeOptions.addArguments("--start-maximized");
                chromeOptions.addArguments("--remote-allow-origins=*");
                // For Shadow DOM and advanced interactions
                chromeOptions.addArguments("--disable-web-security");
                chromeOptions.addArguments("--disable-site-isolation-trials");
                driver = new ChromeDriver(chromeOptions);
                break;
                
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) {
                    firefoxOptions.addArguments("--headless");
                }
                driver = new FirefoxDriver(firefoxOptions);
                break;
                
            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (headless) {
                    edgeOptions.addArguments("--headless");
                }
                driver = new EdgeDriver(edgeOptions);
                break;
                
            case "safari":
                driver = new SafariDriver();
                break;
                
            default:
                log.warn("Unknown browser '{}', defaulting to Chrome", browser);
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
        }
        
        // Configure timeouts
        int implicitWait = propertyManager.getIntProperty("implicitWait", 10);
        int pageLoadTimeout = propertyManager.getIntProperty("pageLoadTimeout", 30);
        int scriptTimeout = propertyManager.getIntProperty("scriptTimeout", 30);
        
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(scriptTimeout));
        
        if (!headless) {
            driver.manage().window().maximize();
        }
        
        driverThreadLocal.set(driver);
        log.info("WebDriver initialized successfully");
        
        return driver;
    }

    /**
     * Gets the current WebDriver instance
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            driver = initDriver();
            driverThreadLocal.set(driver);
        }
        return driver;
    }

    /**
     * Quits the current WebDriver instance
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            log.info("Quitting WebDriver instance");
            driver.quit();
            driverThreadLocal.remove();
        }
    }
}