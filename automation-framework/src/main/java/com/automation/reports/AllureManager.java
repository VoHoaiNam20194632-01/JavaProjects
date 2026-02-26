package com.automation.reports;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;

public final class AllureManager {

    private static final Logger log = LogManager.getLogger(AllureManager.class);

    private AllureManager() {
    }

    @Attachment(value = "Screenshot", type = "image/png")
    public static byte[] attachScreenshot(byte[] screenshot) {
        return screenshot;
    }

    @Attachment(value = "{name}", type = "text/plain")
    public static String attachText(String name, String content) {
        return content;
    }

    @Attachment(value = "{name}", type = "application/json")
    public static String attachJson(String name, String json) {
        return json;
    }

    public static void attachScreenshotToAllure(String name, byte[] screenshot) {
        Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), ".png");
    }

    public static void addEnvironmentInfo(String name, String value) {
        Allure.parameter(name, value);
    }

    public static void step(String stepDescription) {
        Allure.step(stepDescription);
        log.info("Step: {}", stepDescription);
    }

    public static void step(String stepDescription, Allure.ThrowableRunnable runnable) {
        Allure.step(stepDescription, runnable);
    }
}
