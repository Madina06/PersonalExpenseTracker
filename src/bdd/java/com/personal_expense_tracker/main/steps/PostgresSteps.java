// PostgresSteps.java
package com.personal_expense_tracker.main.steps;

import com.personal_expense_tracker.main.model.Expense;
import com.personal_expense_tracker.main.repository.ExpenseRepository;
import com.personal_expense_tracker.main.utils.DatabaseConnection;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PostgresSteps {

    private Connection connection;
    private ExpenseRepository expenseRepository;

    @Before
    public void setUp() throws SQLException {
        connection = DatabaseConnection.connect();
        expenseRepository = new ExpenseRepository(connection);
        expenseRepository.createTableIfNotExists();
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM expenses");	
        }
    }
    
    @After
    public void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Given("The database contains a few expenses")
    public void theDatabaseContainsAFewExpenses() {
        addTestExpenseToDatabase("Lunch", 50.0, "Food", "2023-12-01");
        addTestExpenseToDatabase("Updated Lunch", 60.0, "Food", "2023-12-02");
    }

    @Then("The expense with description {string} should be added to the database")
    public void theExpenseShouldBeAddedToTheDatabase(String description) throws SQLException {
        List<Expense> expenses = expenseRepository.getAllExpenses();
        boolean found = expenses.stream().anyMatch(exp -> exp.getDescription().equals(description));
        assertThat(found).isTrue();
    }

    @Then("The expense with description {string} should be removed from the database")
    public void theExpenseShouldBeRemovedFromTheDatabase(String description) throws SQLException {
        List<Expense> expenses = expenseRepository.getAllExpenses();
        boolean found = expenses.stream().anyMatch(exp -> exp.getDescription().equals(description));
        assertThat(found).isFalse();
    }

    private void addTestExpenseToDatabase(String description, double amount, String category, String date) {
        try {
            Expense expense = new Expense();
            expense.setDescription(description);
            expense.setAmount(amount);
            expense.setCategory(category);
            expense.setDate(LocalDate.parse(date));
            expenseRepository.saveExpense(expense);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
