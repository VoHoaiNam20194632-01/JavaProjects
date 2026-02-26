package com.automation.constants;

public final class EndpointConstants {

    private EndpointConstants() {
    }

    // Auth endpoints
    public static final String LOGIN = "/login";
    public static final String REGISTER = "/register";

    // User endpoints
    public static final String USERS = "/users";
    public static final String USER_BY_ID = "/users/{id}";
    public static final String USERS_PAGE = "/users?page={page}";

    // Resource endpoints
    public static final String RESOURCES = "/unknown";
    public static final String RESOURCE_BY_ID = "/unknown/{id}";
}
