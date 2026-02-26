package com.automation.api.interceptors;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ApiResponseFilter implements Filter {

    private static final Logger log = LogManager.getLogger(ApiResponseFilter.class);

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                          FilterableResponseSpecification responseSpec,
                          FilterContext ctx) {
        Response response = ctx.next(requestSpec, responseSpec);

        log.info("<== {} {} [Status: {}] [Time: {}ms]",
                requestSpec.getMethod(),
                requestSpec.getURI(),
                response.getStatusCode(),
                response.getTime());

        if (response.getStatusCode() >= 400) {
            log.error("Error Response [{}]: {}", response.getStatusCode(), response.getBody().asString());
        }

        return response;
    }
}
