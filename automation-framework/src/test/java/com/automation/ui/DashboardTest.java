package com.automation.ui;

import com.automation.annotations.FrameworkAnnotation;
import com.automation.base.BaseTest;
import com.automation.enums.CategoryType;
import com.automation.pages.DashboardPage;
import io.qameta.allure.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("UI Tests")
@Feature("Dashboard")
public class DashboardTest extends BaseTest {

    private DashboardPage dashboardPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAndNavigate() {
        loginWithSessionReuse();
        dashboardPage = new DashboardPage();
    }

    @Test(description = "Verify dashboard page is accessible after login")
    @FrameworkAnnotation(category = {CategoryType.SMOKE, CategoryType.REGRESSION},
            author = "Framework", description = "Verify dashboard displays after login")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Dashboard is accessible after authentication")
    public void testDashboardIsDisplayed() {
        assertThat(dashboardPage.isDashboardDisplayed())
                .as("Dashboard content area should be displayed")
                .isTrue();
    }

    @Test(description = "Verify dashboard page title")
    @FrameworkAnnotation(category = {CategoryType.REGRESSION},
            author = "Framework", description = "Verify dashboard title")
    @Severity(SeverityLevel.NORMAL)
    @Story("Dashboard page has correct title")
    public void testDashboardTitle() {
        String title = dashboardPage.getDashboardTitle();

        assertThat(title)
                .as("Dashboard page should have a title")
                .isNotEmpty();
    }
}
