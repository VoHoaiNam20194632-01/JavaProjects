package com.automation.api.builder;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

public class RequestBuilder {

    private RequestSpecification spec;
    private String basePath = "";
    private Object body;
    private final Map<String, String> pathParams = new HashMap<>();
    private final Map<String, String> queryParams = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();

    public RequestBuilder(RequestSpecification spec) {
        this.spec = spec;
    }

    public RequestBuilder basePath(String basePath) {
        this.basePath = basePath;
        return this;
    }

    public RequestBuilder body(Object body) {
        this.body = body;
        return this;
    }

    public RequestBuilder pathParam(String key, String value) {
        this.pathParams.put(key, value);
        return this;
    }

    public RequestBuilder queryParam(String key, String value) {
        this.queryParams.put(key, value);
        return this;
    }

    public RequestBuilder header(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public Response get() {
        return buildRequest().get(basePath);
    }

    public Response post() {
        return buildRequest().post(basePath);
    }

    public Response put() {
        return buildRequest().put(basePath);
    }

    public Response patch() {
        return buildRequest().patch(basePath);
    }

    public Response delete() {
        return buildRequest().delete(basePath);
    }

    private RequestSpecification buildRequest() {
        RequestSpecification request = RestAssured.given().spec(spec);

        if (!pathParams.isEmpty()) {
            request.pathParams(pathParams);
        }
        if (!queryParams.isEmpty()) {
            request.queryParams(queryParams);
        }
        if (!headers.isEmpty()) {
            request.headers(headers);
        }
        if (body != null) {
            request.body(body);
        }

        return request;
    }
}
