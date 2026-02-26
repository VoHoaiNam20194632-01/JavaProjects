package com.automation.base;

import com.automation.api.models.AuthToken;
import com.automation.api.services.AuthService;
import com.automation.config.ConfigFactory;
import com.automation.config.ApiConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;

public abstract class BaseApiTest {

    protected static final Logger log = LogManager.getLogger(BaseApiTest.class);
    protected ApiConfig apiConfig;
    protected AuthService authService;
    protected String token;

    @BeforeClass(alwaysRun = true)
    public void apiSetUp() {
        apiConfig = ConfigFactory.getApiConfig();
        authService = new AuthService();
        log.info("API base URL: {}", apiConfig.apiBaseUrl());
    }

    protected String getToken() {
        if (token == null) {
            var response = authService.login(
                    apiConfig.apiUsername(),
                    apiConfig.apiPassword());
            AuthToken authToken = response.as(AuthToken.class);
            token = authToken.getToken();
            log.info("Obtained auth token successfully");
        }
        return token;
    }
}
