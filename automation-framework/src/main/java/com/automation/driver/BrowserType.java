package com.automation.driver;

import com.automation.exceptions.DriverInitializationException;

import java.util.Arrays;

public enum BrowserType {
    CHROME,
    FIREFOX,
    EDGE;

    public static BrowserType fromString(String browser) {
        return Arrays.stream(values())
                .filter(b -> b.name().equalsIgnoreCase(browser))
                .findFirst()
                .orElseThrow(() -> new DriverInitializationException(
                        "Unsupported browser: " + browser + ". Supported: " + Arrays.toString(values())));
    }
}
