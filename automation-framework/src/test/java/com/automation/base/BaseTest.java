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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public abstract class BaseTest {

    protected static final Logger log = LogManager.getLogger(BaseTest.class);
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

        if (config.sessionReuse()) {
            log.info("Session reuse enabled, attempting to load session");
            boolean loaded = CookieManager.loadSession(baseUrl);

            if (loaded) {
                // Wait for the server to validate session and redirect away from login
                try {
                    new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(5))
                            .until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));
                    log.info("Session restored successfully");
                    return new HomePage();
                } catch (Exception e) {
                    log.info("Session expired, performing fresh login");
                }
            }
        }

        // Fresh login
        loginPage.open(baseUrl);
        HomePage homePage = loginPage.loginAs(getAdminEmail(), getAdminPassword());

        if (config.sessionReuse()) {
            // Wait for login to fully complete before saving session
            try {
                new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10))
                        .until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));
            } catch (Exception e) {
                log.warn("Login redirect did not complete within timeout");
            }
            CookieManager.saveSession();
        }

        return homePage;
    }
}
