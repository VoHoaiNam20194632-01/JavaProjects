package com.automation.pages.components;

import com.automation.enums.WaitStrategy;
import com.automation.pages.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class Footer extends BasePage {

    private final By footerContainer = By.id("page-footer");
    private final By footerLink = By.cssSelector("#page-footer a");

    @Step("Check if footer is displayed")
    public boolean isFooterDisplayed() {
        return isDisplayed(footerContainer);
    }

    @Step("Get footer text")
    public String getFooterText() {
        return getText(footerContainer, WaitStrategy.VISIBLE);
    }

    @Step("Click footer link")
    public void clickFooterLink() {
        click(footerLink, WaitStrategy.CLICKABLE);
    }
}
