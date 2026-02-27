package com.automation.dataproviders;

import com.automation.models.ProductType;
import com.automation.utils.ProductDataManager;
import org.testng.annotations.DataProvider;

import java.util.List;

public class ProductDataProvider {

    @DataProvider(name = "singleRandomProduct")
    public static Object[][] singleRandomProduct() {
        ProductType product = ProductDataManager.getInstance().getRandomProduct();
        return new Object[][]{{product}};
    }

    @DataProvider(name = "multipleRandomProducts")
    public static Object[][] multipleRandomProducts() {
        List<ProductType> products = ProductDataManager.getInstance().getRandomProducts(3);
        Object[][] data = new Object[products.size()][1];
        for (int i = 0; i < products.size(); i++) {
            data[i][0] = products.get(i);
        }
        return data;
    }
}
