package com.automation.listeners;

import com.automation.config.ConfigFactory;
import com.automation.reports.AllureManager;
import com.automation.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

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
            byte[] screenshot = ScreenshotUtils.takeScreenshot();
            if (screenshot.length > 0) {
                AllureManager.attachScreenshot(screenshot);
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("--- Test SKIPPED: {} ---", result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        log.warn("--- Test FAILED within success percentage: {} ---",
                result.getMethod().getMethodName());
    }
}
