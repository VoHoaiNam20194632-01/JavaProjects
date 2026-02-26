package com.automation.pages.components;

import com.automation.enums.WaitStrategy;
import com.automation.pages.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class NavigationBar extends BasePage {

    private final By navContainer = By.tagName("nav");
    private final By homeLink = By.cssSelector("a[href='/']");

    @Step("Check if navigation bar is displayed")
    public boolean isNavigationBarDisplayed() {
        return isDisplayed(navContainer);
    }

    @Step("Click home link")
    public void clickHome() {
        click(homeLink, WaitStrategy.CLICKABLE);
    }

    @Step("Get current page URL from navigation")
    public String getCurrentPageUrl() {
        return getCurrentUrl();
    }
}
