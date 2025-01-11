Feature: Expense Management
  As a user
  I want to manage my expenses
  So that I can track my personal finances

  Scenario: Adding an expense
    Given the application is running
    When I add an expense with description "Dinner", amount "50.0", category "Food", and date "2024-05-01"
    Then the expense should be added to the database

  Scenario: Updating an expense
    Given the application is running
    And an expense with description "Dinner" exists in the database
    When I update the expense description to "Updated Dinner"
    Then the expense description should be updated in the database

  Scenario: Deleting an expense
    Given an expense with description "Updated Dinner" exists in the database
    When I delete the expense
    Then the expense should be removed from the database
