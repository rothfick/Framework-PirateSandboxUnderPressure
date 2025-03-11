package com.peliQAn.framework.utils;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for Canvas-related operations
 */
@Slf4j
public class CanvasUtils {

    private CanvasUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Click on Canvas at coordinates
     */
    @Step("Click on Canvas at coordinates: ({x}, {y})")
    public static void clickOnCanvas(WebDriver driver, WebElement canvas, int x, int y) {
        Actions actions = new Actions(driver);
        actions.moveToElement(canvas, x, y).click().perform();
        log.info("Clicked on Canvas at coordinates: ({}, {})", x, y);
    }

    /**
     * Drag on Canvas from point to point
     */
    @Step("Drag on Canvas from ({startX}, {startY}) to ({endX}, {endY})")
    public static void dragOnCanvas(WebDriver driver, WebElement canvas, int startX, int startY, int endX, int endY) {
        Actions actions = new Actions(driver);
        actions.moveToElement(canvas, startX, startY)
               .clickAndHold()
               .moveByOffset(endX - startX, endY - startY)
               .release()
               .perform();
        log.info("Dragged on Canvas from ({}, {}) to ({}, {})", startX, startY, endX, endY);
    }

    /**
     * Draw a line on Canvas
     */
    @Step("Draw a line on Canvas from ({startX}, {startY}) to ({endX}, {endY})")
    public static void drawLineOnCanvas(WebDriver driver, WebElement canvas, int startX, int startY, int endX, int endY) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        String script = 
            "const canvas = arguments[0];" +
            "const ctx = canvas.getContext('2d');" +
            "ctx.beginPath();" +
            "ctx.moveTo(" + startX + ", " + startY + ");" +
            "ctx.lineTo(" + endX + ", " + endY + ");" +
            "ctx.stroke();";
        
        js.executeScript(script, canvas);
        log.info("Drew a line on Canvas from ({}, {}) to ({}, {})", startX, startY, endX, endY);
    }

    /**
     * Get pixel data from Canvas at coordinates
     */
    @Step("Get pixel data from Canvas at coordinates: ({x}, {y})")
    public static Color getPixelColorAtCoordinates(WebDriver driver, WebElement canvas, int x, int y) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        String script = 
            "const canvas = arguments[0];" +
            "const ctx = canvas.getContext('2d');" +
            "const pixel = ctx.getImageData(" + x + ", " + y + ", 1, 1).data;" +
            "return [pixel[0], pixel[1], pixel[2], pixel[3]]";
        
        List<Long> rgba = (List<Long>) js.executeScript(script, canvas);
        
        if (rgba != null && rgba.size() == 4) {
            Color color = new Color(rgba.get(0).intValue(), rgba.get(1).intValue(), 
                                   rgba.get(2).intValue(), rgba.get(3).intValue());
            log.info("Got pixel color at ({}, {}): RGBA({},{},{},{})", 
                     x, y, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            return color;
        } else {
            log.warn("Failed to get pixel color at ({}, {})", x, y);
            return null;
        }
    }

    /**
     * Scan Canvas for specific color
     */
    @Step("Scan Canvas for color: {r},{g},{b}")
    public static List<Point> scanCanvasForColor(WebDriver driver, WebElement canvas, int r, int g, int b, int tolerance) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        String script = 
            "const canvas = arguments[0];" +
            "const r = arguments[1], g = arguments[2], b = arguments[3], tolerance = arguments[4];" +
            "const ctx = canvas.getContext('2d');" +
            "const width = canvas.width, height = canvas.height;" +
            "const imageData = ctx.getImageData(0, 0, width, height).data;" +
            "const points = [];" +
            
            "for (let y = 0; y < height; y += 5) {" + // Step by 5 pixels for performance
            "  for (let x = 0; x < width; x += 5) {" +
            "    const i = (y * width + x) * 4;" +
            "    if (Math.abs(imageData[i] - r) <= tolerance &&" +
            "        Math.abs(imageData[i+1] - g) <= tolerance &&" +
            "        Math.abs(imageData[i+2] - b) <= tolerance) {" +
            "      points.push({x: x, y: y});" +
            "    }" +
            "  }" +
            "}" +
            
            "return points;";
        
        List<Map<String, Long>> points = (List<Map<String, Long>>) js.executeScript(script, canvas, r, g, b, tolerance);
        
        List<Point> result = new ArrayList<>();
        if (points != null) {
            for (Map<String, Long> point : points) {
                result.add(new Point(point.get("x").intValue(), point.get("y").intValue()));
            }
            log.info("Found {} points with color RGB({},{},{}) with tolerance {}", 
                     result.size(), r, g, b, tolerance);
        }
        
        return result;
    }

    /**
     * Draw text on Canvas
     */
    @Step("Draw text on Canvas at ({x}, {y}): {text}")
    public static void drawTextOnCanvas(WebDriver driver, WebElement canvas, String text, int x, int y) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        String script = 
            "const canvas = arguments[0];" +
            "const text = arguments[1];" +
            "const x = arguments[2], y = arguments[3];" +
            "const ctx = canvas.getContext('2d');" +
            "ctx.font = '16px Arial';" +
            "ctx.fillText(text, x, y);";
        
        js.executeScript(script, canvas, text, x, y);
        log.info("Drew text on Canvas at ({}, {}): {}", x, y, text);
    }

    /**
     * Get Canvas dimensions
     */
    @Step("Get Canvas dimensions")
    public static Dimension getCanvasDimensions(WebDriver driver, WebElement canvas) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        String script = 
            "const canvas = arguments[0];" +
            "return {width: canvas.width, height: canvas.height};";
        
        Map<String, Long> dimensions = (Map<String, Long>) js.executeScript(script, canvas);
        
        if (dimensions != null) {
            Dimension dimension = new Dimension(dimensions.get("width").intValue(), 
                                              dimensions.get("height").intValue());
            log.info("Canvas dimensions: {}x{}", dimension.width, dimension.height);
            return dimension;
        } else {
            log.warn("Failed to get Canvas dimensions");
            return null;
        }
    }

    /**
     * Clear Canvas
     */
    @Step("Clear Canvas")
    public static void clearCanvas(WebDriver driver, WebElement canvas) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        String script = 
            "const canvas = arguments[0];" +
            "const ctx = canvas.getContext('2d');" +
            "ctx.clearRect(0, 0, canvas.width, canvas.height);";
        
        js.executeScript(script, canvas);
        log.info("Cleared Canvas");
    }
}