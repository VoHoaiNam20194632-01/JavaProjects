package com.automation.listeners;

import com.automation.config.ConfigFactory;
import com.automation.driver.DriverManager;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Attachment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.IConfigurationListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

public class TestListener implements ITestListener, IConfigurationListener {

    private static final Logger log = LogManager.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        log.info("========== Suite started: {} ==========", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("========== Suite finished: {} ==========", context.getName());
        log.info("Passed: {}, Failed: {}, Skipped: {}",
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
    }

    @Override
    public void onTestStart(ITestResult result) {
        log.info("--- Test started: {} ---", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("--- Test PASSED: {} ---", result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("--- Test FAILED: {} ---", result.getMethod().getMethodName());
        log.error("Failure reason: {}", result.getThrowable().getMessage());

        if (ConfigFactory.getFrameworkConfig().screenshotOnFailure()) {
            captureScreenshot(result.getMethod().getMethodName());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("--- Test SKIPPED: {} ---", result.getMethod().getMethodName());
    }

    @Override
    public void onConfigurationFailure(ITestResult result) {
        log.error("--- Config FAILED: {} ---", result.getMethod().getMethodName());
        log.error("Failure reason: {}", result.getThrowable().getMessage());

        if (ConfigFactory.getFrameworkConfig().screenshotOnFailure()) {
            captureAndSaveScreenshot(result.getMethod().getMethodName());
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        log.warn("--- Test FAILED within success percentage: {} ---",
                result.getMethod().getMethodName());
    }

    private void captureScreenshot(String testName) {
        WebDriver driver = DriverManager.getDriver();
        if (driver == null) {
            log.warn("Cannot take screenshot - driver is null");
            return;
        }
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(testName + " - Screenshot", "image/png",
                    new ByteArrayInputStream(screenshot), ".png");
            log.info("Screenshot attached for: {}", testName);
        } catch (Exception e) {
            log.warn("Failed to attach screenshot via Allure: {}", e.getMessage());
        }
    }

    /**
     * Chụp screenshot khi @BeforeMethod/@AfterMethod fail.
     * Allure fixture đã stop trước khi listener chạy nên updateFixture không work.
     * → Ghi file PNG vào allure-results, sau đó update container JSON trực tiếp.
     */
    private void captureAndSaveScreenshot(String methodName) {
        WebDriver driver = DriverManager.getDriver();
        if (driver == null) {
            log.warn("Cannot take screenshot - driver is null");
            return;
        }
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

            // Ghi file PNG vào allure-results
            String source = java.util.UUID.randomUUID() + "-screenshot.png";
            java.nio.file.Path allureDir = java.nio.file.Paths.get("target", "allure-results");
            java.nio.file.Files.createDirectories(allureDir);
            java.nio.file.Files.write(allureDir.resolve(source), screenshot);

            // Tìm container JSON của method vừa fail và thêm attachment vào
            java.io.File[] containers = allureDir.toFile().listFiles(
                    (dir, name) -> name.endsWith("-container.json"));
            if (containers != null) {
                for (java.io.File container : containers) {
                    String content = java.nio.file.Files.readString(container.toPath());
                    if (content.contains("\"name\":\"" + methodName + "\"") && content.contains("\"status\":\"broken\"")) {
                        // Thêm attachment vào befores[0].attachments
                        String target = "\"attachments\":[]";
                        String replacement = "\"attachments\":[{\"name\":\"" + methodName
                                + " - Screenshot\",\"source\":\"" + source
                                + "\",\"type\":\"image/png\"}]";
                        // Chỉ replace attachment đầu tiên (của fixture bị fail)
                        content = content.replaceFirst(java.util.regex.Pattern.quote(target), replacement);
                        java.nio.file.Files.writeString(container.toPath(), content);
                        log.info("Screenshot attached to container: {}", container.getName());
                        break;
                    }
                }
            }

            log.info("Screenshot saved: {}", source);
        } catch (Exception e) {
            log.warn("Failed to save screenshot: {}", e.getMessage());
        }
    }
}
