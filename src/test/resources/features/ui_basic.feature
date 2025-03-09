Feature: Basic UI Components
  As a user
  I want to interact with basic UI components
  So that I can use the application effectively

  @ui
  Scenario: Interact with text and buttons
    Given I am on the "all elements" page
    When I click the toggle text button
    Then the hidden text should be displayed
    When I click the toggle text button again
    Then the hidden text should be hidden

  @ui
  Scenario: Fill and submit a form
    Given I am on the "forms" page
    When I fill the simple form with
      | name     | John Doe           |
      | email    | john.doe@email.com |
      | password | Password123        |
      | comments | Test comment       |
    And I submit the form
    Then I should see a success message

  @ui
  Scenario: Handle alerts
    Given I am on the "alerts" page
    When I click the alert button
    Then I should see an alert with text "This is a test alert!"
    And I accept the alert
    When I click the confirm button
    Then I should see a confirm dialog with text "Do you confirm this action?"
    And I accept the confirm dialog
    Then I should see the confirm result "You confirmed the action"

  @ui
  Scenario: Drag and drop elements
    Given I am on the "drag-drop" page
    When I drag the treasure to the chest
    Then I should see a "Treasure successfully stored!" message
    When I drag a trash item to the bin
    Then the trash count should increase by 1

  @ui
  Scenario: Work with tables
    Given I am on the "tables" page
    When I sort the table by "Name" column
    Then the table should be sorted by "Name" in "ascending" order
    When I sort the table by "Name" column again
    Then the table should be sorted by "Name" in "descending" order
    When I filter the table with "gold"
    Then the table should only show items containing "gold"