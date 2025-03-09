Feature: Hardcore UI Challenges
  As an advanced user
  I want to interact with complex UI components
  So that I can test my automation skills

  @ui @hardcore
  Scenario: Complete Shadow DOM Challenge
    Given I am on the "shadow-dom" challenge page
    When I start the challenge
    And I complete Step 1 with name "Test User", email "test@example.com", and password "Password123"
    And I complete Step 2 with nested Shadow DOM interactions
    And I complete Step 3 with dynamic content
    And I complete Step 4 with Shadow DOM CAPTCHA
    Then I should receive a challenge code
    When I validate the solution with the code
    Then the validation should be successful

  @ui @hardcore
  Scenario: Complete Multi-Window Treasure Hunt
    Given I am on the "multi-window" challenge page
    When I start the treasure hunt
    And I navigate through all treasure windows collecting clues
    And I solve the final puzzle with the collected clues
    Then I should receive a treasure hunt completion code
    When I submit the treasure hunt code
    Then the validation should be successful

  @ui @hardcore
  Scenario: Complete Canvas Treasure Map Challenge
    Given I am on the "canvas-map" challenge page
    When I start the canvas challenge
    And I navigate the treasure map to find the X marks
    And I dig at all X marks on the map
    Then I should uncover the hidden treasure
    When I collect the treasure and get the code
    And I submit the treasure code
    Then the validation should be successful

  @ui @hardcore
  Scenario: Complete Iframe Inception Challenge
    Given I am on the "iframe-inception" challenge page
    When I start the iframe challenge
    And I navigate through all nested iframe levels
    And I collect all hidden keys in the iframes
    Then I should complete the inception challenge
    When I submit the collected keys
    Then the validation should be successful

  @ui @hardcore
  Scenario: Complete Time Warp Challenge
    Given I am on the "time-warp" challenge page
    When I start the time warp challenge
    And I set the browser time to "2001-01-01T12:00:00Z" to get the past artifact
    And I set the browser time to "2029-01-01T12:00:00Z" to get the future technology
    And I change the timezone to UTC+2 to solve the paradox
    Then I should collect all time codes
    When I submit the time codes
    Then the validation should be successful
    
  @ui @hardcore
  Scenario: Complete Reactive Chaos Challenge
    Given I am on the "reactive-chaos" challenge page
    When I start the reactive chaos challenge
    And I monitor multiple data streams simultaneously
    And I perform calculations on the captured values
    And I progress through all challenge steps
    Then I should receive a reactive chaos completion code
    When I submit the reactive chaos code
    Then the validation should be successful