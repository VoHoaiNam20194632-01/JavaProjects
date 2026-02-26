package com.automation.pages;

import com.automation.driver.DriverManager;
import com.automation.enums.WaitStrategy;
import com.automation.utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public abstract class BasePage {

    protected final Logger log = LogManager.getLogger(getClass());

    protected WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    protected WebElement waitAndFind(By locator, WaitStrategy strategy) {
        return WaitUtils.waitForElement(locator, strategy);
    }

    protected List<WebElement> waitAndFindAll(By locator, WaitStrategy strategy) {
        return WaitUtils.waitForElements(locator, strategy);
    }

    protected void click(By locator, WaitStrategy strategy) {
        log.info("Clicking on: {}", locator);
        waitAndFind(locator, strategy).click();
    }

    protected void click(By locator) {
        click(locator, WaitStrategy.CLICKABLE);
    }

    protected void type(By locator, String text, WaitStrategy strategy) {
        log.info("Typing '{}' into: {}", text, locator);
        WebElement element = waitAndFind(locator, strategy);
        element.clear();
        element.sendKeys(text);
    }

    protected void type(By locator, String text) {
        type(locator, text, WaitStrategy.VISIBLE);
    }

    protected String getText(By locator, WaitStrategy strategy) {
        return waitAndFind(locator, strategy).getText();
    }

    protected String getText(By locator) {
        return getText(locator, WaitStrategy.VISIBLE);
    }

    protected boolean isDisplayed(By locator) {
        try {
            return getDriver().findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected String getPageTitle() {
        return getDriver().getTitle();
    }

    protected String getCurrentUrl() {
        return getDriver().getCurrentUrl();
    }

    protected void navigateTo(String url) {
        log.info("Navigating to: {}", url);
        getDriver().get(url);
    }
}
