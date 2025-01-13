package main.com.personal_expense_tracker.main.controller;


import org.junit.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.*;
import java.time.LocalDate;

import static org.junit.Assert.*;

public class ExpenseControllerIT {

    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("expense_testdb")
                    .withUsername("madina")
                    .withPassword("m123");

    private Connection connection;

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
    }

    @After
    public void tearDownDatabase() throws SQLException {
        // Очищаем таблицу после каждого теста
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("TRUNCATE TABLE Expenses");
        }
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    public void testCreateExpense() throws SQLException {
        String insertQuery = "INSERT INTO Expenses (description, category, amount, date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, "Lunch at Cafe");
            pstmt.setString(2, "Food");
            pstmt.setBigDecimal(3, new java.math.BigDecimal("15.50"));
            pstmt.setDate(4, Date.valueOf(LocalDate.now()));
            int affectedRows = pstmt.executeUpdate();

            assertEquals(1, affectedRows);

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                assertTrue(rs.next());
                int expenseId = rs.getInt(1);
                assertTrue(expenseId > 0);
            }
        }
    }

    @Test
    public void testReadExpense() throws SQLException {
        // Arrange: добавляем тестовые данные
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO Expenses (description, category, amount, date) " +
                    "VALUES ('Groceries', 'Shopping', 50.75, '2025-01-01')");
        }

        // Act: выполняем выборку
        String selectQuery = "SELECT * FROM Expenses WHERE category = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            pstmt.setString(1, "Shopping");
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next());
                assertEquals("Groceries", rs.getString("description"));
                assertEquals("Shopping", rs.getString("category"));
                assertEquals(50.75, rs.getBigDecimal("amount").doubleValue(), 0.001);
                assertEquals(Date.valueOf("2025-01-01"), rs.getDate("date"));
            }
        }
    }

    @Test
    public void testUpdateExpense() throws SQLException {
        // Arrange: добавляем тестовые данные
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO Expenses (description, category, amount, date) " +
                    "VALUES ('Old Description', 'Miscellaneous', 100.00, '2025-01-02')");
        }

        // Act: обновляем данные
        String updateQuery = "UPDATE Expenses SET description = ?, amount = ? WHERE category = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setString(1, "Updated Description");
            pstmt.setBigDecimal(2, new java.math.BigDecimal("150.00"));
            pstmt.setString(3, "Miscellaneous");
            int affectedRows = pstmt.executeUpdate();

            assertEquals(1, affectedRows);

            // Assert: проверяем обновление
            String selectQuery = "SELECT * FROM Expenses WHERE category = ?";
            try (PreparedStatement selectPstmt = connection.prepareStatement(selectQuery)) {
                selectPstmt.setString(1, "Miscellaneous");
                try (ResultSet rs = selectPstmt.executeQuery()) {
                    assertTrue(rs.next());
                    assertEquals("Updated Description", rs.getString("description"));
                    assertEquals(150.00, rs.getBigDecimal("amount").doubleValue(), 0.001);
                }
            }
        }
    }

    @Test
    public void testDeleteExpense() throws SQLException {
        // Arrange: добавляем тестовые данные
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO Expenses (description, category, amount, date) " +
                    "VALUES ('Unnecessary Item', 'Luxury', 999.99, '2025-01-03')");
        }

        // Act: удаляем данные
        String deleteQuery = "DELETE FROM Expenses WHERE category = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setString(1, "Luxury");
            int affectedRows = pstmt.executeUpdate();

            assertEquals(1, affectedRows);

            // Assert: проверяем удаление
            String selectQuery = "SELECT * FROM Expenses WHERE category = ?";
            try (PreparedStatement selectPstmt = connection.prepareStatement(selectQuery)) {
                selectPstmt.setString(1, "Luxury");
                try (ResultSet rs = selectPstmt.executeQuery()) {
                    assertFalse(rs.next());
                }
            }
        }
    }
}
