package com.automation.driver;

import com.automation.config.ConfigFactory;
import com.automation.config.FrameworkConfig;
import com.automation.exceptions.DriverInitializationException;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

public final class DriverFactory {

    private static final Logger log = LogManager.getLogger(DriverFactory.class);

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        FrameworkConfig config = ConfigFactory.getFrameworkConfig();
        BrowserType browserType = BrowserType.fromString(config.browser());
        boolean headless = config.headless();

        log.info("Creating {} driver (headless: {})", browserType, headless);

        WebDriver driver = switch (browserType) {
            case CHROME -> createChromeDriver(headless);
            case FIREFOX -> createFirefoxDriver(headless);
            case EDGE -> createEdgeDriver(headless);
        };

        configureDriver(driver, config);
        return driver;
    }

    private static WebDriver createChromeDriver(boolean headless) {
        try {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            if (headless) {
                options.addArguments("--headless=new");
            }
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            return new ChromeDriver(options);
        } catch (Exception e) {
            throw new DriverInitializationException("Failed to create Chrome driver", e);
        }
    }

    private static WebDriver createFirefoxDriver(boolean headless) {
        try {
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions options = new FirefoxOptions();
            if (headless) {
                options.addArguments("--headless");
            }
            return new FirefoxDriver(options);
        } catch (Exception e) {
            throw new DriverInitializationException("Failed to create Firefox driver", e);
        }
    }

    private static WebDriver createEdgeDriver(boolean headless) {
        try {
            WebDriverManager.edgedriver().setup();
            EdgeOptions options = new EdgeOptions();
            if (headless) {
                options.addArguments("--headless=new");
            }
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            return new EdgeDriver(options);
        } catch (Exception e) {
            throw new DriverInitializationException("Failed to create Edge driver", e);
        }
    }

    private static void configureDriver(WebDriver driver, FrameworkConfig config) {
        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(ConfigFactory.getWaitConfig().implicitWait()));
        driver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(ConfigFactory.getWaitConfig().pageLoadTimeout()));

        if (config.maximize()) {
            driver.manage().window().maximize();
        } else {
            driver.manage().window().setSize(
                    new Dimension(config.browserWidth(), config.browserHeight()));
        }
    }
}
