Feature: Basic API Operations
  As an API user
  I want to test basic API functionality
  So that I can ensure API endpoints work correctly

  @api
  Scenario: Test HTTP Methods
    When I send a GET request to "/test/methods"
    Then I should receive a 200 status code
    And the response should have method "GET"
    When I send a POST request to "/test/methods" with body:
      """
      {
        "name": "John Doe",
        "age": 30
      }
      """
    Then I should receive a 200 status code
    And the response should have method "POST"
    And the response body should contain the sent data
    When I send a PUT request to "/test/methods" with body:
      """
      {
        "id": 1,
        "name": "Jane Doe"
      }
      """
    Then I should receive a 200 status code
    And the response should have method "PUT"
    When I send a DELETE request to "/test/methods"
    Then I should receive a 200 status code
    And the response should have method "DELETE"

  @api
  Scenario: Test Query Parameters
    When I send a GET request to "/test/params" with query parameters:
      | param1 | value1 |
      | param2 | 123    |
      | param3 | a,b,c  |
    Then I should receive a 200 status code
    And the response should contain parameter "param1" with value "value1"
    And the response should contain parameter "param2" with value "123"
    And the response should contain parameter "param3" with value "a,b,c"

  @api
  Scenario: Test Path Parameters
    When I send a GET request to "/test/params/testValue"
    Then I should receive a 200 status code
    And the response should contain path parameter with value "testValue"

  @api
  Scenario: Test Headers and Cookies
    When I send a GET request to "/test/headers" with headers:
      | X-Custom-Header1 | Header-Value-1 |
      | X-Custom-Header2 | Header-Value-2 |
    Then I should receive a 200 status code
    And the response should contain header "X-Custom-Header1" with value "Header-Value-1"
    When I send a GET request to "/test/set-cookie"
    Then I should receive a cookie named "test-cookie"
    When I send a GET request to "/test/cookies" with the received cookie
    Then I should receive a 200 status code
    And the response should confirm the cookie was received

  @api
  Scenario: Test Content Types
    When I send a GET request to "/test/content-types/json"
    Then I should receive a 200 status code
    And the response content type should be "application/json"
    When I send a GET request to "/test/content-types/xml"
    Then I should receive a 200 status code
    And the response content type should be "application/xml"
    When I send a GET request to "/test/content-types/text"
    Then I should receive a 200 status code
    And the response content type should be "text/plain"