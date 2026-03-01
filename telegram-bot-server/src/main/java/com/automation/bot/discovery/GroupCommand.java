package com.automation.bot.discovery;

import com.automation.bot.allure.AllureReportGenerator;
import com.automation.bot.bot.BotMessageSender;
import com.automation.bot.command.BotCommand;
import com.automation.bot.config.TestRunnerProperties;
import com.automation.bot.notification.TelegramNotifier;
import com.automation.bot.parser.SurefireReportParser;
import com.automation.bot.parser.model.TestCase;
import com.automation.bot.parser.model.TestSuite;
import com.automation.bot.runner.*;
import com.automation.bot.session.UserSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Grouped command — đại diện cho 1 package test classes.
 *
 * Ví dụ: GroupCommand "ui" chứa sub-commands: login, dashboard, homepage, checkout
 *
 * Behavior:
 * - /ui           → hiển thị danh sách sub-commands + chạy TẤT CẢ test classes trong group
 * - /ui login     → chạy LoginTest
 * - /ui login dev → chạy LoginTest trên env dev
 *
 * KHÔNG extend AbstractTestCommand vì logic execute khác (phân tích args thành sub-command + env).
 * Thay vào đó, compose với cùng dependencies và tái sử dụng TestRunQueue.submit().
 */
@Slf4j
public class GroupCommand implements BotCommand {

    /** Thông tin 1 sub-command trong group */
    public record SubCommandInfo(String name, String testClass, String description) {}

    private final String groupName;
    private final List<SubCommandInfo> subCommands;
    private final Map<String, SubCommandInfo> subCommandMap;

    // Dependencies (giống AbstractTestCommand)
    private final BotMessageSender messageSender;
    private final UserSessionManager sessionManager;
    private final TestRunnerProperties runnerProperties;
    private final TestRunner testRunner;
    private final TestRunQueue testRunQueue;
    private final TelegramNotifier notifier;
    private final SurefireReportParser reportParser;
    private final AllureReportGenerator allureGenerator;

    public GroupCommand(String groupName,
                        List<SubCommandInfo> subCommands,
                        BotMessageSender messageSender,
                        UserSessionManager sessionManager,
                        TestRunnerProperties runnerProperties,
                        TestRunner testRunner,
                        TestRunQueue testRunQueue,
                        TelegramNotifier notifier,
                        SurefireReportParser reportParser,
                        AllureReportGenerator allureGenerator) {
        this.groupName = groupName;
        this.subCommands = subCommands;
        this.subCommandMap = new LinkedHashMap<>();
        for (SubCommandInfo sub : subCommands) {
            this.subCommandMap.put(sub.name(), sub);
        }
        this.messageSender = messageSender;
        this.sessionManager = sessionManager;
        this.runnerProperties = runnerProperties;
        this.testRunner = testRunner;
        this.testRunQueue = testRunQueue;
        this.notifier = notifier;
        this.reportParser = reportParser;
        this.allureGenerator = allureGenerator;
    }

    @Override
    public String name() {
        return groupName;
    }

    @Override
    public String description() {
        return "Run all " + groupName + " tests (" + subCommands.size() + " tests)";
    }

    @Override
    public boolean isGroup() {
        return true;
    }

    @Override
    public Map<String, String> getSubCommands() {
        Map<String, String> result = new LinkedHashMap<>();
        for (SubCommandInfo sub : subCommands) {
            result.put(sub.name(), sub.description());
        }
        return result;
    }

    @Override
    public void execute(Message message, String args) {
        long chatId = message.getChatId();
        long userId = message.getFrom().getId();

        if (args == null || args.isBlank()) {
            // Không có args → hiển thị gợi ý + chạy tất cả
            sendGroupHelp(chatId);
            runAllTests(chatId, userId, null);
            return;
        }

        // Parse args: tách thành [subCommandName] [env]
        String[] parts = args.trim().split("\\s+", 2);
        String firstArg = parts[0].toLowerCase();
        String remainingArgs = parts.length > 1 ? parts[1].trim() : null;

        // Kiểm tra firstArg có match sub-command không
        SubCommandInfo sub = subCommandMap.get(firstArg);
        if (sub != null) {
            // Match sub-command → chạy test class cụ thể
            String env = resolveEnv(userId, remainingArgs);
            runSingleTest(chatId, userId, sub, env);
            return;
        }

        // firstArg không match sub-command → coi là env, chạy tất cả
        runAllTests(chatId, userId, firstArg);
    }

    /**
     * Hiển thị danh sách sub-commands cho group này.
     */
    private void sendGroupHelp(long chatId) {
        StringBuilder sb = new StringBuilder();
        sb.append("\uD83D\uDCC2 *").append(groupName.toUpperCase()).append("* — Running all tests\n\n");
        sb.append("Available sub-commands:\n");
        for (SubCommandInfo sub : subCommands) {
            sb.append("  \u2022 /").append(groupName).append(" ").append(sub.name())
              .append(" — ").append(sub.description()).append("\n");
        }
        sb.append("\nTip: /").append(groupName).append(" <name> [env]");
        messageSender.send(chatId, sb.toString());
    }

    /**
     * Chạy TẤT CẢ test classes trong group (dùng testClasses comma-separated).
     */
    private void runAllTests(long chatId, long userId, String envOverride) {
        String env = resolveEnv(userId, envOverride);
        String runId = UUID.randomUUID().toString().substring(0, 8);

        // Join tất cả test class names bằng dấu phẩy
        String allClasses = subCommands.stream()
                .map(SubCommandInfo::testClass)
                .collect(Collectors.joining(","));

        String label = groupName + " (all)";

        TestRunRequest request = TestRunRequest.builder()
                .runId(runId)
                .chatId(chatId)
                .userId(userId)
                .env(env)
                .testClasses(allClasses)
                .browser(runnerProperties.getDefaultBrowser())
                .headless(runnerProperties.isHeadless())
                .build();

        submitAndNotify(request, label, env);
    }

    /**
     * Chạy 1 test class cụ thể (sub-command).
     */
    private void runSingleTest(long chatId, long userId, SubCommandInfo sub, String env) {
        String runId = UUID.randomUUID().toString().substring(0, 8);
        String label = groupName + "/" + sub.name();

        TestRunRequest request = TestRunRequest.builder()
                .runId(runId)
                .chatId(chatId)
                .userId(userId)
                .env(env)
                .testClass(sub.testClass())
                .browser(runnerProperties.getDefaultBrowser())
                .headless(runnerProperties.isHeadless())
                .build();

        submitAndNotify(request, label, env);
    }

    /**
     * Submit request vào queue và xử lý callback (giống AbstractTestCommand.execute).
     */
    private void submitAndNotify(TestRunRequest request, String label, String env) {
        TestRunQueue.TestRunInfo runInfo = testRunQueue.submit(request, info -> {
            executeTestRun(info, label);
        });

        if (runInfo == null) {
            notifier.notifyQueueFull(request.getChatId());
            return;
        }

        notifier.notifyQueued(request.getChatId(), request.getRunId(), label, env);
    }

    /**
     * Thực thi test run — chạy trên worker thread (giống AbstractTestCommand.executeTestRun).
     */
    private void executeTestRun(TestRunQueue.TestRunInfo info, String label) {
        TestRunRequest request = info.getRequest();
        String runId = request.getRunId();

        try {
            info.setStatus(RunStatus.RUNNING);
            notifier.notifyRunning(request.getChatId(), label, request.getEnv());

            TestRunResult rawResult = testRunner.run(request);
            info.setStatus(rawResult.getStatus());

            String allureUrl = allureGenerator.generateReport();

            List<TestSuite> suites = reportParser.parseReports(runnerProperties.getFrameworkPath());
            List<TestCase> failedTests = reportParser.getFailedTests(suites);

            TestRunResult enrichedResult = reportParser.buildResult(
                    runId, suites, rawResult.getDuration(), allureUrl);

            notifier.notifyResult(request, enrichedResult, failedTests);

        } catch (Exception e) {
            log.error("[{}] Error executing test run: {}", runId, e.getMessage(), e);
            info.setStatus(RunStatus.FAILED);
            notifier.notifyError(request.getChatId(), label, e.getMessage());

        } finally {
            testRunQueue.removeRun(runId);
        }
    }

    private String resolveEnv(long userId, String envOverride) {
        if (envOverride != null && !envOverride.isBlank()) {
            return envOverride.trim().toLowerCase();
        }
        return sessionManager.getEnv(userId);
    }
}
