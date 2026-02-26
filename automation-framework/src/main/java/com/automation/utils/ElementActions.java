package com.automation.utils;

import com.automation.driver.DriverManager;
import com.automation.enums.WaitStrategy;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public final class ElementActions {

    private static final Logger log = LogManager.getLogger(ElementActions.class);

    private ElementActions() {
    }

    @Step("Click on element: {locator}")
    public static void click(By locator, WaitStrategy strategy) {
        WebElement element = WaitUtils.waitForElement(locator, strategy);
        log.info("Clicking on element: {}", locator);
        element.click();
    }

    @Step("Type '{text}' into element: {locator}")
    public static void type(By locator, String text, WaitStrategy strategy) {
        WebElement element = WaitUtils.waitForElement(locator, strategy);
        log.info("Typing '{}' into element: {}", text, locator);
        element.clear();
        element.sendKeys(text);
    }

    @Step("Get text from element: {locator}")
    public static String getText(By locator, WaitStrategy strategy) {
        WebElement element = WaitUtils.waitForElement(locator, strategy);
        String text = element.getText();
        log.info("Got text '{}' from element: {}", text, locator);
        return text;
    }

    @Step("Get attribute '{attribute}' from element: {locator}")
    public static String getAttribute(By locator, String attribute, WaitStrategy strategy) {
        WebElement element = WaitUtils.waitForElement(locator, strategy);
        return element.getAttribute(attribute);
    }

    public static boolean isDisplayed(By locator) {
        try {
            return DriverManager.getDriver().findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isEnabled(By locator) {
        try {
            return DriverManager.getDriver().findElement(locator).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Select dropdown by visible text: {text}")
    public static void selectByVisibleText(By locator, String text, WaitStrategy strategy) {
        WebElement element = WaitUtils.waitForElement(locator, strategy);
        Select select = new Select(element);
        select.selectByVisibleText(text);
    }

    @Step("Select dropdown by value: {value}")
    public static void selectByValue(By locator, String value, WaitStrategy strategy) {
        WebElement element = WaitUtils.waitForElement(locator, strategy);
        Select select = new Select(element);
        select.selectByValue(value);
    }

    public static List<WebElement> getElements(By locator, WaitStrategy strategy) {
        return WaitUtils.waitForElements(locator, strategy);
    }

    @Step("Hover over element: {locator}")
    public static void hover(By locator, WaitStrategy strategy) {
        WebDriver driver = DriverManager.getDriver();
        WebElement element = WaitUtils.waitForElement(locator, strategy);
        new Actions(driver).moveToElement(element).perform();
    }

    @Step("Scroll to element: {locator}")
    public static void scrollToElement(By locator, WaitStrategy strategy) {
        WebElement element = WaitUtils.waitForElement(locator, strategy);
        ((JavascriptExecutor) DriverManager.getDriver())
                .executeScript("arguments[0].scrollIntoView(true);", element);
    }

    @Step("JavaScript click on element: {locator}")
    public static void jsClick(By locator, WaitStrategy strategy) {
        WebElement element = WaitUtils.waitForElement(locator, strategy);
        ((JavascriptExecutor) DriverManager.getDriver())
                .executeScript("arguments[0].click();", element);
    }
}
