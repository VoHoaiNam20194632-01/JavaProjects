package com.automation.bot.discovery;

import com.automation.bot.allure.AllureReportGenerator;
import com.automation.bot.bot.BotMessageSender;
import com.automation.bot.command.AbstractTestCommand;
import com.automation.bot.config.TestRunnerProperties;
import com.automation.bot.notification.TelegramNotifier;
import com.automation.bot.parser.SurefireReportParser;
import com.automation.bot.runner.TestRunQueue;
import com.automation.bot.runner.TestRunner;
import com.automation.bot.session.UserSessionManager;

/**
 * Dynamic implementation của AbstractTestCommand.
 * Nhận name, description, profile, testClass qua constructor thay vì hardcode.
 * Kế thừa toàn bộ logic execute() từ AbstractTestCommand.
 *
 * KHÔNG có @Component — được tạo programmatically bởi DynamicCommandRegistrar.
 */
public class DynamicTestCommand extends AbstractTestCommand {

    private final String commandName;
    private final String commandDescription;
    private final String commandProfile;
    private final String commandTestClass;

    public DynamicTestCommand(String commandName,
                              String commandDescription,
                              String commandProfile,
                              String commandTestClass,
                              BotMessageSender messageSender,
                              UserSessionManager sessionManager,
                              TestRunnerProperties runnerProperties,
                              TestRunner testRunner,
                              TestRunQueue testRunQueue,
                              TelegramNotifier notifier,
                              SurefireReportParser reportParser,
                              AllureReportGenerator allureGenerator) {
        super(messageSender, sessionManager, runnerProperties, testRunner, testRunQueue,
                notifier, reportParser, allureGenerator);
        this.commandName = commandName;
        this.commandDescription = commandDescription;
        this.commandProfile = commandProfile;
        this.commandTestClass = commandTestClass;
    }

    @Override
    public String name() {
        return commandName;
    }

    @Override
    public String description() {
        return commandDescription;
    }

    @Override
    protected String profile() {
        return commandProfile;
    }

    @Override
    protected String testClass() {
        return commandTestClass;
    }
}
