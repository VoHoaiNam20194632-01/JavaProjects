package com.automation.utils;

import com.automation.exceptions.TestDataException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class JacksonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .enable(SerializationFeature.INDENT_OUTPUT);

    private JacksonUtils() {
    }

    public static <T> T fromJson(String filePath, Class<T> clazz) {
        try {
            return MAPPER.readValue(new File(filePath), clazz);
        } catch (IOException e) {
            throw new TestDataException("Failed to read JSON file: " + filePath, e);
        }
    }

    public static <T> List<T> fromJsonArray(String filePath, Class<T> clazz) {
        try {
            return MAPPER.readValue(new File(filePath),
                    MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            throw new TestDataException("Failed to read JSON array file: " + filePath, e);
        }
    }

    public static List<Map<String, Object>> fromJsonToMapList(String filePath) {
        try {
            return MAPPER.readValue(new File(filePath), new TypeReference<>() {});
        } catch (IOException e) {
            throw new TestDataException("Failed to read JSON file: " + filePath, e);
        }
    }

    public static <T> T fromString(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new TestDataException("Failed to parse JSON string", e);
        }
    }

    public static void toJson(Object object, String filePath) {
        try {
            MAPPER.writeValue(new File(filePath), object);
        } catch (IOException e) {
            throw new TestDataException("Failed to write JSON file: " + filePath, e);
        }
    }

    public static String toJsonString(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (IOException e) {
            throw new TestDataException("Failed to serialize object to JSON", e);
        }
    }
}
