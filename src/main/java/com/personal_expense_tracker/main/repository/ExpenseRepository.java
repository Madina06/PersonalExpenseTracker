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

    public void createTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS expenses (" +
                "id SERIAL PRIMARY KEY, " +
                "description TEXT, " +
                "category VARCHAR(100) NOT NULL, " +
                "amount DECIMAL(10, 2) NOT NULL, " +
                "date DATE NOT NULL)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void saveExpense(Expense expense) throws SQLException {
        String sql;

        // Проверяем, передан ли id
        if (expense.getId() != 0 && expense.getId() > 0) {
            // Если id передан, используем его в запросе
            sql = "INSERT INTO Expenses (id, description, category, amount, date) VALUES (?, ?, ?, ?, ?)";
        } else {
            // Если id не передан, база данных сгенерирует его автоматически
            sql = "INSERT INTO Expenses (description, category, amount, date) VALUES (?, ?, ?, ?)";
        }
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Если id передан, устанавливаем его
            if (expense.getId() != 0 && expense.getId() > 0) {
                stmt.setLong(1, expense.getId());
                stmt.setString(2, expense.getDescription());
                stmt.setString(3, expense.getCategory());
                stmt.setDouble(4, expense.getAmount());
                stmt.setDate(5, Date.valueOf(expense.getDate()));
            } else {
                // Если id не передан, устанавливаем только остальные параметры
                stmt.setString(1, expense.getDescription());
                stmt.setString(2, expense.getCategory());
                stmt.setDouble(3, expense.getAmount());
                stmt.setDate(4, Date.valueOf(expense.getDate()));
            }
            stmt.executeUpdate();
        }
//        String sql = "INSERT INTO Expenses (description, category, amount, date) VALUES (?, ?, ?, ?)";
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setString(1, expense.getDescription());
//            stmt.setString(2, expense.getCategory());
//            stmt.setDouble(3, expense.getAmount());
//            stmt.setDate(4, Date.valueOf(expense.getDate()));
//            stmt.executeUpdate();
//        }
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
