package main.com.personal_expense_tracker.main.view;

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
    private ExpenseRepository expenseRepository;


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
    
    @BeforeEach
    public void clearDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM Expenses");
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
    
    @Test
    void testUpdateExpenseUpdatesTable() throws Exception {
        // Arrange: Add an initial expense
        Expense expense = new Expense();
        expense.setDescription("Old Expense");
        expense.setCategory("Old Category");
        expense.setAmount(50.0);
        expense.setDate(java.time.LocalDate.of(2023, 1, 1));
        expenseController.addExpense(expense);

        // Fetch the inserted expense
        Expense fetchedExpense = expenseController.getAllExpenses().get(0);

        // Update the expense
        fetchedExpense.setDescription("Updated Expense");
        fetchedExpense.setCategory("Updated Category");
        fetchedExpense.setAmount(150.0);
        fetchedExpense.setDate(java.time.LocalDate.of(2023, 6, 1));
        expenseController.updateExpense(fetchedExpense);

        // Act
        expenseView.refreshExpenseTable();

        // Assert
        DefaultTableModel tableModel = (DefaultTableModel) expenseView.expenseTable.getModel();
        assertEquals(1, tableModel.getRowCount());
        assertEquals("Updated Expense", tableModel.getValueAt(0, 1));
        assertEquals("Updated Category", tableModel.getValueAt(0, 2));
        assertEquals(150.0, tableModel.getValueAt(0, 3));
        assertEquals("2023-06-01", tableModel.getValueAt(0, 4).toString());
    }
    
    @Test
    void testDeleteExpenseUpdatesTable() throws Exception {
        // Arrange: Add an expense to delete
        Expense expense = new Expense();
        expense.setDescription("Expense to Delete");
        expense.setCategory("Category to Delete");
        expense.setAmount(200.0);
        expense.setDate(java.time.LocalDate.of(2023, 1, 1));
        expenseController.addExpense(expense);

        // Fetch the inserted expense ID
        Expense fetchedExpense = expenseController.getAllExpenses().get(0);

        // Act: Delete the expense
        expenseController.deleteExpense(fetchedExpense.getId());
        expenseView.refreshExpenseTable();

        // Assert
        DefaultTableModel tableModel = (DefaultTableModel) expenseView.expenseTable.getModel();
        assertEquals(0, tableModel.getRowCount());
    }
    
    @Test
    void testGetAllExpensesPopulatesTable() throws Exception {
        // Arrange: Add multiple expenses
        Expense expense1 = new Expense();
        expense1.setDescription("Expense 1");
        expense1.setCategory("Category 1");
        expense1.setAmount(100.0);
        expense1.setDate(java.time.LocalDate.of(2023, 1, 1));
        expenseController.addExpense(expense1);

        Expense expense2 = new Expense();
        expense2.setDescription("Expense 2");
        expense2.setCategory("Category 2");
        expense2.setAmount(200.0);
        expense2.setDate(java.time.LocalDate.of(2023, 2, 1));
        expenseController.addExpense(expense2);

        // Act
        expenseView.refreshExpenseTable();

        // Assert
        DefaultTableModel tableModel = (DefaultTableModel) expenseView.expenseTable.getModel();
        assertEquals(2, tableModel.getRowCount());

        // Check first row
        assertEquals("Expense 1", tableModel.getValueAt(0, 1));
        assertEquals("Category 1", tableModel.getValueAt(0, 2));
        assertEquals(100.0, tableModel.getValueAt(0, 3));
        assertEquals("2023-01-01", tableModel.getValueAt(0, 4).toString());

        // Check second row
        assertEquals("Expense 2", tableModel.getValueAt(1, 1));
        assertEquals("Category 2", tableModel.getValueAt(1, 2));
        assertEquals(200.0, tableModel.getValueAt(1, 3));
        assertEquals("2023-02-01", tableModel.getValueAt(1, 4).toString());
    }


}
