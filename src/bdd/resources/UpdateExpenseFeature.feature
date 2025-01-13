Feature: Update Expense
  As a user
  I want to update an existing expense
  So that I can correct or modify the details of an expense

  Background:
    Given the database contains only one expense

  Scenario: Update the description of an expense
    Given the main window is displayed
#    Given the expense table is loaded with data
    When the user selects the first row in the expense table
    And clicks the Update Expense button
    And updates the Description field to "Description Updated"
    And clicks the Save button
    Then the expense table should show the updated Description "Description Updated" for the selected row
