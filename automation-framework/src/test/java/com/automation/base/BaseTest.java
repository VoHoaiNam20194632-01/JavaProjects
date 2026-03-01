package com.automation.base;

import com.automation.config.ConfigFactory;
import com.automation.config.FrameworkConfig;
import com.automation.driver.DriverFactory;
import com.automation.driver.DriverManager;
import com.automation.pages.HomePage;
import com.automation.pages.LoginPage;
import com.automation.utils.CookieManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public abstract class BaseTest {

    protected static final Logger log = LogManager.getLogger(BaseTest.class);
    private static final By WELCOME_HEADING = By.xpath("//h5[normalize-space(text())='Welcome to BurgerShop']");
    protected FrameworkConfig config;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        config = ConfigFactory.getFrameworkConfig();
        log.info("Setting up WebDriver for thread: {}", Thread.currentThread().threadId());
        DriverManager.setDriver(DriverFactory.createDriver());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        log.info("Tearing down WebDriver for thread: {}", Thread.currentThread().threadId());
        DriverManager.quitDriver();
    }

    protected String getBaseUrl() {
        return config.baseUrl();
    }

    protected String getAdminEmail() {
        return config.adminEmail();
    }

    protected String getAdminPassword() {
        return config.adminPassword();
    }

    protected HomePage loginWithSessionReuse() {
        LoginPage loginPage = new LoginPage();
        String baseUrl = getBaseUrl();
        HomePage homePage;

        if (config.sessionReuse()) {
            log.info("Session reuse enabled, attempting to load session");
            boolean loaded = CookieManager.loadSession(baseUrl);

            if (loaded) {
                try {
                    WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10));
                    wait.until(ExpectedConditions.urlContains("/admin/home"));
                    wait.until(ExpectedConditions.visibilityOfElementLocated(WELCOME_HEADING));
                    log.info("Session restored successfully, URL: {}", DriverManager.getDriver().getCurrentUrl());
                    homePage = new HomePage();
                } catch (org.openqa.selenium.TimeoutException e) {
                    log.info("Session invalid (did not reach home page), performing fresh login");
                    homePage = null;
                }

                if (homePage != null) {
                    homePage.selectStore(getStoreName());
                    return homePage;
                }
            }
        }

        // Fresh login
        loginPage.open(baseUrl);
        homePage = loginPage.loginAs(getAdminEmail(), getAdminPassword());

        if (config.sessionReuse()) {
            // Wait for login to fully complete (home page loaded) before saving session
            try {
                WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(15));
                wait.until(ExpectedConditions.urlContains("/admin/home"));
                wait.until(ExpectedConditions.visibilityOfElementLocated(WELCOME_HEADING));
            } catch (Exception e) {
                log.warn("Home page did not fully load before saving session");
            }
            CookieManager.saveSession();
        }

        homePage.selectStore(getStoreName());
        return homePage;
    }

    protected String getStoreName() {
        return config.storeName();
    }
}
