package com.automation.pages.admin.product;

import com.automation.enums.WaitStrategy;
import com.automation.pages.BasePage;
import com.automation.utils.DateUtils;
import com.automation.utils.WaitUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class CreateProductPage extends BasePage {

    private final By titleInput = By.xpath("//input[@placeholder=\"Enter title\"]");
    private final By descriptionIframe = By.xpath("//div[span[text()=\"Description\"]]//iframe[@title=\"Rich Text Area\"]");
    private final By descriptionBody = By.xpath("//body[@id=\"tinymce\"]//p");
    private final By saveButton = By.xpath("//bg-full-screen-header//button[normalize-space(.)=\"Save\"]");
    private final By successMessage = By.xpath("//span[text()=\"Save product success!\"]");
    private final By mockupPlaceholder = By.xpath("//span[text()=\"Drag and drop images, videos\"]");
    private final By editDesignButton = By.xpath("//button[text()=\" Edit design \"]");
    private final By saveDesignButton = By.xpath("//bgs-design-editor-dialog//button[text()=\"Save\"]");

    private String generatedTitle;

    @Step("Enter title: {title}")
    public CreateProductPage enterTitle(String title) {
        type(titleInput, title);
        return this;
    }

    @Step("Enter description: {description}")
    public CreateProductPage enterDescription(String description) {
        switchToIFrame(descriptionIframe);
        type(descriptionBody, description, WaitStrategy.VISIBLE);
        switchToDefaultContent();
        return this;
    }

    @Step("Enter title and description for product: {productName}")
    public CreateProductPage enterTitleAndDescription(String productName) {
        String dateTime = DateUtils.getCurrentDateTime();
        generatedTitle = productName + dateTime;
        String description = generatedTitle + "Description";

        enterTitle(generatedTitle);
        enterDescription(description);
        return this;
    }

    @Step("Verify design saved successfully")
    public CreateProductPage verifyDesignSaved() {
        boolean hasMockupPlaceholder = WaitUtils.isElementDisplayed(mockupPlaceholder, 3);
        if (hasMockupPlaceholder) {
            log.warn("Mockup not loaded, retrying save design...");
            click(editDesignButton, WaitStrategy.CLICKABLE);
            click(saveDesignButton, WaitStrategy.CLICKABLE);
            WaitUtils.waitForSpinnerToDisappear();
        }
        return this;
    }

    @Step("Click Save to create product")
    public ProductListPage clickSave() {
        WaitUtils.waitForSpinnerToDisappear();
        click(saveButton, WaitStrategy.CLICKABLE);
        WaitUtils.waitForUrlContains("/admin/products", 10);
        return new ProductListPage();
    }

    @Step("Wait for create product success")
    public CreateProductPage waitForCreateSuccess() {
        WaitUtils.isElementDisplayed(successMessage, 20);
        return this;
    }

    public String getGeneratedTitle() {
        return generatedTitle;
    }
}
