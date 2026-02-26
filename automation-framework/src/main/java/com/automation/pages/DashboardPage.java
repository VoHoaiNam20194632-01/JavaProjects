package com.automation.pages;

import com.automation.enums.WaitStrategy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class DashboardPage extends BasePage {

    private final By pageHeader = By.tagName("h2");
    private final By contentArea = By.id("content");

    @Step("Check if dashboard is displayed")
    public boolean isDashboardDisplayed() {
        return isDisplayed(contentArea);
    }

    @Step("Get dashboard header text")
    public String getDashboardHeader() {
        return getText(pageHeader, WaitStrategy.VISIBLE);
    }

    @Step("Get current URL")
    public String getDashboardUrl() {
        return getCurrentUrl();
    }

    @Step("Get page title")
    public String getDashboardTitle() {
        return getPageTitle();
    }
}
