package main.com.personal_expense_tracker.main.controller;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;

public class ExpenseControllerIT {

    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>("postgres:15");

	private static final PostgreSQLContainer<?> postgresContainer =
            POSTGRE_SQL_CONTAINER
                    .withDatabaseName("expense_testdb")
                    .withUsername("madina")
                    .withPassword("m123");

    private Connection connection;

    @BeforeClass
    public static void startContainer() {
        postgresContainer.start();
    }

    @AfterClass
    public static void stopContainer() {
        postgresContainer.stop();
    }

    @Before
    public void setUpDatabase() throws SQLException {
        connection = DriverManager.getConnection(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword());
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
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO Expenses (description, category, amount, date) " +
                    "VALUES ('Groceries', 'Shopping', 50.75, '2025-01-01')");
        }

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

            assertEquals(1, affectedRows);

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
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO Expenses (description, category, amount, date) " +
                    "VALUES ('Unnecessary Item', 'Luxury', 999.99, '2025-01-03')");
        }

        String deleteQuery = "DELETE FROM Expenses WHERE category = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setString(1, "Luxury");
            int affectedRows = pstmt.executeUpdate();

            assertEquals(1, affectedRows);

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