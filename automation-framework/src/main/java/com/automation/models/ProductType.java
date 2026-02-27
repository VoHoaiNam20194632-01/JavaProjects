package com.automation.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductType {

    @SerializedName("S_NAME")
    private String name;

    @SerializedName("S_SHORT_CODE")
    private String shortCode;
}
