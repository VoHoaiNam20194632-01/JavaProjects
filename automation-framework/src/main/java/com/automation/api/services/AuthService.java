package com.automation.api.services;

import com.automation.constants.EndpointConstants;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Map;

public class AuthService extends BaseService {

    public AuthService() {
        super();
    }

    @Step("Login with email: {email}")
    public Response login(String email, String password) {
        return request()
                .basePath(EndpointConstants.LOGIN)
                .body(Map.of("email", email, "password", password))
                .post();
    }

    @Step("Register user with email: {email}")
    public Response register(String email, String password) {
        return request()
                .basePath(EndpointConstants.REGISTER)
                .body(Map.of("email", email, "password", password))
                .post();
    }
}
