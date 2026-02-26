package com.automation.pages;

import com.automation.enums.WaitStrategy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class HomePage extends BasePage {

    private final By sidebarMenu = By.cssSelector("bg-root .sidebar, [class*='sidebar'], [class*='menu']");
    private final By userProfile = By.cssSelector("[class*='profile'], [class*='avatar'], [class*='user']");
    private final By dashboardHeader = By.cssSelector("[class*='dashboard'], [class*='header'] h1, [class*='header'] h2");

    @Step("Check if home page is displayed after login")
    public boolean isHomePageDisplayed() {
        return isDisplayed(sidebarMenu) || isDisplayed(dashboardHeader);
    }

    @Step("Get dashboard header text")
    public String getDashboardHeaderText() {
        return getText(dashboardHeader, WaitStrategy.VISIBLE);
    }

    public String getCurrentPageUrl() {
        return getCurrentUrl();
    }

    public String getPageTitle() {
        return super.getPageTitle();
    }
}
