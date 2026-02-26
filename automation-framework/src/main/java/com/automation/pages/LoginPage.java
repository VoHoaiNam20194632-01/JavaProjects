package com.automation.pages;

import com.automation.enums.WaitStrategy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class LoginPage extends BasePage {

    // Step 1: Email input
    private final By emailInput = By.cssSelector("input[type='email'], input[formcontrolname='email'], input[placeholder*='email' i]");
    private final By loginButton = By.xpath("//button[normalize-space()='Login' or normalize-space()='Log in' or normalize-space()='LOG IN']");

    // Step 2: Password input
    private final By passwordInput = By.cssSelector("input[type='password'], input[formcontrolname='password'], input[placeholder*='password' i]");
    private final By continueButton = By.xpath("//button[normalize-space()='Continue' or normalize-space()='CONTINUE']");

    @Step("Navigate to login page")
    public LoginPage open(String url) {
        navigateTo(url);
        return this;
    }

    @Step("Enter email: {email}")
    public LoginPage enterEmail(String email) {
        type(emailInput, email, WaitStrategy.VISIBLE);
        return this;
    }

    @Step("Click Login button")
    public LoginPage clickLogin() {
        click(loginButton, WaitStrategy.CLICKABLE);
        return this;
    }

    @Step("Enter password")
    public LoginPage enterPassword(String password) {
        type(passwordInput, password, WaitStrategy.VISIBLE);
        return this;
    }

    @Step("Click Continue button")
    public HomePage clickContinue() {
        click(continueButton, WaitStrategy.CLICKABLE);
        return new HomePage();
    }

    @Step("Login with email: {email}")
    public HomePage loginAs(String email, String password) {
        return enterEmail(email)
                .clickLogin()
                .enterPassword(password)
                .clickContinue();
    }

    public boolean isLoginPageDisplayed() {
        return isDisplayed(emailInput);
    }

    public String getCurrentPageUrl() {
        return getCurrentUrl();
    }
}
