package com.automation.pages;

import com.automation.enums.WaitStrategy;
import com.automation.utils.WaitUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class HomePage extends BasePage {

    private final By sidebarMenu = By.cssSelector("bg-root .sidebar, [class*='sidebar'], [class*='menu']");
    private final By userProfile = By.cssSelector("[class*='profile'], [class*='avatar'], [class*='user']");
    private final By dashboardHeader = By.cssSelector("[class*='dashboard'], [class*='header'] h1, [class*='header'] h2");
    private final By storeMenuButton = By.cssSelector("bgs-menu-store > div");
    private final By storeCurrentName = By.xpath("//bgs-menu-store//span");

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

    @Step("Select store: {storeName}")
    public HomePage selectStore(String storeName) {
        if (storeName == null || storeName.isEmpty()) {
            log.info("No store name configured, skipping store selection");
            return this;
        }

        if (isStoreAlreadySelected(storeName)) {
            log.info("Store '{}' is already selected", storeName);
            return this;
        }

        log.info("Selecting store: {}", storeName);
        WaitUtils.waitForSpinnerToDisappear();
        click(storeMenuButton, WaitStrategy.CLICKABLE);

        By storeOption = By.xpath("//div[contains(@class, 'menu__item')]//p[text()=\"" + storeName + "\"]");
        click(storeOption, WaitStrategy.CLICKABLE);
        WaitUtils.waitForSpinnerToDisappear();
        log.info("Store '{}' selected successfully", storeName);
        return this;
    }

    @Step("Check if store '{storeName}' is already selected")
    public boolean isStoreAlreadySelected(String storeName) {
        try {
            String currentStore = getText(storeCurrentName, WaitStrategy.VISIBLE);
            return currentStore != null && currentStore.trim().equalsIgnoreCase(storeName.trim());
        } catch (Exception e) {
            log.debug("Could not determine current store selection: {}", e.getMessage());
            return false;
        }
    }
}
