package com.peliQAn.framework.config;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton class to manage properties from configuration files
 */
@Slf4j
public class PropertyManager {
    private static final String CONFIG_FILE = "src/test/resources/config/config.properties";
    private static PropertyManager instance;
    private final Properties properties;

    private PropertyManager() {
        properties = new Properties();
        loadProperties();
    }

    /**
     * Get the singleton instance of PropertyManager
     */
    public static synchronized PropertyManager getInstance() {
        if (instance == null) {
            instance = new PropertyManager();
        }
        return instance;
    }

    /**
     * Load properties from configuration file
     */
    private void loadProperties() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            log.info("Configuration loaded successfully from {}", CONFIG_FILE);
        } catch (IOException e) {
            log.error("Error loading properties file: {}", CONFIG_FILE, e);
            throw new RuntimeException("Failed to load configuration", e);
        }
        
        // Override properties with system properties
        properties.forEach((key, value) -> {
            String systemValue = System.getProperty(key.toString());
            if (systemValue != null) {
                properties.setProperty(key.toString(), systemValue);
                log.debug("Property '{}' overridden by system property: {}", key, systemValue);
            }
        });
    }

    /**
     * Get a property value
     * 
     * @param key Property key
     * @return Property value or null if not found
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Get a property value with default
     * 
     * @param key Property key
     * @param defaultValue Default value if property is not found
     * @return Property value or default if not found
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Get an integer property value
     * 
     * @param key Property key
     * @param defaultValue Default value if property is not found or invalid
     * @return Integer property value or default if not found or invalid
     */
    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        try {
            return (value != null) ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            log.warn("Invalid integer property '{}': {}, using default: {}", key, value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Get a boolean property value
     * 
     * @param key Property key
     * @param defaultValue Default value if property is not found
     * @return Boolean property value or default if not found
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return (value != null) ? Boolean.parseBoolean(value) : defaultValue;
    }

    /**
     * Set a property value
     * 
     * @param key Property key
     * @param value Property value
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
        log.debug("Property '{}' set to '{}'", key, value);
    }
}