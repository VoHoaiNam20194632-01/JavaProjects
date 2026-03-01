package com.automation.bot.discovery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Scan automation-framework để phát hiện Maven profiles và test classes.
 *
 * Quy tắc:
 * - Parse pom.xml → lấy tất cả profile IDs → tạo command chạy theo profile
 * - Walk src/test/java → tìm *Test.java → tạo command chạy theo test class
 * - Loại trừ: BaseTest, BaseApiTest, package base/, dataproviders/
 * - Tên command = tên class bỏ "Test" suffix, lowercase. VD: CreateProductTest → /createproduct
 */
@Slf4j
@Component
public class TestSuiteScanner {

    private static final Set<String> EXCLUDED_CLASSES = Set.of("BaseTest", "BaseApiTest");
    private static final Set<String> EXCLUDED_PACKAGES = Set.of("base", "dataproviders");

    public List<DiscoveredCommand> scan(String frameworkPath) {
        List<DiscoveredCommand> commands = new ArrayList<>();

        Path root = Path.of(frameworkPath);
        if (!Files.isDirectory(root)) {
            log.warn("Framework path does not exist: {}", frameworkPath);
            return commands;
        }

        commands.addAll(scanProfiles(root));
        commands.addAll(scanTestClasses(root));

        log.info("Discovered {} commands ({} profiles, {} test classes)",
                commands.size(),
                commands.stream().filter(c -> c.getProfile() != null).count(),
                commands.stream().filter(c -> c.getTestClass() != null).count());

        return commands;
    }

    /**
     * Parse pom.xml → lấy tất cả <profile><id> elements.
     */
    private List<DiscoveredCommand> scanProfiles(Path root) {
        List<DiscoveredCommand> commands = new ArrayList<>();
        Path pomFile = root.resolve("pom.xml");

        if (!Files.isRegularFile(pomFile)) {
            log.warn("pom.xml not found at: {}", pomFile);
            return commands;
        }

        try {
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(pomFile.toFile());

            NodeList profileNodes = doc.getElementsByTagName("profile");
            for (int i = 0; i < profileNodes.getLength(); i++) {
                NodeList children = profileNodes.item(i).getChildNodes();
                for (int j = 0; j < children.getLength(); j++) {
                    if ("id".equals(children.item(j).getNodeName())) {
                        String profileId = children.item(j).getTextContent().trim();
                        commands.add(new DiscoveredCommand(
                                profileId,
                                "Run " + profileId + " test suite (profile)",
                                profileId,
                                null
                        ));
                        log.debug("Discovered profile: {}", profileId);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse pom.xml: {}", e.getMessage(), e);
        }

        return commands;
    }

    /**
     * Walk src/test/java → tìm *Test.java files.
     * Loại trừ base classes và dataproviders.
     */
    private List<DiscoveredCommand> scanTestClasses(Path root) {
        List<DiscoveredCommand> commands = new ArrayList<>();
        Path testDir = root.resolve("src/test/java");

        if (!Files.isDirectory(testDir)) {
            log.warn("Test directory not found: {}", testDir);
            return commands;
        }

        try (Stream<Path> paths = Files.walk(testDir)) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith("Test.java"))
                    .forEach(p -> {
                        String fileName = p.getFileName().toString();
                        String className = fileName.replace(".java", "");

                        if (EXCLUDED_CLASSES.contains(className)) {
                            return;
                        }

                        // Kiểm tra package path có chứa excluded package không
                        String relativePath = testDir.relativize(p).toString().replace(File.separatorChar, '/');
                        for (String excluded : EXCLUDED_PACKAGES) {
                            if (relativePath.contains("/" + excluded + "/")) {
                                return;
                            }
                        }

                        String commandName = deriveCommandName(className);
                        commands.add(new DiscoveredCommand(
                                commandName,
                                "Run " + className,
                                null,
                                className
                        ));
                        log.debug("Discovered test class: {} → /{}", className, commandName);
                    });
        } catch (IOException e) {
            log.error("Failed to scan test classes: {}", e.getMessage(), e);
        }

        return commands;
    }

    /**
     * Derive command name từ test class name.
     * CreateProductTest → createproduct
     * HomePageTest → homepage
     * AuthApiTest → authapi
     */
    private String deriveCommandName(String className) {
        String name = className;
        if (name.endsWith("Test")) {
            name = name.substring(0, name.length() - 4);
        }
        return name.toLowerCase();
    }

    /**
     * Scan test classes và group theo package name.
     * Ví dụ: com.automation.ui.LoginTest → group "ui", sub "login"
     *         com.automation.product.CreateProductTest → group "product", sub "createproduct"
     *
     * @return Map: groupName → List<DiscoveredCommand> (mỗi command = 1 test class trong group)
     */
    public Map<String, List<DiscoveredCommand>> scanGrouped(String frameworkPath) {
        Map<String, List<DiscoveredCommand>> groups = new HashMap<>();

        Path root = Path.of(frameworkPath);
        Path testDir = root.resolve("src/test/java");

        if (!Files.isDirectory(testDir)) {
            log.warn("Test directory not found: {}", testDir);
            return groups;
        }

        try (Stream<Path> paths = Files.walk(testDir)) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith("Test.java"))
                    .forEach(p -> {
                        String fileName = p.getFileName().toString();
                        String className = fileName.replace(".java", "");

                        if (EXCLUDED_CLASSES.contains(className)) {
                            return;
                        }

                        String relativePath = testDir.relativize(p).toString().replace(File.separatorChar, '/');
                        for (String excluded : EXCLUDED_PACKAGES) {
                            if (relativePath.contains("/" + excluded + "/")) {
                                return;
                            }
                        }

                        // Extract group name: lấy package ngay trước test class
                        // Ví dụ: "com/automation/tests/ui/LoginTest.java" → group = "ui"
                        //         "com/automation/tests/product/CreateProductTest.java" → group = "product"
                        String[] pathParts = relativePath.split("/");
                        if (pathParts.length < 2) {
                            // Test class ở root package → skip, không group được
                            return;
                        }

                        // Group = thư mục cha gần nhất của test class
                        String groupName = pathParts[pathParts.length - 2];

                        // Skip nếu group trùng với excluded packages
                        if (EXCLUDED_PACKAGES.contains(groupName)) {
                            return;
                        }

                        String subCommandName = deriveCommandName(className);
                        DiscoveredCommand cmd = new DiscoveredCommand(
                                subCommandName,
                                "Run " + className,
                                null,
                                className
                        );

                        groups.computeIfAbsent(groupName, k -> new ArrayList<>()).add(cmd);
                        log.debug("Discovered grouped test: {} → group '{}', sub '{}'",
                                className, groupName, subCommandName);
                    });
        } catch (IOException e) {
            log.error("Failed to scan test classes for grouping: {}", e.getMessage(), e);
        }

        log.info("Discovered {} groups with {} total test classes",
                groups.size(), groups.values().stream().mapToInt(List::size).sum());

        return groups;
    }
}
