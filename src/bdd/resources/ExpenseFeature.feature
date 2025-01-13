Feature: Expense Management

<<<<<<< HEAD
=======
  Background:
    Given The database contains a few expenses

>>>>>>> c57b429e11f3ff35ea6189b73561bd208d5c3be5
  Scenario: User opens the "Add Expense" dialog
    Given the main window is displayed
    When the user clicks the Add Expense button
    Then the Add Expense dialog should appear

    Given the Add Expense dialog is open
    When the user enters a description "Lunch", category "Food", amount "50", and date "2025-01-12"
    And the user clicks the Save button
    Then the expense should be saved with the correct description, category, amount, and date

<<<<<<< HEAD
 

=======
>>>>>>> c57b429e11f3ff35ea6189b73561bd208d5c3be5
