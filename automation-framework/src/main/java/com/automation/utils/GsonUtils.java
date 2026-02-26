package com.automation.utils;

import com.automation.exceptions.TestDataException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public final class GsonUtils {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private GsonUtils() {
    }

    public static <T> T fromJson(String filePath, Class<T> clazz) {
        try (FileReader reader = new FileReader(filePath)) {
            return GSON.fromJson(reader, clazz);
        } catch (IOException e) {
            throw new TestDataException("Failed to read JSON file: " + filePath, e);
        }
    }

    public static <T> List<T> fromJsonArray(String filePath, Class<T> clazz) {
        try (FileReader reader = new FileReader(filePath)) {
            Type listType = TypeToken.getParameterized(List.class, clazz).getType();
            return GSON.fromJson(reader, listType);
        } catch (IOException e) {
            throw new TestDataException("Failed to read JSON array file: " + filePath, e);
        }
    }

    public static List<Map<String, String>> fromJsonToMapList(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<List<Map<String, String>>>() {}.getType();
            return GSON.fromJson(reader, type);
        } catch (IOException e) {
            throw new TestDataException("Failed to read JSON file: " + filePath, e);
        }
    }

    public static void toJson(Object object, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            GSON.toJson(object, writer);
        } catch (IOException e) {
            throw new TestDataException("Failed to write JSON file: " + filePath, e);
        }
    }

    public static String toJsonString(Object object) {
        return GSON.toJson(object);
    }
}
