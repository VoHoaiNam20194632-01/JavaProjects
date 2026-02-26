package com.automation.ui;

import com.automation.annotations.FrameworkAnnotation;
import com.automation.base.BaseTest;
import com.automation.enums.CategoryType;
import com.automation.pages.HomePage;
import io.qameta.allure.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("UI Tests")
@Feature("Home Page")
public class HomePageTest extends BaseTest {

    private HomePage homePage;

    @BeforeMethod(alwaysRun = true)
    public void loginAndNavigate() {
        homePage = loginWithSessionReuse();
    }

    @Test(description = "Verify home page is displayed after login")
    @FrameworkAnnotation(category = {CategoryType.SMOKE, CategoryType.REGRESSION},
            author = "Framework", description = "Verify home page displays correctly")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Home page displays after successful login")
    public void testHomePageDisplayed() {
        assertThat(homePage.isHomePageDisplayed())
                .as("Home page should be displayed")
                .isTrue();
    }

    @Test(description = "Verify home page URL after login")
    @FrameworkAnnotation(category = {CategoryType.REGRESSION},
            author = "Framework", description = "Verify home page URL")
    @Severity(SeverityLevel.NORMAL)
    @Story("Home page URL verification")
    public void testHomePageUrl() {
        String url = homePage.getCurrentPageUrl();

        assertThat(url)
                .as("URL should not be the login page")
                .doesNotContain("/login");
    }
}
