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
│   │   │               ├── api/             # API client classes
│   │   │               ├── config/          # Configuration classes
│   │   │               ├── core/            # Core framework classes
│   │   │               ├── pages/           # Page Object classes
│   │   │               │   ├── basic/       # Basic UI page objects
│   │   │               │   ├── advanced/    # Advanced UI page objects
│   │   │               │   └── hardcore/    # Hardcore challenge page objects
│   │   │               ├── pact/            # Pact contract testing classes
│   │   │               └── utils/           # Utility classes
│   │   └── resources/                       # Resources like log4j2.xml
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── peliQAn/
│       │           └── framework/
│       │               ├── runners/         # TestNG runners
│       │               ├── stepdefinitions/ # Cucumber step definitions
│       │               ├── pact/            # Pact test implementations
│       │               └── utils/           # Test utilities
│       └── resources/
│           ├── features/                    # Cucumber feature files
│           ├── data/                        # Test data
│           └── config/                      # Test configuration
└── pom.xml                                  # Maven build file
```

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven
- Chrome, Firefox, Edge, or Safari browser

### Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/PiratePeliQAnFramework.git
cd PiratePeliQAnFramework
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
```

### Run API tests only:
```bash
mvn clean test -Dcucumber.filter.tags="@api"
```

### Run hardcore challenge tests only:
```bash
mvn clean test -Dcucumber.filter.tags="@hardcore"
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

### Contract Testing
- **Consumer-Driven Contracts**: Defines expectations for API interactions
- **Provider Verification**: Ensures API implementations meet contract requirements

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
1. Handling large payload responses
2. Handling network timeouts and retries
3. Authentication token expiration and refresh
4. API rate limiting
5. Handling diverse content types
6. Request parameter validation
7. Concurrent API requests handling
8. Partial updates with PATCH
9. Error responses and status code validation
10. Header and cookie manipulation
11. File uploads through API
12. Search and filtering edge cases
13. Pagination with various page sizes
14. Data type validation and conversion
15. HTTP method combinations for the same resource

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