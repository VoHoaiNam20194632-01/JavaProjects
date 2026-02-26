package com.automation.api.builder;

import io.restassured.response.Response;
import org.assertj.core.api.Assertions;

import static org.hamcrest.Matchers.*;

public class ResponseValidator {

    private final Response response;

    public ResponseValidator(Response response) {
        this.response = response;
    }

    public static ResponseValidator of(Response response) {
        return new ResponseValidator(response);
    }

    public ResponseValidator statusCode(int expectedStatusCode) {
        response.then().statusCode(expectedStatusCode);
        return this;
    }

    public ResponseValidator bodyContains(String key, Object value) {
        response.then().body(key, equalTo(value));
        return this;
    }

    public ResponseValidator bodyNotNull(String key) {
        response.then().body(key, notNullValue());
        return this;
    }

    public ResponseValidator bodyHasKey(String key) {
        response.then().body(key, notNullValue());
        return this;
    }

    public ResponseValidator headerContains(String header, String value) {
        Assertions.assertThat(response.getHeader(header)).contains(value);
        return this;
    }

    public ResponseValidator responseTimeLessThan(long milliseconds) {
        response.then().time(lessThan(milliseconds));
        return this;
    }

    public ResponseValidator bodyArraySize(String key, int size) {
        response.then().body(key, hasSize(size));
        return this;
    }

    public <T> T extractAs(Class<T> clazz) {
        return response.as(clazz);
    }

    public <T> T extractPath(String path) {
        return response.jsonPath().get(path);
    }

    public Response getResponse() {
        return response;
    }
}
