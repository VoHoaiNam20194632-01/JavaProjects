package com.automation.api;

import com.automation.annotations.FrameworkAnnotation;
import com.automation.api.builder.ResponseValidator;
import com.automation.api.models.AuthToken;
import com.automation.base.BaseApiTest;
import com.automation.enums.CategoryType;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("API Tests")
@Feature("Authentication")
public class AuthApiTest extends BaseApiTest {

    @Test(description = "Successful login returns token")
    @FrameworkAnnotation(category = {CategoryType.SMOKE, CategoryType.API},
            author = "Framework", description = "Verify successful login returns token")
    @Severity(SeverityLevel.BLOCKER)
    @Story("User receives token on valid login")
    public void testSuccessfulLogin() {
        Response response = authService.login("eve.holt@reqres.in", "cityslicka");

        ResponseValidator.of(response)
                .statusCode(200)
                .bodyNotNull("token");

        AuthToken authToken = response.as(AuthToken.class);
        assertThat(authToken.getToken())
                .as("Token should not be null or empty")
                .isNotEmpty();
    }

    @Test(description = "Login without password returns error")
    @FrameworkAnnotation(category = {CategoryType.REGRESSION, CategoryType.API},
            author = "Framework", description = "Verify login fails without password")
    @Severity(SeverityLevel.CRITICAL)
    @Story("API returns error for missing password")
    public void testLoginWithoutPassword() {
        Response response = authService.login("eve.holt@reqres.in", "");

        ResponseValidator.of(response)
                .statusCode(400)
                .bodyContains("error", "Missing password");
    }

    @Test(description = "Successful registration returns token and id")
    @FrameworkAnnotation(category = {CategoryType.SMOKE, CategoryType.API},
            author = "Framework", description = "Verify successful registration")
    @Severity(SeverityLevel.BLOCKER)
    @Story("User can register successfully")
    public void testSuccessfulRegistration() {
        Response response = authService.register("eve.holt@reqres.in", "pistol");

        ResponseValidator.of(response)
                .statusCode(200)
                .bodyNotNull("id")
                .bodyNotNull("token");
    }

    @Test(description = "Registration without password returns error")
    @FrameworkAnnotation(category = {CategoryType.REGRESSION, CategoryType.API},
            author = "Framework", description = "Verify registration fails without password")
    @Severity(SeverityLevel.CRITICAL)
    @Story("API returns error for incomplete registration")
    public void testRegistrationWithoutPassword() {
        Response response = authService.register("eve.holt@reqres.in", "");

        ResponseValidator.of(response)
                .statusCode(400)
                .bodyContains("error", "Missing password");
    }
}
