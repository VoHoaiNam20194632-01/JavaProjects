package com.automation.utils;

import com.automation.constants.FrameworkConstants;
import com.automation.models.ProductType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class ProductDataManager {

    private static volatile ProductDataManager instance;
    private final List<ProductType> products;
    private final String filePath;
    private final Random random = new Random();

    private ProductDataManager() {
        filePath = FrameworkConstants.TEST_DATA_JSON_PATH + "/data_base_product.json";
        products = new ArrayList<>(GsonUtils.fromJsonArray(filePath, ProductType.class));
    }

    public static synchronized ProductDataManager getInstance() {
        if (instance == null) {
            instance = new ProductDataManager();
        }
        return instance;
    }

    public synchronized ProductType getRandomProduct() {
        if (products.isEmpty()) {
            throw new IllegalStateException("No products available");
        }
        return products.get(random.nextInt(products.size()));
    }

    public synchronized List<ProductType> getRandomProducts(int count) {
        if (count > products.size()) {
            throw new IllegalArgumentException("Requested count exceeds available products: " + count);
        }
        List<ProductType> shuffled = new ArrayList<>(products);
        Collections.shuffle(shuffled, random);
        return shuffled.subList(0, count);
    }

    public synchronized ProductType getProductByName(String name) {
        return products.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + name));
    }

    public synchronized List<ProductType> getAllProducts() {
        return new ArrayList<>(products);
    }

    public synchronized void removeProduct(String name) {
        products.removeIf(p -> p.getName().equalsIgnoreCase(name));
        saveToFile();
    }

    private void saveToFile() {
        GsonUtils.toJson(products, filePath);
    }
}
