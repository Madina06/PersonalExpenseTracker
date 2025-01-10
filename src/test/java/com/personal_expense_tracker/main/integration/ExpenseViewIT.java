package com.personal_expense_tracker.main.integration;

import com.personal_expense_tracker.main.controller.ExpenseController;
import com.personal_expense_tracker.main.model.Expense;
import com.personal_expense_tracker.main.repository.ExpenseRepository;
import com.personal_expense_tracker.main.view.ExpenseView;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExpenseViewIT {

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
        // Запускаем контейнер перед тестами
        postgresContainer.start();
    }

    @AfterAll
    static void stopContainer() {
        // Останавливаем контейнер после тестов
        postgresContainer.stop();
    }

    @BeforeAll
    public void setUpDatabase() throws SQLException {
        // Connect to the Dockerized PostgreSQL database
        connection = DriverManager.getConnection(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword());
        // Create a test table
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE Expenses (\n" +
                    "    id SERIAL PRIMARY KEY,\n" +
                    "    description TEXT,\n" +
                    "    category VARCHAR(100) NOT NULL,\n" +
                    "    amount DECIMAL(10, 2) NOT NULL,\n" +
                    "    date DATE NOT NULL)");
        }
        
        // Initialize repository, controller, and view
        ExpenseRepository expenseRepository = new ExpenseRepository(connection);
        expenseController = new ExpenseController(expenseRepository);
        expenseView = new ExpenseView(expenseController);
    }

    @AfterAll
    public void tearDownDatabase() throws SQLException {
        // Drop the test table
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS Expenses");
        }
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void testAddExpenseUpdatesTable() throws Exception {
        // Arrange: Создаём новый расход
    	Expense expense = new Expense();
        expense.setDescription("Dinner");
        expense.setCategory("Food");
        expense.setAmount(25.0);
        expense.setDate(java.time.LocalDate.of(2023, 5, 1));

        // Act: Добавляем расход и обновляем таблицу
        expenseController.addExpense(expense);
        expenseView.refreshExpenseTable();


        // Assert: Проверяем, что таблица обновилась
        DefaultTableModel tableModel = (DefaultTableModel) expenseView.expenseTable.getModel();
        assertEquals(1, tableModel.getRowCount()); // Убедитесь, что здесь корректное значение
        assertEquals("Dinner", tableModel.getValueAt(0, 1));
        assertEquals("Food", tableModel.getValueAt(0, 2));
        assertEquals(25.0, tableModel.getValueAt(0, 3));
        assertEquals("2023-05-01", tableModel.getValueAt(0, 4).toString());
    }




}
