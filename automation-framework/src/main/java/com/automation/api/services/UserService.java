package com.automation.api.services;

import com.automation.constants.EndpointConstants;
import com.automation.models.User;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Map;

public class UserService extends BaseService {

    public UserService() {
        super();
    }

    public UserService(String token) {
        super(token);
    }

    @Step("Get all users - page: {page}")
    public Response getUsers(int page) {
        return request()
                .basePath(EndpointConstants.USERS)
                .queryParam("page", String.valueOf(page))
                .get();
    }

    @Step("Get user by id: {id}")
    public Response getUserById(int id) {
        return request()
                .basePath(EndpointConstants.USER_BY_ID)
                .pathParam("id", String.valueOf(id))
                .get();
    }

    @Step("Create user: {user.name}")
    public Response createUser(User user) {
        return request()
                .basePath(EndpointConstants.USERS)
                .body(Map.of("name", user.getName(), "job", user.getJob()))
                .post();
    }

    @Step("Update user id: {id}")
    public Response updateUser(int id, User user) {
        return request()
                .basePath(EndpointConstants.USER_BY_ID)
                .pathParam("id", String.valueOf(id))
                .body(Map.of("name", user.getName(), "job", user.getJob()))
                .put();
    }

    @Step("Patch user id: {id}")
    public Response patchUser(int id, Map<String, String> fields) {
        return request()
                .basePath(EndpointConstants.USER_BY_ID)
                .pathParam("id", String.valueOf(id))
                .body(fields)
                .patch();
    }

    @Step("Delete user id: {id}")
    public Response deleteUser(int id) {
        return request()
                .basePath(EndpointConstants.USER_BY_ID)
                .pathParam("id", String.valueOf(id))
                .delete();
    }
}
