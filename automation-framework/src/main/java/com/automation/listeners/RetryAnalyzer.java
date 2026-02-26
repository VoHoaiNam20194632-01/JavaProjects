package com.automation.listeners;

import com.automation.config.ConfigFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        int maxRetry = ConfigFactory.getFrameworkConfig().retryCount();
        if (retryCount < maxRetry) {
            retryCount++;
            return true;
        }
        return false;
    }
}
