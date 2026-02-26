package com.automation.utils;

import com.automation.constants.FrameworkConstants;
import com.automation.exceptions.TestDataException;

import java.util.List;
import java.util.Map;

public final class DataProviderUtils {

    private DataProviderUtils() {
    }

    public static Object[][] getJsonTestData(String fileName) {
        String filePath = FrameworkConstants.TEST_DATA_JSON_PATH + "/" + fileName;
        List<Map<String, String>> data = GsonUtils.fromJsonToMapList(filePath);
        return convertToArray(data);
    }

    public static Object[][] getExcelTestData(String fileName, String sheetName) {
        String filePath = FrameworkConstants.TEST_DATA_EXCEL_PATH + "/" + fileName;
        return ExcelReader.readExcelAsArray(filePath, sheetName);
    }

    public static <T> Object[][] getJsonTestData(String fileName, Class<T> clazz) {
        String filePath = FrameworkConstants.TEST_DATA_JSON_PATH + "/" + fileName;
        List<T> data = GsonUtils.fromJsonArray(filePath, clazz);
        Object[][] result = new Object[data.size()][1];
        for (int i = 0; i < data.size(); i++) {
            result[i][0] = data.get(i);
        }
        return result;
    }

    private static Object[][] convertToArray(List<Map<String, String>> data) {
        if (data == null || data.isEmpty()) {
            throw new TestDataException("No test data found");
        }
        Object[][] result = new Object[data.size()][1];
        for (int i = 0; i < data.size(); i++) {
            result[i][0] = data.get(i);
        }
        return result;
    }
}
