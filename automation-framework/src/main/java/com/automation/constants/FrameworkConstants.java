package com.automation.constants;

import com.automation.config.ConfigFactory;

public final class FrameworkConstants {

    private FrameworkConstants() {
    }

    public static final String PROJECT_PATH = System.getProperty("user.dir");
    public static final String RESOURCES_PATH = PROJECT_PATH + "/src/main/resources";
    public static final String TEST_RESOURCES_PATH = PROJECT_PATH + "/src/test/resources";
    public static final String CONFIG_PATH = RESOURCES_PATH + "/config";
    public static final String TEST_DATA_JSON_PATH = TEST_RESOURCES_PATH + "/testdata/json";
    public static final String TEST_DATA_EXCEL_PATH = TEST_RESOURCES_PATH + "/testdata/excel";
    public static final String SCREENSHOTS_PATH = PROJECT_PATH + "/screenshots";
    public static final String ALLURE_RESULTS_PATH = PROJECT_PATH + "/target/allure-results";
    public static final String LOGS_PATH = PROJECT_PATH + "/logs";
    public static final String COOKIES_PATH = PROJECT_PATH + "/cookies";

    public static final int EXPLICIT_WAIT = ConfigFactory.getWaitConfig().explicitWait();
    public static final int IMPLICIT_WAIT = ConfigFactory.getWaitConfig().implicitWait();
    public static final int PAGE_LOAD_TIMEOUT = ConfigFactory.getWaitConfig().pageLoadTimeout();

    public static final String IMAGES_PATH = RESOURCES_PATH + "/images";

    public static final String YES = "yes";
    public static final String NO = "no";
}
