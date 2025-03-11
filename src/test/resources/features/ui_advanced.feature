Feature: Advanced UI Components
  As a user
  I want to interact with advanced UI components
  So that I can use more complex features of the application

  @ui
  Scenario: Work with iframes
    Given I am on the "iframes" page
    When I switch to the simple iframe
    And I enter "Test text in iframe" in the iframe input
    And I click the button in the iframe
    Then I should see "Button clicked" text in the iframe
    When I switch back to the main content
    And I navigate through nested iframes
    Then I should see "Nested iframe text" in the nested iframe

  @ui
  Scenario: Work with windows
    Given I am on the "windows" page
    When I open a new window
    Then I should see a new window with title "New Window"
    When I fill the form in the new window
    And I close the window and switch back to the parent
    Then I should be back on the windows page
    When I enter "Test data" for transfer
    And I send data to a new window
    Then the new window should show "Test data" as received data

  @ui
  Scenario: Work with widgets
    Given I am on the "widgets" page
    When I select date "2023-12-25" from the datepicker
    Then the selected date should be "December 25, 2023"
    When I set the slider value to 75
    Then the slider value should be 75
    When I enter "app" in the autocomplete input
    And I select the first autocomplete suggestion
    Then the autocomplete result should contain "apple"

  @ui
  Scenario: Handle dynamic content
    Given I am on the "all elements" page
    When I click the load data button
    Then I should see the data items load
    And there should be at least 3 data items

  @ui
  Scenario: Manipulate tables
    Given I am on the "tables" page
    When I navigate to page 2 of the paginated table
    Then I should see different data on page 2
    When I change the page size to 10
    Then I should see 10 items per page
    When I edit cell at row 0, column a with value "Updated Data"
    And I save the edits
    Then I should see a success message for the edit
    
  @ui
  Scenario: Test with network throttling conditions
    Given I am on the "all elements" page
    When I enable network throttling with "Slow 3G" preset
    And I click the load data button
    Then I should see the loading indicator
    And the data should load within a reasonable time
    When I disable network throttling
    And I reload the page
    Then the data should load much faster