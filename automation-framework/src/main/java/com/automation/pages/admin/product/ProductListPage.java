package com.automation.pages.admin.product;

import com.automation.enums.WaitStrategy;
import com.automation.pages.BasePage;
import com.automation.utils.WaitUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class ProductListPage extends BasePage {

    private final By searchInput = By.xpath("//input[@placeholder=\"Search products...\"]");
    private final By createPODProductButton = By.xpath("//span[normalize-space(text())=\"Create POD product\"]");
    private final By successMessage = By.xpath("//div[normalize-space()=\"SuccessSave product success!\" and contains(concat(' ',normalize-space(@class),' '),' p-toast-message-text ')]");
    private final By noProductText = By.xpath("//h5[text()=\"You have no product\"]");
    private final By searchIcon = By.xpath("//span[@class=\"p-input-icon\"]");

    @Step("Navigate to Products page")
    public ProductListPage navigateToProductsPage(String baseUrl) {
        String productsUrl = baseUrl.endsWith("/") ? baseUrl + "admin/products" : baseUrl + "/admin/products";
        navigateTo(productsUrl);
        WaitUtils.waitForSpinnerToDisappear();
        return this;
    }

    @Step("Click Create POD product")
    public DesignEditorPage clickCreatePODProduct() {
        click(createPODProductButton, WaitStrategy.CLICKABLE);
        return new DesignEditorPage();
    }

    @Step("Search product: {title}")
    public ProductListPage searchProduct(String title) {
        type(searchInput, title);
        WaitUtils.waitForSpinnerToDisappear();
        return this;
    }

    @Step("Check if product is displayed: {title}")
    public boolean isProductDisplayed(String title) {
        String xpath = "//a[normalize-space(text())=\"" + title + "\"]";
        return WaitUtils.isElementDisplayed(By.xpath(xpath), 10);
    }

    @Step("Wait for save product success")
    public ProductListPage waitForSaveSuccess() {
        WaitUtils.isElementDisplayed(successMessage, 20);
        return this;
    }

    @Step("Check if no product message is displayed")
    public boolean isNoProductDisplayed() {
        return isDisplayed(noProductText);
    }
}
