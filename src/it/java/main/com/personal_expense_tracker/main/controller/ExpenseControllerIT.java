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
                    "description TEXT NOT NULL, " +
                    "category VARCHAR(100) NOT NULL CHECK (category <> ''), " +
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
    public void testCreateExpense_ValidData() throws SQLException {
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

    @Test(expected = SQLException.class)
    public void testCreateExpense_InvalidData() throws SQLException {
        String insertQuery = "INSERT INTO Expenses (description, category, amount, date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setString(1, null);
            pstmt.setString(2, "Food");
            pstmt.setBigDecimal(3, new java.math.BigDecimal("15.50"));
            pstmt.setDate(4, null);
            pstmt.executeUpdate();
        }
    }

    @Test
    public void testReadExpense_ValidData() throws SQLException {
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
    public void testUpdateExpense_ValidData() throws SQLException {
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
    public void testDeleteExpense_ValidId() throws SQLException {
        int expenseId;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO Expenses (description, category, amount, date) " +
                    "VALUES ('Test Description', 'Test Category', 100.0, '2025-01-01')");
            ResultSet rs = stmt.executeQuery("SELECT id FROM Expenses WHERE description = 'Test Description'");
            rs.next();
            expenseId = rs.getInt("id");
        }

        String deleteQuery = "DELETE FROM Expenses WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, expenseId);
            int affectedRows = pstmt.executeUpdate();

            assertEquals(1, affectedRows);
            String selectQuery = "SELECT * FROM Expenses WHERE id = ?";
            try (PreparedStatement selectPstmt = connection.prepareStatement(selectQuery)) {
                selectPstmt.setInt(1, expenseId);
                try (ResultSet rs = selectPstmt.executeQuery()) {
                    assertFalse(rs.next());
                }
            }
        }
    }

    @Test
    public void testDeleteExpense_InvalidId() throws SQLException {
        int nonExistentId = 99999;

        String deleteQuery = "DELETE FROM Expenses WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, nonExistentId);
            int affectedRows = pstmt.executeUpdate();

            assertEquals(0, affectedRows);
        }
    }

    @Test
    public void testDeleteExpense_InvalidId_Negative() throws SQLException {
        int invalidId = -1;

        String deleteQuery = "DELETE FROM Expenses WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, invalidId);
            int affectedRows = pstmt.executeUpdate();

            assertEquals(0, affectedRows);
        }
    }


    @Test
    public void testValidateEmptyCategory() throws SQLException {
        String insertQuery = "INSERT INTO Expenses (description, category, amount, date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setString(1, "Test Description");
            pstmt.setString(2, ""); 
            pstmt.setBigDecimal(3, new java.math.BigDecimal("15.00"));
            pstmt.setDate(4, Date.valueOf(LocalDate.now()));
            pstmt.executeUpdate();
            fail("Expected SQLException for empty category");
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("violates check constraint") || e.getMessage().contains("violates not-null constraint"));
        }
    }

    @Test
    public void testNoExpensesFound() throws SQLException {
        String selectQuery = "SELECT * FROM Expenses";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectQuery)) {
            assertFalse(rs.next());
        }
    }

    @Test
    public void testDeleteNonExistentExpense() throws SQLException {
        String deleteQuery = "DELETE FROM Expenses WHERE category = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setString(1, "NonExistentCategory");
            int affectedRows = pstmt.executeUpdate();
            assertEquals(0, affectedRows);
        }
    }
}
