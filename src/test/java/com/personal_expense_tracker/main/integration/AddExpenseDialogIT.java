package com.personal_expense_tracker.main.integration;

import com.personal_expense_tracker.main.controller.ExpenseController;
import com.personal_expense_tracker.main.model.Expense;
import com.personal_expense_tracker.main.repository.ExpenseRepository;
import com.personal_expense_tracker.main.view.AddExpenseDialog;
import com.personal_expense_tracker.main.view.ExpenseView;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AddExpenseDialogIT {

    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("expense_testdb")
                    .withUsername("madina")
                    .withPassword("m123");

    private Connection connection;
    private ExpenseController expenseController;
    private ExpenseView expenseView;

    @BeforeAll
    static void startContainer() {
        postgresContainer.start();
    }

    @AfterAll
    static void stopContainer() {
        postgresContainer.stop();
    }

    @BeforeAll
    public void setUpDatabase() throws SQLException {
        connection = DriverManager.getConnection(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword());

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE Expenses (" +
                    "id SERIAL PRIMARY KEY," +
                    "description TEXT," +
                    "category VARCHAR(100) NOT NULL," +
                    "amount DECIMAL(10, 2) NOT NULL," +
                    "date DATE NOT NULL)");
        }

        ExpenseRepository expenseRepository = new ExpenseRepository(connection);
        expenseController = new ExpenseController(expenseRepository);
        expenseView = new ExpenseView(expenseController);
    }

    @AfterAll
    public void tearDownDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS Expenses");
        }
        if (connection != null) {
            connection.close();
        }
    }

    @BeforeEach
    public void clearDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM Expenses");
        }
    }

    @Test
    void testAddExpenseDialogCreatesExpense() throws Exception {
        // Arrange
        AddExpenseDialog dialog = new AddExpenseDialog(expenseController, expenseView);

        // Simulate user input
        dialog.getDescriptionField().setText("New Expense");
        dialog.getAmountField().setText("100.0");
        dialog.getCategoryField().setText("Food");
        dialog.getDateField().setText("2023-12-31");

        // Act
        dialog.getSaveButton().doClick();

        // Assert
        DefaultTableModel tableModel = (DefaultTableModel) expenseView.expenseTable.getModel();
        assertEquals(1, tableModel.getRowCount());
        assertEquals("New Expense", tableModel.getValueAt(0, 1));
        assertEquals("Food", tableModel.getValueAt(0, 2));
        assertEquals(100.0, tableModel.getValueAt(0, 3));
        assertEquals("2023-12-31", tableModel.getValueAt(0, 4).toString());
    }

    @Test
    void testAddExpenseDialogValidationError() throws Exception {
        // Arrange
        AddExpenseDialog dialog = new AddExpenseDialog(expenseController, expenseView);

        // Simulate user input with invalid amount
        dialog.getDescriptionField().setText("Invalid Expense");
        dialog.getAmountField().setText("InvalidAmount"); // Invalid input
        dialog.getCategoryField().setText("Food");
        dialog.getDateField().setText("2023-12-31");

        // Act
        dialog.getSaveButton().doClick();

        // Assert: Verify that no expense was added
        DefaultTableModel tableModel = (DefaultTableModel) expenseView.expenseTable.getModel();
        assertEquals(0, tableModel.getRowCount());
    }

    @Test
    void testAddExpenseDialogUpdatesExistingExpense() throws Exception {
        // Arrange: Add an initial expense
        Expense expense = new Expense();
        expense.setDescription("Old Expense");
        expense.setCategory("Old Category");
        expense.setAmount(50.0);
        expense.setDate(java.time.LocalDate.of(2023, 1, 1));
        expenseController.addExpense(expense);

        // Fetch the inserted expense
        Expense fetchedExpense = expenseController.getAllExpenses().get(0);

        // Open dialog for updating expense
        AddExpenseDialog dialog = new AddExpenseDialog(expenseController, expenseView, fetchedExpense);

        // Simulate user input
        dialog.getDescriptionField().setText("Updated Expense");
        dialog.getAmountField().setText("150.0");
        dialog.getCategoryField().setText("Updated Category");
        dialog.getDateField().setText("2023-06-01");

        // Act
        dialog.getSaveButton().doClick();

        // Assert
        DefaultTableModel tableModel = (DefaultTableModel) expenseView.expenseTable.getModel();
        assertEquals(1, tableModel.getRowCount());
        assertEquals("Updated Expense", tableModel.getValueAt(0, 1));
        assertEquals("Updated Category", tableModel.getValueAt(0, 2));
        assertEquals(150.0, tableModel.getValueAt(0, 3));
        assertEquals("2023-06-01", tableModel.getValueAt(0, 4).toString());
    }
}
