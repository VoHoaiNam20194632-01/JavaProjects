package com.automation.enums;

public enum Environment {
    DEV,
    REPLICA,
    PROD;

    public static Environment fromString(String env) {
        for (Environment e : values()) {
            if (e.name().equalsIgnoreCase(env)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown environment: " + env);
    }
}
