package com.automation.utils;

import com.automation.config.ConfigFactory;
import com.automation.constants.FrameworkConstants;
import com.automation.driver.DriverManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class CookieManager {

    private static final Logger log = LogManager.getLogger(CookieManager.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private CookieManager() {
    }

    public static void saveSession() {
        ensureCookiesDirectory();
        saveCookies();
        saveStorage("localStorage");
        saveStorage("sessionStorage");
    }

    public static boolean loadSession(String baseUrl) {
        // Navigate to domain first so cookies/storage can be set
        DriverManager.getDriver().get(baseUrl);

        boolean cookiesLoaded = loadCookies();
        boolean localLoaded = loadStorage("localStorage");
        boolean sessionLoaded = loadStorage("sessionStorage");

        if (!cookiesLoaded && !localLoaded && !sessionLoaded) {
            return false;
        }

        // Refresh to apply session
        DriverManager.getDriver().navigate().refresh();
        return true;
    }

    private static void saveCookies() {
        Set<Cookie> cookies = DriverManager.getDriver().manage().getCookies();
        if (cookies.isEmpty()) {
            log.debug("No cookies to save");
            return;
        }

        List<CookieData> cookieDataList = cookies.stream()
                .map(CookieData::from)
                .toList();

        String filePath = getFilePath("cookies");
        try (FileWriter writer = new FileWriter(filePath)) {
            GSON.toJson(cookieDataList, writer);
            log.info("Saved {} cookies to {}", cookieDataList.size(), filePath);
        } catch (IOException e) {
            log.error("Failed to save cookies to {}", filePath, e);
        }
    }

    private static void saveStorage(String storageType) {
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();

        @SuppressWarnings("unchecked")
        Map<String, String> storage = (Map<String, String>) js.executeScript(
                "var items = {}; " +
                "var s = window[arguments[0]]; " +
                "for (var i = 0; i < s.length; i++) { " +
                "  var key = s.key(i); " +
                "  items[key] = s.getItem(key); " +
                "} " +
                "return items;", storageType);

        if (storage == null || storage.isEmpty()) {
            log.debug("No {} data to save", storageType);
            return;
        }

        String filePath = getFilePath(storageType);
        try (FileWriter writer = new FileWriter(filePath)) {
            GSON.toJson(storage, writer);
            log.info("Saved {} {} entries to {}", storage.size(), storageType, filePath);
        } catch (IOException e) {
            log.error("Failed to save {} to {}", storageType, filePath, e);
        }
    }

    private static boolean loadCookies() {
        String filePath = getFilePath("cookies");
        if (!Files.exists(Paths.get(filePath))) {
            return false;
        }

        List<CookieData> cookieDataList;
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<List<CookieData>>() {}.getType();
            cookieDataList = GSON.fromJson(reader, type);
        } catch (IOException e) {
            log.error("Failed to read cookies from {}", filePath, e);
            return false;
        }

        if (cookieDataList == null || cookieDataList.isEmpty()) {
            return false;
        }

        int loaded = 0;
        for (CookieData data : cookieDataList) {
            if (data.isExpired()) {
                log.debug("Skipping expired cookie: {}", data.name);
                continue;
            }
            try {
                DriverManager.getDriver().manage().addCookie(data.toCookie());
                loaded++;
            } catch (Exception e) {
                log.debug("Could not add cookie '{}': {}", data.name, e.getMessage());
            }
        }

        log.info("Loaded {} cookies", loaded);
        return loaded > 0;
    }

    private static boolean loadStorage(String storageType) {
        String filePath = getFilePath(storageType);
        if (!Files.exists(Paths.get(filePath))) {
            return false;
        }

        Map<String, String> storage;
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            storage = GSON.fromJson(reader, type);
        } catch (IOException e) {
            log.error("Failed to read {} from {}", storageType, filePath, e);
            return false;
        }

        if (storage == null || storage.isEmpty()) {
            return false;
        }

        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
        int loaded = 0;
        for (Map.Entry<String, String> entry : storage.entrySet()) {
            try {
                js.executeScript("window[arguments[0]].setItem(arguments[1], arguments[2]);",
                        storageType, entry.getKey(), entry.getValue());
                loaded++;
            } catch (Exception e) {
                log.debug("Could not set {} '{}': {}", storageType, entry.getKey(), e.getMessage());
            }
        }

        log.info("Loaded {} {} entries", loaded, storageType);
        return loaded > 0;
    }

    private static void ensureCookiesDirectory() {
        Path dir = Paths.get(FrameworkConstants.COOKIES_PATH);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            log.error("Failed to create cookies directory: {}", dir, e);
        }
    }

    private static String getFilePath(String type) {
        String env = ConfigFactory.getFrameworkConfig().environment();
        return FrameworkConstants.COOKIES_PATH + "/" + type + "_" + env + ".json";
    }

    static class CookieData {
        String name;
        String value;
        String domain;
        String path;
        Date expiry;
        boolean secure;
        boolean httpOnly;

        static CookieData from(Cookie cookie) {
            CookieData data = new CookieData();
            data.name = cookie.getName();
            data.value = cookie.getValue();
            data.domain = cookie.getDomain();
            data.path = cookie.getPath();
            data.expiry = cookie.getExpiry();
            data.secure = cookie.isSecure();
            data.httpOnly = cookie.isHttpOnly();
            return data;
        }

        Cookie toCookie() {
            return new Cookie.Builder(name, value)
                    .domain(domain)
                    .path(path)
                    .expiresOn(expiry)
                    .isSecure(secure)
                    .isHttpOnly(httpOnly)
                    .build();
        }

        boolean isExpired() {
            return expiry != null && expiry.before(new Date());
        }
    }
}
