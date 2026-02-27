package com.automation.pages.admin.product;

import com.automation.enums.WaitStrategy;
import com.automation.pages.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class ProductDetailPage extends BasePage {

    private final By productTitle = By.xpath("//input[@placeholder=\"Enter title\"]");
    private final By backButton = By.xpath("//bg-full-screen-header//div//div//div[1]");

    @Step("Get product title")
    public String getProductTitle() {
        return waitAndFind(productTitle, WaitStrategy.VISIBLE).getAttribute("value");
    }

    @Step("Click back to product list")
    public ProductListPage clickBack() {
        click(backButton, WaitStrategy.CLICKABLE);
        return new ProductListPage();
    }
}
