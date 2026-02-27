package com.automation.pages.admin.product;

import com.automation.constants.FrameworkConstants;
import com.automation.enums.WaitStrategy;
import com.automation.pages.BasePage;
import com.automation.utils.WaitUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DesignEditorPage extends BasePage {

    // Product Type locators
    private final By productTypeSearchInput = By.xpath("//input[@placeholder=\"Search by name\"]");
    private final By saveProductTypeButton = By.xpath("//bgs-add-product-type-dialog//button[text()=\"Save\"]");

    // Color locators
    private final By addColorButton = By.xpath("//bgs-product-type-item//button[@severity=\"secondary\"]");
    private final By colorHeader = By.xpath("//div[@class=\"flex justify-between\"]");
    private final By colorItems = By.xpath("//div[.//div[contains(text(),\"Add color\")]]//div[contains(concat(' ',normalize-space(@class),' '),' grid ')]/div");
    private final By selectedColors = By.xpath("//div[.//div[contains(text(),\"Add color\")]]//div[contains(concat(' ',normalize-space(@class),' '),' grid ')]//span[contains(concat(' ',normalize-space(@class),' '),' text-primary ')]");
    private final By closeColorButton = By.xpath("//div[@class=\"flex justify-between\"]//svg-icon");

    // Design locators
    private final By recommendedSizeText = By.cssSelector("div.flex-1.flex.flex-col.gap-8 > div > div:nth-child(3)");
    private final By addImageButton = By.xpath("//button[text()=\"Add image\"]");
    private final By mediaSearchInput = By.xpath("//input[@placeholder=\"Search by file name\"]");
    private final By mediaAddImageButton = By.xpath("//span[text()=\"Add image\"]");
    private final By fileInput = By.xpath("//input[@accept=\"image/*\"]");
    private final By uploadCompleteText = By.xpath("//span[text()=\"100%\"]");
    private final By mediaSaveButton = By.xpath("//add-image-dialog//button[.//span[normalize-space()=\"Save\"]]");
    private final By saveDesignButton = By.xpath("//bgs-design-editor-dialog//button[text()=\"Save\"]");
    private final By designApplied = By.xpath("//div[span[normalize-space(text())=\"Layer\"]]//div[@draggable=\"true\"]");

    private static final String[] AVAILABLE_IMAGE_SIZES = {
            "4200x4800", "4200x3000", "4500x5400", "4500x5100", "4500x5000", "4800x5400",
            "4050x4650", "4050x4050", "3600x4800", "3600x4795", "3000x4000", "2800x3200",
            "2400x4800", "2400x3200", "2400x3197", "2100x2400", "6000x4000", "6101x8126",
            "8740x9433", "9449x11814", "9549x5505", "12000x6000", "12289x5906"
    };
    private static final String DEFAULT_LARGE_IMAGE = "4200x4800";

    @Step("Search and select product type: {name}")
    public boolean searchAndSelectProductType(String name) {
        type(productTypeSearchInput, name);
        WaitUtils.waitForSpinnerToDisappear();
        String xpath = "//span[normalize-space(text())=\"" + name + "\"]";
        if (!WaitUtils.isElementDisplayed(By.xpath(xpath), 5)) {
            log.warn("Product type not found: {}", name);
            return false;
        }
        click(By.xpath(xpath), WaitStrategy.CLICKABLE);
        return true;
    }

    @Step("Check if product type is displayed: {name}")
    public boolean isProductTypeDisplayed(String name) {
        String xpath = "//span[normalize-space(text())=\"" + name + "\"]";
        return WaitUtils.isElementDisplayed(By.xpath(xpath), 5);
    }

    @Step("Click Save product type")
    public DesignEditorPage clickSaveProductType() {
        click(saveProductTypeButton, WaitStrategy.CLICKABLE);
        WaitUtils.waitForSpinnerToDisappear();
        return this;
    }

    @Step("Click Add Color")
    public DesignEditorPage clickAddColor() {
        click(addColorButton, WaitStrategy.CLICKABLE);
        return this;
    }

    @Step("Select maximum colors")
    public List<String> selectMaxColors() {
        String headerText = getText(colorHeader);
        int maxCount = parseMaxColorCount(headerText);
        log.info("Max color count: {}", maxCount);

        List<WebElement> colorList = waitAndFindAll(colorItems, WaitStrategy.VISIBLE);
        int selectCount = Math.min(maxCount, colorList.size());
        for (int i = 0; i < selectCount; i++) {
            colorList.get(i).click();
        }

        List<WebElement> selected = waitAndFindAll(selectedColors, WaitStrategy.VISIBLE);
        List<String> colorNames = new ArrayList<>();
        for (WebElement color : selected) {
            colorNames.add(color.getText());
        }
        log.info("Selected colors: {}", colorNames);
        return colorNames;
    }

    @Step("Close color panel")
    public DesignEditorPage closeColorPanel() {
        List<WebElement> colorList = getDriver().findElements(colorItems);
        if (colorList.size() > 1) {
            click(closeColorButton, WaitStrategy.CLICKABLE);
        }
        return this;
    }

    @Step("Upload recommended design image")
    public DesignEditorPage uploadRecommendedDesign() {
        String sizeText = getText(recommendedSizeText);
        String sizeRecommend = extractSize(sizeText);
        log.info("Recommended size: {}", sizeRecommend);

        if (sizeRecommend != null) {
            click(addImageButton, WaitStrategy.CLICKABLE);
            WaitUtils.waitForSpinnerToDisappear();

            String imageToUse = findOrUploadImage(sizeRecommend);

            String xpath = "//span[normalize-space(text())=\"" + imageToUse + ".jpg\"]";
            click(By.xpath(xpath), WaitStrategy.CLICKABLE);
            waitAndFind(designApplied, WaitStrategy.VISIBLE);
        }
        return this;
    }

    @Step("Save design")
    public CreateProductPage saveDesign() {
        click(saveDesignButton, WaitStrategy.CLICKABLE);
        WaitUtils.waitForSpinnerToDisappear();
        return new CreateProductPage();
    }

    private String findOrUploadImage(String sizeRecommend) {
        // Step 1: Search exact size in media library
        type(mediaSearchInput, sizeRecommend);

        String xpathExact = "//span[text()=\" " + sizeRecommend + ".jpg \"]";
        if (WaitUtils.isElementDisplayed(By.xpath(xpathExact), 5)) {
            log.info("Found exact size in media library: {}", sizeRecommend);
            return sizeRecommend;
        }

        // Step 2: Upload exact size if local file exists
        if (isLocalImageExists(sizeRecommend)) {
            log.info("Uploading exact size from local: {}", sizeRecommend);
            uploadImage(sizeRecommend);
            return sizeRecommend;
        }

        // Step 3: Find closest larger image
        String closestSize = findClosestLargerImage(sizeRecommend);
        log.info("Using closest larger image: {}", closestSize);

        type(mediaSearchInput, closestSize);

        String xpathClosest = "//span[text()=\" " + closestSize + ".jpg \"]";
        if (WaitUtils.isElementDisplayed(By.xpath(xpathClosest), 5)) {
            log.info("Found closest size in media library: {}", closestSize);
            return closestSize;
        }

        // Step 4: Upload closest size
        if (isLocalImageExists(closestSize)) {
            log.info("Uploading closest size from local: {}", closestSize);
            uploadImage(closestSize);
            return closestSize;
        }

        // Step 5: Fallback to default
        log.info("Fallback to default image: {}", DEFAULT_LARGE_IMAGE);
        type(mediaSearchInput, DEFAULT_LARGE_IMAGE);

        String xpathDefault = "//span[text()=\" " + DEFAULT_LARGE_IMAGE + ".jpg \"]";
        if (!WaitUtils.isElementDisplayed(By.xpath(xpathDefault), 5)) {
            uploadImage(DEFAULT_LARGE_IMAGE);
        }
        return DEFAULT_LARGE_IMAGE;
    }

    private void uploadImage(String size) {
        click(mediaAddImageButton, WaitStrategy.CLICKABLE);
        String imagePath = FrameworkConstants.IMAGES_PATH + "/" + size + ".jpg";
        uploadFile(fileInput, imagePath);
        WaitUtils.waitForElement(uploadCompleteText, WaitStrategy.VISIBLE, 60);
        click(mediaSaveButton, WaitStrategy.CLICKABLE);
        WaitUtils.waitForSpinnerToDisappear();
    }

    private boolean isLocalImageExists(String size) {
        String imagePath = FrameworkConstants.IMAGES_PATH + "/" + size + ".jpg";
        return new File(imagePath).exists();
    }

    private String findClosestLargerImage(String recommendedSize) {
        int[] rec = parseDimensions(recommendedSize);
        if (rec == null) return DEFAULT_LARGE_IMAGE;

        int recWidth = rec[0];
        int recHeight = rec[1];
        long recArea = (long) recWidth * recHeight;

        String closestSize = DEFAULT_LARGE_IMAGE;
        long closestDiff = Long.MAX_VALUE;

        for (String size : AVAILABLE_IMAGE_SIZES) {
            if (!isLocalImageExists(size)) continue;

            int[] dims = parseDimensions(size);
            if (dims == null) continue;

            if (dims[0] >= recWidth && dims[1] >= recHeight) {
                long diff = (long) dims[0] * dims[1] - recArea;
                if (diff < closestDiff) {
                    closestDiff = diff;
                    closestSize = size;
                }
            }
        }
        return closestSize;
    }

    private String extractSize(String text) {
        Matcher matcher = Pattern.compile("(\\d+x\\d+)").matcher(text);
        return matcher.find() ? matcher.group(1) : null;
    }

    private int parseMaxColorCount(String headerText) {
        String[] parts = headerText.split("/");
        if (parts.length == 2) {
            String numberStr = parts[1].replace(")", "").trim();
            return Integer.parseInt(numberStr);
        }
        return 1;
    }

    private int[] parseDimensions(String size) {
        String[] parts = size.split("x");
        if (parts.length != 2) return null;
        return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
    }
}
