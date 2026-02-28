package com.automation.bot.discovery;

import com.automation.bot.allure.AllureReportGenerator;
import com.automation.bot.bot.BotMessageSender;
import com.automation.bot.command.CommandRegistry;
import com.automation.bot.config.TestRunnerProperties;
import com.automation.bot.notification.TelegramNotifier;
import com.automation.bot.parser.SurefireReportParser;
import com.automation.bot.runner.TestRunQueue;
import com.automation.bot.runner.TestRunner;
import com.automation.bot.session.UserSessionManager;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Đăng ký dynamic commands lúc startup.
 *
 * Flow:
 * 1. @PostConstruct → gọi TestSuiteScanner.scan()
 * 2. Lọc trùng tên với static commands đã đăng ký trong CommandRegistry
 * 3. Nếu trùng tên giữa profile và test class → profile thắng
 * 4. Đăng ký commands còn lại vào CommandRegistry
 */
@Slf4j
@Component
public class DynamicCommandRegistrar {

    private final TestSuiteScanner scanner;
    private final CommandRegistry commandRegistry;
    private final TestRunnerProperties runnerProperties;

    // Dependencies cần truyền cho DynamicTestCommand
    private final BotMessageSender messageSender;
    private final UserSessionManager sessionManager;
    private final TestRunner testRunner;
    private final TestRunQueue testRunQueue;
    private final TelegramNotifier notifier;
    private final SurefireReportParser reportParser;
    private final AllureReportGenerator allureGenerator;

    public DynamicCommandRegistrar(TestSuiteScanner scanner,
                                   CommandRegistry commandRegistry,
                                   TestRunnerProperties runnerProperties,
                                   BotMessageSender messageSender,
                                   UserSessionManager sessionManager,
                                   TestRunner testRunner,
                                   TestRunQueue testRunQueue,
                                   TelegramNotifier notifier,
                                   SurefireReportParser reportParser,
                                   AllureReportGenerator allureGenerator) {
        this.scanner = scanner;
        this.commandRegistry = commandRegistry;
        this.runnerProperties = runnerProperties;
        this.messageSender = messageSender;
        this.sessionManager = sessionManager;
        this.testRunner = testRunner;
        this.testRunQueue = testRunQueue;
        this.notifier = notifier;
        this.reportParser = reportParser;
        this.allureGenerator = allureGenerator;
    }

    @PostConstruct
    public void registerDynamicCommands() {
        String frameworkPath = runnerProperties.getFrameworkPath();
        if (frameworkPath == null || frameworkPath.isBlank()) {
            log.warn("bot.runner.framework-path not configured, skipping dynamic command discovery");
            return;
        }

        List<DiscoveredCommand> discovered = scanner.scan(frameworkPath);

        // Dedup: profile thắng test class nếu trùng commandName
        Map<String, DiscoveredCommand> deduped = new HashMap<>();
        for (DiscoveredCommand cmd : discovered) {
            String name = cmd.getCommandName();
            DiscoveredCommand existing = deduped.get(name);

            if (existing == null) {
                deduped.put(name, cmd);
            } else if (cmd.getProfile() != null && existing.getProfile() == null) {
                // Profile ưu tiên hơn test class
                deduped.put(name, cmd);
                log.debug("Profile '{}' overrides test class for command /{}", cmd.getProfile(), name);
            }
        }

        int registered = 0;
        for (DiscoveredCommand cmd : deduped.values()) {
            // CommandRegistry.registerCommand() sẽ skip nếu command đã tồn tại (static commands)
            DynamicTestCommand dynamicCommand = new DynamicTestCommand(
                    cmd.getCommandName(),
                    cmd.getDescription(),
                    cmd.getProfile(),
                    cmd.getTestClass(),
                    messageSender,
                    sessionManager,
                    runnerProperties,
                    testRunner,
                    testRunQueue,
                    notifier,
                    reportParser,
                    allureGenerator
            );

            commandRegistry.registerCommand(dynamicCommand);
            registered++;
        }

        log.info("Dynamic command registration complete: {} commands processed", registered);
    }
}
