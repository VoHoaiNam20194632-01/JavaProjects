package com.automation.utils;

import com.automation.config.ConfigFactory;
import com.automation.driver.DriverManager;
import com.automation.enums.WaitStrategy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;

public final class WaitUtils {

    private WaitUtils() {
    }

    public static WebElement waitForElement(By locator, WaitStrategy strategy) {
        return waitForElement(locator, strategy,
                ConfigFactory.getWaitConfig().explicitWait());
    }

    public static WebElement waitForElement(By locator, WaitStrategy strategy, int timeoutSeconds) {
        WebDriver driver = DriverManager.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

        return switch (strategy) {
            case CLICKABLE -> wait.until(ExpectedConditions.elementToBeClickable(locator));
            case VISIBLE -> wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            case PRESENCE -> wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            case NONE -> driver.findElement(locator);
        };
    }

    public static List<WebElement> waitForElements(By locator, WaitStrategy strategy) {
        WebDriver driver = DriverManager.getDriver();
        WebDriverWait wait = new WebDriverWait(driver,
                Duration.ofSeconds(ConfigFactory.getWaitConfig().explicitWait()));

        return switch (strategy) {
            case VISIBLE -> wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
            case PRESENCE -> wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
            default -> driver.findElements(locator);
        };
    }

    public static WebElement fluentWait(By locator) {
        WebDriver driver = DriverManager.getDriver();
        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(ConfigFactory.getWaitConfig().fluentWaitTimeout()))
                .pollingEvery(Duration.ofMillis(ConfigFactory.getWaitConfig().pollingInterval()))
                .ignoring(NoSuchElementException.class);

        return wait.until(d -> d.findElement(locator));
    }

    public static boolean waitForUrlContains(String urlPart, int timeoutSeconds) {
        WebDriver driver = DriverManager.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.urlContains(urlPart));
    }

    public static boolean waitForTitleContains(String titlePart, int timeoutSeconds) {
        WebDriver driver = DriverManager.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.titleContains(titlePart));
    }
}
