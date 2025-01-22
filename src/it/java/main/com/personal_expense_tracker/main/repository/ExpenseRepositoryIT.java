package main.com.personal_expense_tracker.main.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import com.personal_expense_tracker.main.model.Expense;
import com.personal_expense_tracker.main.repository.ExpenseRepository;

public class ExpenseRepositoryIT {
    @SuppressWarnings("resource")
    
    @ClassRule
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("expense_testdb")
            .withUsername("madina")
            .withPassword("m123");
    
    private Connection connection;
    private ExpenseRepository expenseRepository;
    
    @Before
    public void setUp() throws Exception {
        connection = DriverManager.getConnection(postgresContainer.getJdbcUrl(), postgresContainer.getUsername(),
                postgresContainer.getPassword());
        expenseRepository = new ExpenseRepository(connection);
        expenseRepository.createTableIfNotExists();
    }
    
    @After
    public void tearDown() throws Exception {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE expenses");
        }
        connection.close();
    }
    
    @Test
    public void testSaveExpense() throws Exception {
        Expense expense = new Expense(1, "Groceries", "Food", 150.0, LocalDate.now());
        expenseRepository.saveExpense(expense);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM expenses")) {
            assertTrue(resultSet.next());
            assertEquals("Groceries", resultSet.getString("description"));
            assertEquals("Food", resultSet.getString("category"));
            assertEquals(150.0, resultSet.getDouble("amount"), 0.01);
        }
    }
    
    @Test
    public void testGetAllExpenses() throws Exception {
        expenseRepository.saveExpense(new Expense(1, "Groceries", "Food", 150.0, LocalDate.now()));
        expenseRepository.saveExpense(new Expense(2, "Transport", "Travel", 50.0, LocalDate.now()));
        List<Expense> expenses = expenseRepository.getAllExpenses();
        assertEquals(2, expenses.size());
        assertEquals("Groceries", expenses.get(0).getDescription());
        assertEquals("Transport", expenses.get(1).getDescription());
    }
    
    @Test
    public void testUpdateExpense() throws Exception {
        Expense expense = new Expense(1, "Groceries", "Food", 150.0, LocalDate.now());
        expenseRepository.saveExpense(expense);
        
        expense.setDescription("Groceries Updated");
        expense.setAmount(200.0);
        expenseRepository.updateExpense(expense);
        List<Expense> expenses = expenseRepository.getAllExpenses();
        assertEquals(1, expenses.size());
        assertEquals("Groceries Updated", expenses.get(0).getDescription());
        assertEquals(200.0, expenses.get(0).getAmount(), 0.01);
    }
    
    @Test
    public void testDeleteExpense() throws Exception {
        Expense expense = new Expense(1, "Groceries", "Food", 150.0, LocalDate.now());
        expenseRepository.saveExpense(expense);
        expenseRepository.deleteExpense(expense.getId());
        List<Expense> expenses = expenseRepository.getAllExpenses();
        assertTrue(expenses.isEmpty());
    }
}