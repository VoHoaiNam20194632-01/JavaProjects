package com.automation.api.interceptors;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ApiRequestFilter implements Filter {

    private static final Logger log = LogManager.getLogger(ApiRequestFilter.class);

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                          FilterableResponseSpecification responseSpec,
                          FilterContext ctx) {
        log.info("==> {} {}", requestSpec.getMethod(), requestSpec.getURI());
        if (requestSpec.getBody() != null) {
            log.info("Request Body: {}", String.valueOf(requestSpec.getBody()));
        }
        return ctx.next(requestSpec, responseSpec);
    }
}
