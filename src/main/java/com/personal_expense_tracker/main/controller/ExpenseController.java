package com.personal_expense_tracker.main.controller;

import com.personal_expense_tracker.main.model.Expense;
import com.personal_expense_tracker.main.repository.ExpenseRepository;

import java.sql.SQLException;
import java.util.List;

public class ExpenseController {

    private final ExpenseRepository expenseRepository;

    public ExpenseController(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    // Add a new expense
    public void addExpense(Expense expense) throws IllegalArgumentException {
        validateExpense(expense);
        try {
            expenseRepository.saveExpense(expense);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add expense: " + e.getMessage(), e);
        }
    }

    // Retrieve all expenses
    public List<Expense> getAllExpenses() {
        try {
            return expenseRepository.getAllExpenses();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch expenses: " + e.getMessage(), e);
        }
    }

    // Update an existing expense
    public void updateExpense(Expense expense) throws IllegalArgumentException {
        validateExpense(expense);
        try {
            expenseRepository.updateExpense(expense);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update expense: " + e.getMessage(), e);
        }
    }

 // Delete an expense by ID
    public void deleteExpense(int expenseId) {
        if (expenseId <= 0) {
            throw new IllegalArgumentException("Expense ID must be greater than 0");
        }
        try {
            expenseRepository.deleteExpense(expenseId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete expense: " + e.getMessage(), e);
        }
    }


    // Validate expense data
    private void validateExpense(Expense expense) throws IllegalArgumentException {
        if (expense.getDescription() == null || expense.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty.");
        }
        if (expense.getCategory() == null || expense.getCategory().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty.");
        }
        if (expense.getCategory().matches(".*\\d.*")) {
            throw new IllegalArgumentException("Category cannot contain numeric values.");
        }
        if (expense.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        if (expense.getDate() == null) {
            throw new IllegalArgumentException("Date cannot be null.");
        }
    }
}
