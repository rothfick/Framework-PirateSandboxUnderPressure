# PiratePeliQAn Test Automation Framework

A comprehensive test automation framework for the PeliQAn Sandbox application, built to test both UI and API components with a special focus on challenging test scenarios.

## Features

- **UI Testing**: Selenium-based with advanced Shadow DOM, iframes, and canvas support
- **API Testing**: RestAssured-based client for comprehensive API testing
- **Contract Testing**: Pact implementation for consumer-driven contract testing
- **BDD Style**: Cucumber integration for behavior-driven development
- **Reporting**: Allure reports for beautiful test reporting
- **Parallel Execution**: Multithreaded test execution
- **Edge Cases**: Support for testing complex edge cases

## Project Structure

```
PiratePeliQAnFramework/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── peliQAn/
│   │   │           └── framework/
│   │   │               ├── api/                # API client classes
│   │   │               │   ├── AuthApiClient.java
│   │   │               │   ├── BaseApiClient.java
│   │   │               │   ├── TestCasesApiClient.java
│   │   │               │   ├── TestMethodsApiClient.java
│   │   │               │   └── TreasureApiClient.java
│   │   │               ├── config/             # Configuration classes
│   │   │               │   └── PropertyManager.java
│   │   │               ├── core/               # Core framework classes
│   │   │               │   └── DriverFactory.java
│   │   │               ├── pages/              # Page Object classes
│   │   │               │   ├── BasePage.java   # Base Page Object
│   │   │               │   ├── basic/          # Basic UI components
│   │   │               │   │   ├── AlertsPage.java
│   │   │               │   │   ├── AllElementsPage.java
│   │   │               │   │   ├── DragDropPage.java
│   │   │               │   │   ├── FormsPage.java
│   │   │               │   │   ├── IframesPage.java
│   │   │               │   │   ├── TablesPage.java
│   │   │               │   │   ├── WidgetsPage.java
│   │   │               │   │   └── WindowsPage.java
│   │   │               │   └── hardcore/       # Hardcore UI challenges
│   │   │               │       ├── CanvasMapChallengePage.java
│   │   │               │       ├── IframeInceptionChallengePage.java
│   │   │               │       ├── MultiWindowChallengePage.java
│   │   │               │       ├── ReactiveChaosChallengePage.java
│   │   │               │       ├── ShadowDomChallengePage.java
│   │   │               │       └── TimeWarpChallengePage.java
│   │   │               └── utils/              # Utility classes
│   │   │                   ├── CanvasUtils.java
│   │   │                   ├── MultiWindowUtils.java
│   │   │                   ├── NetworkUtils.java
│   │   │                   ├── ScreenshotUtils.java
│   │   │                   └── TimeUtils.java
│   │   └── resources/                          # Resources
│   │       └── log4j2.xml                      # Logging configuration
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── peliQAn/
│       │           └── framework/
│       │               ├── api/                # API test classes
│       │               │   └── advanced/       # Advanced API testing
│       │               │       ├── AdvancedRestAssuredTest.java
│       │               │       ├── pact/       # Advanced Pact tests
│       │               │       │   ├── AdvancedPactConsumerTest.java
│       │               │       │   └── AdvancedPactProviderTest.java
│       │               │       └── wiremock/   # WireMock tests
│       │               │           └── WireMockTest.java 
│       │               ├── pact/               # Basic Pact tests
│       │               │   ├── TreasureConsumerPactTest.java
│       │               │   └── TreasureProviderPactTest.java
│       │               ├── runners/            # TestNG runners
│       │               │   ├── AdvancedApiTestRunner.java
│       │               │   ├── ApiMethodsTest.java
│       │               │   ├── ComprehensiveIntegrationTest.java
│       │               │   ├── CucumberTestRunner.java
│       │               │   ├── HardcoreChallengesTestRunner.java
│       │               │   ├── ShadowDomChallengeTest.java
│       │               │   └── TreasureApiTest.java
│       │               └── stepdefinitions/    # Cucumber step definitions
│       │                   ├── AdvancedApiSteps.java
│       │                   ├── ApiTreasureSteps.java
│       │                   ├── BasicApiSteps.java
│       │                   ├── CanvasMapSteps.java
│       │                   ├── Hooks.java
│       │                   ├── IframeInceptionSteps.java
│       │                   ├── MultiWindowSteps.java
│       │                   ├── ReactiveChaosSteps.java
│       │                   ├── ShadowDomSteps.java
│       │                   └── TimeWarpSteps.java
│       └── resources/
│           ├── config/                         # Test configuration
│           │   └── config.properties
│           └── features/                       # Cucumber feature files
│               ├── api_advanced.feature        # Advanced API scenarios
│               ├── api_basic.feature           # Basic API scenarios
│               ├── api_treasures.feature       # Treasure API scenarios
│               ├── ui_advanced.feature         # Advanced UI scenarios
│               ├── ui_basic.feature            # Basic UI scenarios
│               └── ui_hardcore.feature         # Hardcore UI scenarios
└── pom.xml                                     # Maven build file
```

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven
- Chrome, Firefox, Edge, or Safari browser

### Installation

1. Clone the repository:
```bash
git clone https://github.com/rothfick/Framework-PirateSandboxUnderPressure.git
cd Framework-PirateSandboxUnderPressure
```

2. Install dependencies:
```bash
mvn clean install -DskipTests
```

### Configuration

Configuration is managed through `src/test/resources/config/config.properties`. Key properties include:

- `browser`: Target browser (chrome, firefox, edge, safari)
- `headless`: Run in headless mode (true, false)
- `app.baseUrl`: Base URL of the application under test
- `api.baseUrl`: Base URL for API tests
- `api.timeout`: Timeout for API requests in seconds
- `screenshot.on.failure`: Take screenshots on test failure (true, false)

## Running Tests

### Run all tests:
```bash
mvn clean test
```

### Run UI tests only:
```bash
mvn clean test -Dcucumber.filter.tags="@ui"
# or use the dedicated TestNG XML file
mvn clean test -DsuiteXmlFile=testng-ui.xml
```

### Run API tests only:
```bash
mvn clean test -Dcucumber.filter.tags="@api"
# or use the dedicated TestNG XML file
mvn clean test -DsuiteXmlFile=testng-api.xml
```

### Run advanced API tests only:
```bash
mvn clean test -Dtest=AdvancedApiTestRunner
```

### Run WireMock tests only:
```bash
mvn clean test -Dtest=APIWireMockTestRunner
```

### Run Pact contract tests only:
```bash
mvn clean test -Dtest=APIPactTestRunner
```

### Run UI basic or advanced tests:
```bash
mvn clean test -Dtest=UIBasicTestRunner
mvn clean test -Dtest=UIAdvancedTestRunner
```

### Run hardcore challenge tests only:
```bash
mvn clean test -Dcucumber.filter.tags="@hardcore"
# or use specific test runner
mvn clean test -Dtest=HardcoreChallengesTestRunner
```

### Run tests by test group using ComprehensiveTestSuite:
```bash
# Choose one of the following groups:
# ui-basic, ui-advanced, ui-hardcore
# api-basic, api-advanced, api-treasures, api-wiremock, api-pact
# all-ui, all-api, all
mvn clean test -Dtest=ComprehensiveTestSuite -DtestGroup=api-advanced
```

### Run tests in parallel:
```bash
mvn clean test -DthreadCount=4
```

### Generate Allure report:
```bash
mvn allure:report
```

### View Allure report:
```bash
mvn allure:serve
```

## Test Implementations

### UI Testing
- **Basic UI Tests**: Covers basic UI components like forms, alerts, tables
- **Advanced UI Tests**: Handles advanced UI components like iframes, windows, widgets
- **Hardcore Challenges**: Special implementations for challenging UI scenarios:
  - Shadow DOM Challenge
  - Multi-Window Treasure Hunt
  - Canvas Treasure Map
  - Iframe Inception
  - Time Warp Challenge

### API Testing
- **Basic API Tests**: Tests HTTP methods, parameters, headers, cookies
- **Treasure API Tests**: Comprehensive CRUD operations for treasures
- **Authentication**: Testing auth endpoints and token handling
- **Advanced API Tests**:
  - **RestAssured Advanced**: JSON schema validation, response time validation, complex query parameters, etc.
  - **WireMock**: API mocking, fault injection, network delays, stateful behavior simulation
  - **Pact Advanced**: Comprehensive contract testing, provider state management, complex matchers

### Contract Testing
- **Consumer-Driven Contracts**: Defines expectations for API interactions
- **Provider Verification**: Ensures API implementations meet contract requirements
- **Advanced Contract Techniques**:
  - Type-based matchers for flexible contracts
  - Provider state management for stateful testing
  - Error response contract validation
  - Bulk operation contract testing

## Edge Cases

The framework includes implementations for 15 edge cases for both UI and API testing:

### UI Edge Cases
1. Shadow DOM element interaction
2. Nested iframes navigation
3. Canvas element manipulation
4. Multiple windows handling
5. Time manipulation in browser
6. Dynamic element loading with variable delays
7. Drag and drop operations
8. Table sorting and filtering
9. Form validation with various input formats
10. Alert and dialog handling with timeout conditions
11. Reactive UI with rapidly changing values
12. Network throttling conditions
13. Keyboard and mouse event combinations
14. File upload and manipulation
15. Long-running operations and progress monitoring

### API Edge Cases
1. JSON schema validation of API responses
2. Response time validation and performance thresholds
3. Sequence of dependent API requests with data chaining
4. Complex query parameters and filtering
5. Error response validation (400, 404, 401, 403, 500)
6. Mock API responses with WireMock
7. Network fault and delay simulation
8. Stateful behavior testing with scenarios
9. Dynamic response generation based on requests
10. Request body validation and matching
11. Contract testing with type-based matchers
12. Provider state management in contract tests
13. Bulk operations and batch processing
14. HTTP header and proxy manipulation
15. Response templating and transformation

## Best Practices

- Follow Page Object Model pattern for UI tests
- Keep step definitions small and focused
- Use feature files to describe business scenarios
- Write independent, atomic tests
- Use appropriate tags to categorize tests
- Add descriptive comments and documentation

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Maintainers

- The PeliQAn Team