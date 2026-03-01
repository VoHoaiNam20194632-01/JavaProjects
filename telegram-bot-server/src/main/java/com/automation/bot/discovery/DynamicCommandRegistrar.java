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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Đăng ký dynamic commands lúc startup.
 *
 * Flow:
 * 1. @PostConstruct → gọi TestSuiteScanner.scanGrouped()
 * 2. Với mỗi group (package) → tạo GroupCommand chứa sub-commands
 * 3. Đăng ký GroupCommand vào CommandRegistry
 * 4. Static commands (SmokeCommand, RegressionCommand) vẫn ưu tiên nếu trùng tên
 */
@Slf4j
@Component
public class DynamicCommandRegistrar {

    private final TestSuiteScanner scanner;
    private final CommandRegistry commandRegistry;
    private final TestRunnerProperties runnerProperties;

    // Dependencies cần truyền cho GroupCommand
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

        // Scan grouped: package → list of test classes
        Map<String, List<DiscoveredCommand>> groups = scanner.scanGrouped(frameworkPath);

        int registered = 0;
        for (Map.Entry<String, List<DiscoveredCommand>> entry : groups.entrySet()) {
            String groupName = entry.getKey();
            List<DiscoveredCommand> commands = entry.getValue();

            // Build sub-command list
            List<GroupCommand.SubCommandInfo> subCommands = new ArrayList<>();
            for (DiscoveredCommand cmd : commands) {
                subCommands.add(new GroupCommand.SubCommandInfo(
                        cmd.getCommandName(),
                        cmd.getTestClass(),
                        cmd.getDescription()
                ));
            }

            GroupCommand groupCommand = new GroupCommand(
                    groupName,
                    subCommands,
                    messageSender,
                    sessionManager,
                    runnerProperties,
                    testRunner,
                    testRunQueue,
                    notifier,
                    reportParser,
                    allureGenerator
            );

            // CommandRegistry.registerCommand() sẽ skip nếu command đã tồn tại (static commands)
            commandRegistry.registerCommand(groupCommand);
            registered++;
        }

        log.info("Dynamic command registration complete: {} group commands registered", registered);
    }
}
