Feature: Treasure API Operations
  As an API user
  I want to manage treasures
  So that I can track my pirate loot

  Background:
    Given I am registered and logged in as a user

  @api
  Scenario: Get all treasures
    When I send a GET request to "/treasures"
    Then I should receive a 200 status code
    And the response should be a list of treasures
    And each treasure should have id, name, value, description, location and discovered fields

  @api
  Scenario: Get a specific treasure
    Given there is a treasure with the following details:
      | name        | Golden Compass      |
      | value       | 1000               |
      | description | A magical compass   |
      | location    | Mysterious Island  |
      | discovered  | false              |
    When I send a GET request to "/treasures/{id}" with the treasure id
    Then I should receive a 200 status code
    And the response should contain the treasure details

  @api
  Scenario: Create a new treasure
    When I send a POST request to "/treasures" with body:
      """
      {
        "name": "Silver Chalice",
        "value": 500,
        "description": "An ancient silver chalice",
        "location": "Dragon Island",
        "discovered": false
      }
      """
    Then I should receive a 201 status code
    And the response should contain the created treasure with an id
    And the created treasure should have name "Silver Chalice"

  @api
  Scenario: Update a treasure
    Given there is a treasure with the following details:
      | name        | Bronze Statue      |
      | value       | 300                |
      | description | An ancient statue  |
      | location    | Hidden Cave        |
      | discovered  | false              |
    When I send a PUT request to "/treasures/{id}" with the treasure id and body:
      """
      {
        "name": "Bronze Statue",
        "value": 350,
        "description": "An ancient statue with inscriptions",
        "location": "Hidden Cave",
        "discovered": true
      }
      """
    Then I should receive a 200 status code
    And the response should contain the updated treasure
    And the updated treasure should have value 350
    And the updated treasure should have discovered true

  @api
  Scenario: Delete a treasure
    Given there is a treasure with the following details:
      | name        | Rusty Key         |
      | value       | 50                |
      | description | An old rusty key  |
      | location    | Abandoned Ship    |
      | discovered  | true              |
    When I send a DELETE request to "/treasures/{id}" with the treasure id
    Then I should receive a 204 status code
    When I send a GET request to "/treasures/{id}" with the treasure id
    Then I should receive a 404 status code

  @api
  Scenario: Search treasures by name
    Given there are treasures with names:
      | Golden Chalice  |
      | Golden Compass  |
      | Silver Ring     |
    When I send a GET request to "/treasures/search" with query parameter "name" set to "Golden"
    Then I should receive a 200 status code
    And the response should be a list of treasures
    And all treasure names should contain "Golden"
    And the treasure list should not contain treasures with name containing "Silver"

  @api
  Scenario: Get discovered treasures
    Given there are treasures with discovered status:
      | Golden Chalice | true  |
      | Golden Compass | false |
      | Silver Ring    | true  |
    When I send a GET request to "/treasures" with query parameter "discovered" set to "true"
    Then I should receive a 200 status code
    And the response should be a list of treasures
    And all treasures should have discovered status true