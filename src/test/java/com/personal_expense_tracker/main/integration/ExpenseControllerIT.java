package com.personal_expense_tracker.main.integration;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExpenseControllerIT {

    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("expense_testdb")
                    .withUsername("madina")
                    .withPassword("m123");

    private Connection connection;


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
    public void clearTable() throws SQLException {
        // Clear the table before each test
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM Expenses");
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

            assertEquals(1, affectedRows, "One row should be inserted");

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                assertTrue(rs.next(), "Generated key should exist");
                int expenseId = rs.getInt(1);
                assertTrue(expenseId > 0, "Expense ID should be a positive integer");
            }
        }
    }

    @Test
    public void testReadExpense() throws SQLException {
        // Insert a test expense
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO Expenses (description, category, amount, date) " +
                    "VALUES ('Groceries', 'Shopping', 50.75, '2025-01-01')");
        }

        String selectQuery = "SELECT * FROM Expenses WHERE category = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            pstmt.setString(1, "Shopping");
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next(), "Result set should contain a record");
                assertEquals("Groceries", rs.getString("description"));
                assertEquals("Shopping", rs.getString("category"));
                assertEquals(50.75, rs.getBigDecimal("amount").doubleValue());
                assertEquals(Date.valueOf("2025-01-01"), rs.getDate("date"));
            }
        }
    }

    @Test
    public void testUpdateExpense() throws SQLException {
        // Insert a test expense
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO Expenses (description, category, amount, date) " +
                    "VALUES ('Old Description', 'Miscellaneous', 100.00, '2025-01-02')");
        }

        String updateQuery = "UPDATE Expenses SET description = ?, amount = ? WHERE category = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setString(1, "Updated Description");
            pstmt.setBigDecimal(2, new java.math.BigDecimal("150.00"));
            pstmt.setString(3, "Miscellaneous");
            int affectedRows = pstmt.executeUpdate();

            assertEquals(1, affectedRows, "One row should be updated");

            // Verify the update
            String selectQuery = "SELECT * FROM Expenses WHERE category = ?";
            try (PreparedStatement selectPstmt = connection.prepareStatement(selectQuery)) {
                selectPstmt.setString(1, "Miscellaneous");
                try (ResultSet rs = selectPstmt.executeQuery()) {
                    assertTrue(rs.next(), "Result set should contain a record");
                    assertEquals("Updated Description", rs.getString("description"));
                    assertEquals(150.00, rs.getBigDecimal("amount").doubleValue());
                }
            }
        }
    }

    @Test
    public void testDeleteExpense() throws SQLException {
        // Insert a test expense
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO Expenses (description, category, amount, date) " +
                    "VALUES ('Unnecessary Item', 'Luxury', 999.99, '2025-01-03')");
        }

        String deleteQuery = "DELETE FROM Expenses WHERE category = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setString(1, "Luxury");
            int affectedRows = pstmt.executeUpdate();

            assertEquals(1, affectedRows, "One row should be deleted");

            // Verify deletion
            String selectQuery = "SELECT * FROM Expenses WHERE category = ?";
            try (PreparedStatement selectPstmt = connection.prepareStatement(selectQuery)) {
                selectPstmt.setString(1, "Luxury");
                try (ResultSet rs = selectPstmt.executeQuery()) {
                    assertFalse(rs.next(), "Result set should be empty");
                }
            }
        }
    }


}
