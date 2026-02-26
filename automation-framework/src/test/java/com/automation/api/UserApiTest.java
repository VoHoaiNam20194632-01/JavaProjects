package com.automation.api;

import com.automation.annotations.FrameworkAnnotation;
import com.automation.api.builder.ResponseValidator;
import com.automation.api.services.UserService;
import com.automation.base.BaseApiTest;
import com.automation.dataproviders.UserDataProvider;
import com.automation.enums.CategoryType;
import com.automation.models.User;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("API Tests")
@Feature("User CRUD")
public class UserApiTest extends BaseApiTest {

    private UserService userService;

    @BeforeClass(alwaysRun = true)
    public void initService() {
        userService = new UserService();
    }

    @Test(description = "Get list of users - page 1")
    @FrameworkAnnotation(category = {CategoryType.SMOKE, CategoryType.API},
            author = "Framework", description = "Verify get users returns data")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Retrieve list of users")
    public void testGetUsers() {
        Response response = userService.getUsers(1);

        ResponseValidator.of(response)
                .statusCode(200)
                .bodyContains("page", 1)
                .bodyNotNull("data");

        int total = response.jsonPath().getInt("total");
        assertThat(total).as("Total users should be positive").isPositive();
    }

    @Test(description = "Get single user by ID")
    @FrameworkAnnotation(category = {CategoryType.SMOKE, CategoryType.API},
            author = "Framework", description = "Verify get user by id")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Retrieve single user")
    public void testGetUserById() {
        Response response = userService.getUserById(2);

        ResponseValidator.of(response)
                .statusCode(200)
                .bodyNotNull("data.id")
                .bodyNotNull("data.email")
                .bodyContains("data.id", 2);
    }

    @Test(description = "Get user that does not exist returns 404")
    @FrameworkAnnotation(category = {CategoryType.REGRESSION, CategoryType.API},
            author = "Framework", description = "Verify 404 for non-existent user")
    @Severity(SeverityLevel.NORMAL)
    @Story("Handle non-existent user")
    public void testGetNonExistentUser() {
        Response response = userService.getUserById(9999);

        ResponseValidator.of(response)
                .statusCode(404);
    }

    @Test(description = "Create a new user")
    @FrameworkAnnotation(category = {CategoryType.SMOKE, CategoryType.API},
            author = "Framework", description = "Verify user creation")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Create new user")
    public void testCreateUser() {
        User user = User.builder()
                .name("John Doe")
                .job("Software Engineer")
                .build();

        Response response = userService.createUser(user);

        ResponseValidator.of(response)
                .statusCode(201)
                .bodyContains("name", "John Doe")
                .bodyContains("job", "Software Engineer")
                .bodyNotNull("id")
                .bodyNotNull("createdAt");
    }

    @Test(dataProvider = "userData", dataProviderClass = UserDataProvider.class,
            description = "Data-driven user creation")
    @FrameworkAnnotation(category = {CategoryType.REGRESSION, CategoryType.API},
            author = "Framework", description = "Data-driven user creation test")
    @Severity(SeverityLevel.NORMAL)
    @Story("Create users from test data")
    public void testCreateUserDataDriven(Map<String, String> data) {
        User user = User.builder()
                .name(data.get("name"))
                .job(data.get("job"))
                .build();

        Response response = userService.createUser(user);

        ResponseValidator.of(response)
                .statusCode(201)
                .bodyContains("name", data.get("name"))
                .bodyContains("job", data.get("job"));
    }

    @Test(description = "Update an existing user")
    @FrameworkAnnotation(category = {CategoryType.REGRESSION, CategoryType.API},
            author = "Framework", description = "Verify user update")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Update existing user")
    public void testUpdateUser() {
        User user = User.builder()
                .name("Jane Doe")
                .job("QA Lead")
                .build();

        Response response = userService.updateUser(2, user);

        ResponseValidator.of(response)
                .statusCode(200)
                .bodyContains("name", "Jane Doe")
                .bodyContains("job", "QA Lead")
                .bodyNotNull("updatedAt");
    }

    @Test(description = "Patch user with partial data")
    @FrameworkAnnotation(category = {CategoryType.REGRESSION, CategoryType.API},
            author = "Framework", description = "Verify partial user update")
    @Severity(SeverityLevel.NORMAL)
    @Story("Patch user data")
    public void testPatchUser() {
        Response response = userService.patchUser(2, Map.of("job", "DevOps Engineer"));

        ResponseValidator.of(response)
                .statusCode(200)
                .bodyContains("job", "DevOps Engineer")
                .bodyNotNull("updatedAt");
    }

    @Test(description = "Delete a user")
    @FrameworkAnnotation(category = {CategoryType.REGRESSION, CategoryType.API},
            author = "Framework", description = "Verify user deletion")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Delete user")
    public void testDeleteUser() {
        Response response = userService.deleteUser(2);

        ResponseValidator.of(response)
                .statusCode(204);
    }
}
