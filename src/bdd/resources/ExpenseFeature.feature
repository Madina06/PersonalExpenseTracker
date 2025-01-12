Feature: Expense Management

  Background:
    Given The database contains a few expenses

  Scenario: Adding an expense
    When The Expense View is shown
    Given The user provides expense details with description "Lunch", amount "50.0", category "Food", and date "2023-12-01"
    When The user clicks the "Add Expense" button
    Then The expense with description "Lunch" should be added to the database
    And The displayed expenses should include the added expense

