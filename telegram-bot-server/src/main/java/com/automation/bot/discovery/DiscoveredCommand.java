package com.automation.bot.discovery;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO chứa thông tin command được phát hiện tự động.
 * profile != null → chạy bằng Maven profile (mvn test -P{profile})
 * testClass != null → chạy bằng test class (mvn test -Dtest={testClass})
 */
@Getter
@AllArgsConstructor
public class DiscoveredCommand {

    private final String commandName;
    private final String description;
    private final String profile;
    private final String testClass;
}
