package com.automation.dataproviders;

import com.automation.utils.DataProviderUtils;
import org.testng.annotations.DataProvider;

public class LoginDataProvider {

    @DataProvider(name = "loginData")
    public static Object[][] loginData() {
        return DataProviderUtils.getJsonTestData("login-data.json");
    }
}
