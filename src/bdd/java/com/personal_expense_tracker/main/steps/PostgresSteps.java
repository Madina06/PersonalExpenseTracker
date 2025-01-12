package com.personal_expense_tracker.main.steps;

import com.personal_expense_tracker.main.model.Expense;
import com.personal_expense_tracker.main.repository.ExpenseRepository;
import com.personal_expense_tracker.main.utils.DatabaseConnection;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class PostgresSteps {

    private Connection connection;
    private ExpenseRepository expenseRepository;

    static final int EXPENSE_FIXTURE_1_ID = 100;
    static final String EXPENSE_FIXTURE_1_DESCRIPTION = "Description A";
    static final double EXPENSE_FIXTURE_1_AMOUNT = 1000000.0;
    static final String EXPENSE_FIXTURE_1_CATEGORY = "Category A";
    static final String EXPENSE_FIXTURE_1_DATE = "2025-01-11";

    static final int EXPENSE_FIXTURE_2_ID = 200;
    static final String EXPENSE_FIXTURE_2_DESCRIPTION = "Description B";
    static final double EXPENSE_FIXTURE_2_AMOUNT = 2000000.0;
    static final String EXPENSE_FIXTURE_2_CATEGORY = "Category B";
    static final String EXPENSE_FIXTURE_2_DATE = "2025-01-12";

    @Before
    public void setUp() throws SQLException {
        connection = DatabaseConnection.connect();
        expenseRepository = new ExpenseRepository(connection);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM expenses");
        }
    }

    @Given("The database contains a few expenses")
    public void the_database_contains_a_few_expenses() {
        addTestExpenseToDatabase(EXPENSE_FIXTURE_1_ID, EXPENSE_FIXTURE_1_DESCRIPTION,
                EXPENSE_FIXTURE_1_AMOUNT, EXPENSE_FIXTURE_1_CATEGORY, EXPENSE_FIXTURE_1_DATE);
        addTestExpenseToDatabase(EXPENSE_FIXTURE_2_ID, EXPENSE_FIXTURE_2_DESCRIPTION,
                EXPENSE_FIXTURE_2_AMOUNT, EXPENSE_FIXTURE_2_CATEGORY, EXPENSE_FIXTURE_2_DATE);
    }

    private void addTestExpenseToDatabase(int id,
                                          String description,
                                          double amount,
                                          String category,
                                          String date) {
        try {
            Expense expense = new Expense();
            expense.setId(id);
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
