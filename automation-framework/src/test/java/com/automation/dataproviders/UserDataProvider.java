package com.automation.dataproviders;

import com.automation.utils.DataProviderUtils;
import org.testng.annotations.DataProvider;

public class UserDataProvider {

    @DataProvider(name = "userData")
    public static Object[][] userData() {
        return DataProviderUtils.getJsonTestData("user-data.json");
    }
}
