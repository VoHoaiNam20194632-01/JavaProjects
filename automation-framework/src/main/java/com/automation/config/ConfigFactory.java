package com.automation.config;

import org.aeonbits.owner.ConfigCache;

public final class ConfigFactory {

    private ConfigFactory() {
    }

    public static FrameworkConfig getFrameworkConfig() {
        return ConfigCache.getOrCreate(FrameworkConfig.class);
    }

    public static ApiConfig getApiConfig() {
        return ConfigCache.getOrCreate(ApiConfig.class);
    }

    public static WaitConfig getWaitConfig() {
        return ConfigCache.getOrCreate(WaitConfig.class);
    }
}
