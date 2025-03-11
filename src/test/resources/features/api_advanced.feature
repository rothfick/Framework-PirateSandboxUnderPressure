@api
Feature: Advanced API Testing Features
  As a QA engineer
  I want to perform advanced API tests
  So that I can ensure the API handles complex scenarios correctly

  Background:
    Given the API base URL is set
    And I am authenticated with valid credentials

  # RestAssured Advanced Tests
  Scenario: Validate API response against JSON schema
    When I create a new treasure with valid data
    Then the treasure should be created successfully
    And the response should match the defined JSON schema
    And I should clean up the created treasure

  Scenario: Verify API response times meet performance criteria
    When I request a list of all treasures
    Then the response time should be less than 3 seconds

  Scenario: Execute a sequence of dependent API requests
    When I create a new treasure as undiscovered
    And I mark the treasure as discovered
    Then I should find the treasure in the discovered treasures list
    And I should clean up the created treasure

  Scenario: Filter treasures using complex query parameters
    Given there are treasures with different properties
    When I search for treasures with "gold" in the name
    And I filter treasures with value between 1000 and 5000
    Then I should receive treasures matching all filter criteria
    And I should clean up the test treasures

  Scenario: Handle API error responses correctly
    When I request a non-existent treasure
    Then I should receive a not found error response
    When I submit invalid treasure data
    Then I should receive a validation error response

  # WireMock Tests
  Scenario: Mock API responses with WireMock
    Given the WireMock server is configured
    When I make a request to the mocked treasure endpoint
    Then I should receive the predefined mock response

  Scenario: Simulate network delays with WireMock
    Given the WireMock server is configured with network delays
    When I make a request to the delayed endpoint
    Then the response should be delayed by the configured time

  Scenario: Test API resilience with WireMock fault injection
    Given the WireMock server is configured with fault injection
    When I make a request to the faulty endpoint
    Then the client should handle the error appropriately

  Scenario: Test stateful behavior with WireMock
    Given the WireMock server is configured with stateful behavior
    When I change the state of a resource through the mock API
    Then subsequent requests should reflect the new state

  # Pact Contract Tests
  Scenario: Verify consumer contracts with Pact
    Given I have defined Pact contracts as a consumer
    When I verify against the provider API
    Then all contract expectations should be met

  Scenario: Test API with advanced contract matching rules
    Given I have defined type-based matchers in Pact contracts
    When I verify various data types in the response
    Then the contract verification should succeed

  Scenario: Verify error handling contracts
    Given I have contracts for error responses
    When I trigger error conditions against the provider
    Then the error responses should match the contracts