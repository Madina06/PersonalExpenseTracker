Feature: Expense Management through GUI

  Scenario: Add an expense through the GUI
    Given the application is running
    When the user clicks the "Add Expense" button
    And the user enters "Lunch" as the description, "Food" as the category, "50.0" as the amount, and "2025-01-12" as the date
    And the user clicks the dialog "Save" button
    Then a confirmation dialog with message "Expense added successfully!" should appear
    And the expense table should contain 1 row
    And the row should display "Lunch", "Food", "50.0", "2025-01-12"

  Scenario: Update an expense through the GUI
    Given the application is running
    When the user selects the expense with description "Lunch"
    And the user clicks the "Update Expense" button
    And the user updates the description to "Dinner", category to "Entertainment", amount to "70.0", and date to "2025-01-15"
    And the user clicks the dialog "Save" button
    Then a confirmation dialog with message "Expense updated successfully!" should appear
    And the row should display "Dinner", "Entertainment", "70.0", "2025-01-15"

  Scenario: Delete an expense through the GUI
    Given the application is running
    When the user selects the expense with description "Dinner"
    And the user clicks the "Delete Expense" button
    Then the expense table should be empty
