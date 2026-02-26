package com.automation.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private int id;
    private String email;
    private String name;
    private String job;
    private String firstName;
    private String lastName;
    private String avatar;
    private String createdAt;
    private String updatedAt;
}
