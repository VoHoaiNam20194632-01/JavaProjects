package com.automation.api.services;

import com.automation.api.builder.RequestBuilder;
import com.automation.api.specs.RequestSpecFactory;
import io.restassured.specification.RequestSpecification;

public abstract class BaseService {

    protected RequestSpecification baseSpec;

    protected BaseService() {
        this.baseSpec = RequestSpecFactory.getBaseSpec();
    }

    protected BaseService(String token) {
        this.baseSpec = RequestSpecFactory.getAuthenticatedSpec(token);
    }

    protected RequestBuilder request() {
        return new RequestBuilder(baseSpec);
    }

    protected RequestBuilder request(RequestSpecification spec) {
        return new RequestBuilder(spec);
    }
}
