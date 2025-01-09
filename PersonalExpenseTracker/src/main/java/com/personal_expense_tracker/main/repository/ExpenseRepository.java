package com.personal_expense_tracker.main.repository;

import com.personal_expense_tracker.main.model.Expense;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseRepository {

    private final Connection connection;

    public ExpenseRepository(Connection connection) {
        this.connection = connection;
    }

    public void saveExpense(Expense expense) throws SQLException {
        String sql = "INSERT INTO Expenses (description, category, amount, date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, expense.getDescription());
            stmt.setString(2, expense.getCategory());
            stmt.setDouble(3, expense.getAmount());
            stmt.setDate(4, Date.valueOf(expense.getDate()));
            stmt.executeUpdate();
        }
    }

    public List<Expense> getAllExpenses() throws SQLException {
        String sql = "SELECT * FROM Expenses";
        List<Expense> expenses = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Expense expense = new Expense();
                expense.setId(rs.getInt("id"));
                expense.setDescription(rs.getString("description"));
                expense.setCategory(rs.getString("category"));
                expense.setAmount(rs.getDouble("amount"));
                expense.setDate(rs.getDate("date").toLocalDate());
                expenses.add(expense);
            }
        }
        return expenses;
    }

    public void updateExpense(Expense expense) throws SQLException {
        String sql = "UPDATE Expenses SET description = ?, category = ?, amount = ?, date = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, expense.getDescription());
            stmt.setString(2, expense.getCategory());
            stmt.setDouble(3, expense.getAmount());
            stmt.setDate(4, Date.valueOf(expense.getDate()));
            stmt.setInt(5, expense.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteExpense(int id) throws SQLException {
        String sql = "DELETE FROM Expenses WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
