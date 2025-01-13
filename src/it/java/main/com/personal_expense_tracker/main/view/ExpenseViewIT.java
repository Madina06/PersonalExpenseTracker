package main.com.personal_expense_tracker.main.view;


import com.personal_expense_tracker.main.controller.ExpenseController;
import com.personal_expense_tracker.main.model.Expense;
import com.personal_expense_tracker.main.repository.ExpenseRepository;
import com.personal_expense_tracker.main.view.ExpenseView;

import org.junit.*;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

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

    @BeforeClass
    public static void startContainer() {
        // Запускаем контейнер перед тестами
        postgresContainer.start();
    }

    @AfterClass
    public static void stopContainer() {
        // Останавливаем контейнер после тестов
        postgresContainer.stop();
    }

    @Before
    public void setUpDatabase() throws SQLException {
        // Подключаемся к базе данных в Docker
        connection = DriverManager.getConnection(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword());

        // Создаём таблицу для тестов
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS Expenses (" +
                    "id SERIAL PRIMARY KEY, " +
                    "description TEXT, " +
                    "category VARCHAR(100) NOT NULL, " +
                    "amount DECIMAL(10, 2) NOT NULL, " +
                    "date DATE NOT NULL)");
        }

        // Инициализируем репозиторий, контроллер и представление
        expenseRepository = new ExpenseRepository(connection);
        expenseController = new ExpenseController(expenseRepository);
        expenseView = new ExpenseView(expenseController);
    }

    @After
    public void tearDownDatabase() throws SQLException {
        // Очищаем таблицу после каждого теста
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("TRUNCATE TABLE Expenses");
        }
    }

    @Test
    public void testAddExpenseUpdatesTable() throws Exception {
        // Arrange: создаём новый расход
        Expense expense = new Expense();
        expense.setDescription("Dinner");
        expense.setCategory("Food");
        expense.setAmount(25.0);
        expense.setDate(LocalDate.of(2023, 5, 1));

        // Act: добавляем расход и обновляем таблицу
        expenseController.addExpense(expense);
        expenseView.refreshExpenseTable();

        // Assert: проверяем, что таблица обновилась
        DefaultTableModel tableModel = (DefaultTableModel) expenseView.expenseTable.getModel();
        assertEquals(1, tableModel.getRowCount());
        assertEquals("Dinner", tableModel.getValueAt(0, 1));
        assertEquals("Food", tableModel.getValueAt(0, 2));
        assertEquals(25.0, tableModel.getValueAt(0, 3));
        assertEquals("2023-05-01", tableModel.getValueAt(0, 4).toString());
    }

    @Test
    public void testUpdateExpenseUpdatesTable() throws Exception {
        // Arrange: добавляем расход
        Expense expense = new Expense();
        expense.setDescription("Old Expense");
        expense.setCategory("Old Category");
        expense.setAmount(50.0);
        expense.setDate(LocalDate.of(2023, 1, 1));
        expenseController.addExpense(expense);

        // Обновляем данные расхода
        Expense fetchedExpense = expenseController.getAllExpenses().get(0);
        fetchedExpense.setDescription("Updated Expense");
        fetchedExpense.setCategory("Updated Category");
        fetchedExpense.setAmount(150.0);
        fetchedExpense.setDate(LocalDate.of(2023, 6, 1));
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
    public void testDeleteExpenseUpdatesTable() throws Exception {
        // Arrange: добавляем расход
        Expense expense = new Expense();
        expense.setDescription("Expense to Delete");
        expense.setCategory("Category to Delete");
        expense.setAmount(200.0);
        expense.setDate(LocalDate.of(2023, 1, 1));
        expenseController.addExpense(expense);

        // Удаляем расход
        Expense fetchedExpense = expenseController.getAllExpenses().get(0);
        expenseController.deleteExpense(fetchedExpense.getId());
        expenseView.refreshExpenseTable();

        // Assert
        DefaultTableModel tableModel = (DefaultTableModel) expenseView.expenseTable.getModel();
        assertEquals(0, tableModel.getRowCount());
    }

    @Test
    public void testGetAllExpensesPopulatesTable() throws Exception {
        // Arrange: добавляем несколько расходов
        Expense expense1 = new Expense();
        expense1.setDescription("Expense 1");
        expense1.setCategory("Groceries");
        expense1.setAmount(100.0);
        expense1.setDate(LocalDate.of(2023, 1, 1));
        expenseController.addExpense(expense1);

        Expense expense2 = new Expense();
        expense2.setDescription("Expense 2");
        expense2.setCategory("Utilities");
        expense2.setAmount(200.0);
        expense2.setDate(LocalDate.of(2023, 2, 1));
        expenseController.addExpense(expense2);

        // Act
        expenseView.refreshExpenseTable();

        // Assert
        DefaultTableModel tableModel = (DefaultTableModel) expenseView.expenseTable.getModel();
        assertEquals(2, tableModel.getRowCount());

        // Проверяем первую строку
        assertEquals("Expense 1", tableModel.getValueAt(0, 1));
        assertEquals("Groceries", tableModel.getValueAt(0, 2));
        assertEquals(100.0, tableModel.getValueAt(0, 3));
        assertEquals("2023-01-01", tableModel.getValueAt(0, 4).toString());

        // Проверяем вторую строку
        assertEquals("Expense 2", tableModel.getValueAt(1, 1));
        assertEquals("Utilities", tableModel.getValueAt(1, 2));
        assertEquals(200.0, tableModel.getValueAt(1, 3));
        assertEquals("2023-02-01", tableModel.getValueAt(1, 4).toString());
    }
}
