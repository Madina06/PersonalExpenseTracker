package com.personal_expense_tracker.main.steps;

import com.personal_expense_tracker.main.controller.ExpenseController;
import com.personal_expense_tracker.main.model.Expense;
import com.personal_expense_tracker.main.repository.ExpenseRepository;
import com.personal_expense_tracker.main.utils.DatabaseConnection;
import io.cucumber.java.en.*;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseSteps {

    private Connection connection;
    private ExpenseController expenseController;
    private Expense expense;

    @Given("the application is running")
    public void theApplicationIsRunning() throws Exception {
        connection = DatabaseConnection.connect();
        ExpenseRepository expenseRepository = new ExpenseRepository(connection);
        expenseController = new ExpenseController(expenseRepository);
    }

    @Given("I add an expense with description {string}, amount {string}, category {string}, and date {string}")
    public void iAddAnExpenseWithDescriptionAmountCategoryAndDate(String description, String amountStr, String category, String date) {
        try {
            double amount = Double.parseDouble(amountStr); // Convert string to double
            LocalDate expenseDate = LocalDate.parse(date); // Convert string to LocalDate
            Expense expense = new Expense();
            expense.setDescription(description);
            expense.setAmount(amount);
            expense.setCategory(category);
            expense.setDate(expenseDate);
            expenseController.addExpense(expense); // Add the expense
            
            System.out.println("Description: " + description);
            System.out.println("Amount: " + amount);
            System.out.println("Category: " + category);
            System.out.println("Date: " + expenseDate);

        } catch (NumberFormatException | DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid input format: " + e.getMessage());
        }
    }



    @Then("the expense should be added to the database")
    public void theExpenseShouldBeAddedToTheDatabase() {
        Expense fetchedExpense = expenseController.getAllExpenses().get(0);
        assertNotNull(fetchedExpense);
        assertEquals(expense.getDescription(), fetchedExpense.getDescription());
    }

    @Given("an expense with description {string} exists in the database")
    public void anExpenseExistsInTheDatabase(String description) {
        expense = new Expense();
        expense.setDescription(description);
        expense.setAmount(50.0);
        expense.setCategory("Food");
        expense.setDate(LocalDate.of(2023, 5, 1));
        expenseController.addExpense(expense);
    }

    @When("I update the expense description to {string}")
    public void iUpdateTheExpenseDescription(String newDescription) {
        expense.setDescription(newDescription);
        expenseController.updateExpense(expense);
    }

    @Then("the expense description should be updated in the database")
    public void theExpenseDescriptionShouldBeUpdatedInTheDatabase() {
        Expense fetchedExpense = expenseController.getAllExpenses().get(0);
        assertEquals(expense.getDescription(), fetchedExpense.getDescription());
    }

    @When("I delete the expense")
    public void iDeleteTheExpense() {
        expenseController.deleteExpense(expense.getId());
    }

    @Then("the expense should be removed from the database")
    public void theExpenseShouldBeRemovedFromTheDatabase() {
        assertTrue(expenseController.getAllExpenses().isEmpty());
    }
}
