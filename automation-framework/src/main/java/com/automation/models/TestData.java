package com.automation.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestData<T> {

    private String testName;
    private String description;
    private T data;
    private boolean enabled;
}
