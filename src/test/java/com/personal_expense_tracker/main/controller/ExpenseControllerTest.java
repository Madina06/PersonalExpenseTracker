package com.personal_expense_tracker.main.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.personal_expense_tracker.main.model.Expense;
import com.personal_expense_tracker.main.repository.ExpenseRepository;

public class ExpenseControllerTest {

    private ExpenseController expenseController;

    @Mock
    private ExpenseRepository mockExpenseRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        expenseController = new ExpenseController(mockExpenseRepository);
    }

    @Test
    public void testAddExpense_ValidExpense() throws SQLException {
        Expense expense = createValidExpense();
        doNothing().when(mockExpenseRepository).saveExpense(expense);

        expenseController.addExpense(expense);

        verify(mockExpenseRepository, times(1)).saveExpense(expense);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddExpense_NullDescription() {
        Expense expense = createValidExpense();
        expense.setDescription(null);

        expenseController.addExpense(expense);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddExpense_EmptyDescription() {
        Expense expense = createValidExpense();
        expense.setDescription("");

        expenseController.addExpense(expense);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddExpense_NullCategory() {
        Expense expense = createValidExpense();
        expense.setCategory(null);

        expenseController.addExpense(expense);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddExpense_EmptyCategory() {
        Expense expense = createValidExpense();
        expense.setCategory("");

        expenseController.addExpense(expense);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddExpense_CategoryWithNumbers() {
        Expense expense = createValidExpense();
        expense.setCategory("123Food");

        expenseController.addExpense(expense);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddExpense_NegativeAmount() {
        Expense expense = createValidExpense();
        expense.setAmount(-15.0);

        expenseController.addExpense(expense);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddExpense_ZeroAmount() {
        Expense expense = createValidExpense();
        expense.setAmount(0.0);

        expenseController.addExpense(expense);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddExpense_NullDate() {
        Expense expense = createValidExpense();
        expense.setDate(null);

        expenseController.addExpense(expense);
    }

    @Test(expected = RuntimeException.class)
    public void testAddExpense_SQLException() throws SQLException {
        Expense expense = createValidExpense();
        doThrow(new SQLException("Database error")).when(mockExpenseRepository).saveExpense(expense);

        expenseController.addExpense(expense);
    }

    @Test
    public void testGetAllExpenses_ValidData() throws SQLException {
        List<Expense> mockExpenses = Arrays.asList(createValidExpense(), createValidExpense());
        when(mockExpenseRepository.getAllExpenses()).thenReturn(mockExpenses);

        List<Expense> result = expenseController.getAllExpenses();

        assertEquals(2, result.size());
        verify(mockExpenseRepository, times(1)).getAllExpenses();
    }

    @Test
    public void testGetAllExpenses_EmptyList() throws SQLException {
        when(mockExpenseRepository.getAllExpenses()).thenReturn(Collections.emptyList());

        List<Expense> result = expenseController.getAllExpenses();

        assertTrue(result.isEmpty());
        verify(mockExpenseRepository, times(1)).getAllExpenses();
    }

    @Test(expected = RuntimeException.class)
    public void testGetAllExpenses_SQLException() throws SQLException {
        when(mockExpenseRepository.getAllExpenses()).thenThrow(new SQLException("Database error"));

        expenseController.getAllExpenses();
    }

    @Test
    public void testUpdateExpense_ValidExpense() throws SQLException {
        Expense expense = createValidExpense();
        doNothing().when(mockExpenseRepository).updateExpense(expense);

        expenseController.updateExpense(expense);

        verify(mockExpenseRepository, times(1)).updateExpense(expense);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateExpense_InvalidExpense() {
        Expense expense = createValidExpense();
        expense.setDescription("");

        expenseController.updateExpense(expense);
    }

    @Test(expected = RuntimeException.class)
    public void testUpdateExpense_SQLException() throws SQLException {
        Expense expense = createValidExpense();
        doThrow(new SQLException("Database error")).when(mockExpenseRepository).updateExpense(expense);

        expenseController.updateExpense(expense);
    }

    @Test
    public void testDeleteExpense_ValidId() throws SQLException {
        int expenseId = 1;
        doNothing().when(mockExpenseRepository).deleteExpense(expenseId);

        expenseController.deleteExpense(expenseId);

        verify(mockExpenseRepository, times(1)).deleteExpense(expenseId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteExpense_InvalidId() {
        expenseController.deleteExpense(0);
    }

    @Test(expected = RuntimeException.class)
    public void testDeleteExpense_SQLException() throws SQLException {
        int expenseId = 1;
        doThrow(new SQLException("Database error")).when(mockExpenseRepository).deleteExpense(expenseId);

        expenseController.deleteExpense(expenseId);
    }

    private Expense createValidExpense() {
        Expense expense = new Expense();
        expense.setDescription("Lunch");
        expense.setCategory("Food");
        expense.setAmount(15.0);
        expense.setDate(LocalDate.now());
        return expense;
    }
}
