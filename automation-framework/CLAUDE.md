# Automation Framework — CLAUDE.md

## Overview
Selenium + TestNG automation framework cho **burgershop.io** admin portal.
Hỗ trợ cả UI testing (Page Object Model) và API testing (REST Assured).

## Tech Stack
- **Java 17** | **Selenium 4.41** | **TestNG 7.12** | **REST Assured 5.5**
- **Allure 2.33** (reporting) | **Owner 1.0.12** (config) | **Gson 2.13** (JSON)
- **WebDriverManager 6.3** | **Lombok 1.18** | **AssertJ 3.27** | **Log4j2 2.25**
- **Apache POI 5.3** (Excel) | **DataFaker 2.5** (test data)

## Project Structure
```
src/main/java/com/automation/
├── config/          # FrameworkConfig, ApiConfig, WaitConfig (Owner interfaces)
├── constants/       # FrameworkConstants, EndpointConstants
├── driver/          # DriverManager (ThreadLocal), DriverFactory
├── pages/           # BasePage, LoginPage, HomePage, DashboardPage
│   └── components/  # NavigationBar, Footer
├── api/             # builder/, services/, specs/, interceptors/, models/
├── enums/           # WaitStrategy, CategoryType, Environment, LogType
├── exceptions/      # FrameworkException, DriverInitException, ApiException...
├── listeners/       # TestListener, RetryAnalyzer, AnnotationTransformer
├── utils/           # WaitUtils, GsonUtils, CookieManager, ExcelReader...
└── reports/         # AllureManager

src/test/java/com/automation/
├── base/            # BaseTest (UI), BaseApiTest (API)
├── ui/              # LoginTest, HomePageTest, DashboardTest
├── api/             # AuthApiTest, UserApiTest
└── dataproviders/   # LoginDataProvider, UserDataProvider
```

## Key Patterns & Conventions

### Page Object Model
- Mọi page kế thừa `BasePage` → cung cấp `click()`, `type()`, `getText()`, `isDisplayed()`, `navigateTo()`
- Wait strategy qua enum: `WaitStrategy.CLICKABLE`, `VISIBLE`, `PRESENCE`, `NONE`
- Fluent API: `loginPage.enterEmail(e).clickLogin().enterPassword(p).clickContinue()`

### Configuration (Owner library)
- Config load theo thứ tự: **system props → env vars → ${env}.properties → config.properties**
- Chọn env: `-Denv=dev` (default: dev)
- File config: `src/main/resources/config/{dev,replica,prod}.properties`

### Driver Management
- `DriverManager` dùng `ThreadLocal<WebDriver>` → thread-safe cho parallel
- `DriverFactory` tạo Chrome/Firefox/Edge, hỗ trợ headless và remote

### Session Reuse (cookies + localStorage + sessionStorage)
- `BaseTest.loginWithSessionReuse()` → load session trước, nếu hết hạn thì login lại
- `CookieManager.saveSession()` / `loadSession()` — lưu/load cả 3: cookies, localStorage, sessionStorage
- App burgershop.io lưu auth token trong **sessionStorage** (không phải cookies hay localStorage)
- Phải đợi login redirect xong (URL không còn `/login`) rồi mới save session
- Files lưu tại `cookies/`: `cookies_dev.json`, `localStorage_dev.json`, `sessionStorage_dev.json`
- Bật/tắt qua `session.reuse=true/false` trong config hoặc `-Dsession.reuse=false`
- `LoginTest` KHÔNG dùng session reuse (cần test chính flow login)

### Test Structure
- UI tests: `@BeforeMethod` setup driver → login → test → `@AfterMethod` quit driver
- API tests: `BaseApiTest` tự lấy auth token, dùng `RequestBuilder` fluent API
- Data-driven: JSON (`GsonUtils`) hoặc Excel (`ExcelReader`) qua `@DataProvider`

### Reporting & Listeners
- Allure: `@Epic`, `@Feature`, `@Story`, `@Severity`, `@Step` trên mỗi test/method
- `TestListener`: auto screenshot on failure, attach vào Allure
- `RetryAnalyzer`: retry count từ config (default: 1)
- `@FrameworkAnnotation(category, author, description)`: metadata cho test filtering

## Commands
```bash
# Run all tests
mvn test

# Run by profile
mvn test -Psmoke          # smoke tests only
mvn test -Pregression     # full regression
mvn test -Papi            # API tests only

# Override config
mvn test -Dbrowser=firefox -Denv=dev -Dheadless=true
mvn test -Dsession.reuse=false   # force fresh login every test

# Allure report
mvn allure:serve
```

## Important Files
| File | Purpose |
|------|---------|
| `config/FrameworkConfig.java` | Tất cả config properties (browser, url, credentials...) |
| `config/config.properties` | Default values |
| `config/dev.properties` | Dev env credentials & URL |
| `driver/DriverFactory.java` | Tạo WebDriver theo browser type |
| `pages/BasePage.java` | Base methods cho mọi page object |
| `pages/LoginPage.java` | Login flow: email → Login → password → Continue |
| `base/BaseTest.java` | setUp/tearDown driver + `loginWithSessionReuse()` |
| `utils/CookieManager.java` | Save/load browser cookies cho session reuse |
| `listeners/TestListener.java` | Screenshot on failure + Allure integration |

## Notes
- Credentials nằm trong `dev.properties` (không commit lên git)
- `cookies/` đã được gitignore
- Target app: `https://admin-dev.burgershop.io`
- Login flow gồm 2 bước: nhập email → click Login → nhập password → click Continue
