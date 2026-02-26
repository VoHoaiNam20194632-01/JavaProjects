package com.automation.ui;

import com.automation.annotations.FrameworkAnnotation;
import com.automation.base.BaseTest;
import com.automation.enums.CategoryType;
import com.automation.pages.HomePage;
import com.automation.pages.LoginPage;
import io.qameta.allure.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("UI Tests")
@Feature("Login")
public class LoginTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        loginPage = new LoginPage();
        loginPage.open(getBaseUrl());
    }

    @Test(description = "Valid login with correct credentials")
    @FrameworkAnnotation(category = {CategoryType.SMOKE, CategoryType.REGRESSION},
            author = "Framework", description = "Verify successful login")
    @Severity(SeverityLevel.BLOCKER)
    @Story("User can login with valid credentials")
    public void testValidLogin() {
        HomePage homePage = loginPage.loginAs(getAdminEmail(), getAdminPassword());

        assertThat(homePage.isHomePageDisplayed())
                .as("Home page should be displayed after login")
                .isTrue();
    }

    @Test(description = "Login page is displayed correctly")
    @FrameworkAnnotation(category = {CategoryType.SMOKE},
            author = "Framework", description = "Verify login page loads")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Login page loads correctly")
    public void testLoginPageDisplayed() {
        assertThat(loginPage.isLoginPageDisplayed())
                .as("Login page should display email input")
                .isTrue();
    }
}
