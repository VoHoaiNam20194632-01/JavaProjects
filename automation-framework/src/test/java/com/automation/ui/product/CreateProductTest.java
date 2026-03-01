package com.automation.ui.product;

import com.automation.annotations.FrameworkAnnotation;
import com.automation.base.BaseTest;
import com.automation.dataproviders.ProductDataProvider;
import com.automation.enums.CategoryType;
import com.automation.models.ProductType;
import com.automation.pages.admin.product.CreateProductPage;
import com.automation.pages.admin.product.DesignEditorPage;
import com.automation.pages.admin.product.ProductListPage;
import com.automation.utils.ProductDataManager;
import io.qameta.allure.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("UI Tests")
@Feature("Product Management")
public class CreateProductTest extends BaseTest {

    private ProductListPage productListPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAndNavigate() {
        loginWithSessionReuse();
        productListPage = new ProductListPage();
        productListPage.navigateToProductsPage(getBaseUrl());
    }

    @Test(description = "Create a POD product with random product base",
            dataProvider = "singleRandomProduct", dataProviderClass = ProductDataProvider.class)
    @FrameworkAnnotation(category = {CategoryType.REGRESSION},
            author = "Framework", description = "Create a POD product end-to-end")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Create POD Product")
    public void testCreatePODProduct(ProductType productType) {
        log.info("Creating POD product with base: {}", productType.getName());

        DesignEditorPage designEditor = productListPage.clickCreatePODProduct();

        ProductType selectedProduct = resolveProductType(designEditor, productType);
        designEditor.clickSaveProductType();

        designEditor.clickAddColor();
        designEditor.selectMaxColors();
        designEditor.closeColorPanel();

        designEditor.uploadRecommendedDesign();

        CreateProductPage createProductPage = designEditor.saveDesign();
        createProductPage.verifyDesignSaved();
        createProductPage.enterTitleAndDescription(selectedProduct.getName());
        String title = createProductPage.getGeneratedTitle();

        ProductListPage listPage = createProductPage.clickSave();
        listPage.waitForSaveSuccess();
        listPage.searchProduct(title);

        assertThat(listPage.isProductDisplayed(title))
                .as("Product '%s' should be displayed in product list", title)
                .isTrue();
    }

    private ProductType resolveProductType(DesignEditorPage designEditor, ProductType initial) {
        ProductDataManager dataManager = ProductDataManager.getInstance();
        ProductType current = initial;
        int maxAttempts = dataManager.getAllProducts().size();

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            if (designEditor.searchAndSelectProductType(current.getName())) {
                return current;
            }
            log.warn("Product type '{}' not found, removing and trying another", current.getName());
            dataManager.removeProduct(current.getName());
            current = dataManager.getRandomProduct();
            log.info("Trying product type: {}", current.getName());
        }
        throw new IllegalStateException("No available product types found in design editor after exhausting all options");
    }
}
