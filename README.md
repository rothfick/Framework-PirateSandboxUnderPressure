<!-- README_PRESENTATION_START -->
<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=rect&height=140&color=0:111827,100:DC2626&text=Pirate%20Sandbox%20Under%20Pressure&fontColor=FFFFFF&fontSize=30&fontAlignY=42&desc=Advanced%20Java%20QA%20sandbox%20for%20hard%20UI%2C%20API%2C%20contracts%20and%20mocks&descAlignY=68&descSize=15" alt="Pirate Sandbox Under Pressure banner" />
</p>

<p align="center">
  <img alt="Java: 11" src="https://img.shields.io/badge/Java-11-007396?style=for-the-badge" /> <img alt="UI: Selenium" src="https://img.shields.io/badge/UI-Selenium-43B02A?style=for-the-badge" /> <img alt="API: RestAssured" src="https://img.shields.io/badge/API-RestAssured-2563EB?style=for-the-badge" /> <img alt="Contracts: Pact" src="https://img.shields.io/badge/Contracts-Pact-7C3AED?style=for-the-badge" /> <img alt="Mocks: WireMock" src="https://img.shields.io/badge/Mocks-WireMock-DC2626?style=for-the-badge" /> <img alt="BDD: Cucumber" src="https://img.shields.io/badge/BDD-Cucumber-23D96C?style=for-the-badge" />
</p>

<table>
  <tr><td><strong>Role signal</strong></td><td>Advanced SDET-style problem solving across UI, API and integrations</td></tr>
<tr><td><strong>What to inspect</strong></td><td>hardcore UI features, API clients, Pact/WireMock tests, utilities</td></tr>
<tr><td><strong>Best for</strong></td><td>Senior QA Automation, SDET, QA Lead technical screens</td></tr>
</table>

<!-- README_PRESENTATION_END -->

# Pirate Sandbox Under Pressure

Advanced Java QA automation sandbox for UI, API, contract, mock-service, and difficult browser-interaction scenarios.

This repository is a more ambitious automation playground than a basic Selenium framework. It combines BDD-style tests, UI automation, API clients, RestAssured validation, Pact contract tests, WireMock examples, Allure reporting, and specialized utilities for hard UI problems such as Shadow DOM, iframes, multi-window flows, canvas interactions, network throttling, and time-based behavior.

## What This Project Demonstrates

- Java 11 automation framework design;
- Cucumber BDD test organization;
- Selenium WebDriver UI automation;
- RestAssured API testing;
- JSON schema validation;
- Pact consumer/provider contract tests;
- WireMock service virtualization;
- TestNG execution;
- Allure reporting across UI/API/BDD layers;
- WebDriverManager-based browser setup;
- page-object organization for basic and advanced UI surfaces;
- custom utilities for canvas, windows, network, screenshots, and time;
- feature files grouped by API/UI complexity.

## Technology Stack

| Area | Tools |
|---|---|
| Language | Java 11 |
| Build | Maven |
| BDD | Cucumber 7 |
| Test runner | TestNG |
| UI automation | Selenium WebDriver 4 |
| Browser setup | WebDriverManager |
| API testing | RestAssured |
| Contract testing | Pact |
| Mock services | WireMock |
| Reporting | Allure |
| Test data | JavaFaker |
| Logging | Log4j |

## Repository Structure

```text
src/main/java/com/peliQAn/framework/
  api/
    AuthApiClient.java
    BaseApiClient.java
    TestCasesApiClient.java
    TestMethodsApiClient.java
    TreasureApiClient.java

  core/
    DriverFactory.java

  pages/basic/
    AlertsPage.java
    DragDropPage.java
    FormsPage.java
    IframesPage.java
    TablesPage.java
    WidgetsPage.java
    WindowsPage.java

  pages/hardcore/
    CanvasMapChallengePage.java
    IframeInceptionChallengePage.java
    MultiWindowChallengePage.java
    ReactiveChaosChallengePage.java
    ShadowDomChallengePage.java
    TimeWarpChallengePage.java

  utils/
    CanvasUtils.java
    MultiWindowUtils.java
    NetworkUtils.java
    ScreenshotUtils.java
    TimeUtils.java

src/test/java/com/peliQAn/framework/
  api/advanced/
  pact/
  runners/
  stepdefinitions/

src/test/resources/features/
  api_basic.feature
  api_advanced.feature
  api_treasures.feature
  ui_basic.feature
  ui_advanced.feature
  ui_hardcore.feature
```

## Test Areas

### Basic UI automation

Feature files and page objects cover standard web automation areas such as alerts, forms, tables, widgets, windows, and iframes.

### Hardcore UI automation

The project includes specialized page objects and step definitions for harder browser automation cases:

- canvas map interactions;
- nested iframes;
- multi-window workflows;
- reactive / unstable UI;
- Shadow DOM;
- time-warp or timing-sensitive flows;
- network throttling.

These areas are useful in interviews because they show knowledge beyond simple click-and-type tests.

### API automation

The API layer includes reusable clients and Cucumber runners for:

- basic API scenarios;
- advanced API scenarios;
- treasure/test-case/test-method API flows;
- RestAssured validation.

### Contract and mock testing

The repository includes Pact and WireMock examples, which demonstrates awareness of integration risks and service-level quality beyond the UI.

## Running Locally

Requirements:

- Java 11;
- Maven;
- browser available locally;
- any target API/UI endpoints configured in `src/test/resources/config/config.properties`.

Run all tests:

```bash
mvn test
```

Run with a specific TestNG suite:

```bash
mvn test -DsuiteXmlFile=src/test/resources/testng-ui.xml
mvn test -DsuiteXmlFile=src/test/resources/testng-api.xml
```

Serve Allure report:

```bash
mvn allure:serve
```

## What To Review First

1. `pom.xml` for the full QA automation stack.
2. `ui_hardcore.feature` for difficult UI scenarios.
3. `api_advanced.feature` for API coverage style.
4. `CanvasUtils.java`, `NetworkUtils.java`, and `MultiWindowUtils.java` for custom utilities.
5. `TreasureApiClient.java` for API client structure.
6. Pact and WireMock tests under `src/test/java/com/peliQAn/framework/api/advanced`.

## Recruiter Signal

This project is one of the strongest Java QA Automation signals in the portfolio because it goes beyond basic Selenium:

- UI + API + contract testing;
- BDD feature organization;
- advanced browser problem-solving;
- service virtualization;
- reusable framework utilities;
- reporting and diagnostics;
- practical SDET-style thinking.

It is relevant for Senior QA Automation, SDET, QA Lead, Java automation, API testing, contract testing, and framework ownership roles.
