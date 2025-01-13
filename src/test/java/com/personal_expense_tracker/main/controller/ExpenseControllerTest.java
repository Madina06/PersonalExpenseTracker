package com.personal_expense_tracker.main.controller;

import com.personal_expense_tracker.main.model.Expense;
import com.personal_expense_tracker.main.repository.ExpenseRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
    public void testAddExpense_ValidExpense() throws Exception {
        // Arrange
        Expense expense = new Expense();
        expense.setDescription("Lunch");
        expense.setCategory("Food");
        expense.setAmount(15.0);
        expense.setDate(LocalDate.now());

        doNothing().when(mockExpenseRepository).saveExpense(expense);

        // Act
        expenseController.addExpense(expense);

        // Assert
        verify(mockExpenseRepository, times(1)).saveExpense(expense);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddExpense_NullDescription() {
        Expense expense = new Expense();
        expense.setDescription(null);
        expense.setCategory("Food");
        expense.setAmount(15.0);
        expense.setDate(LocalDate.now());

        expenseController.addExpense(expense);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddExpense_EmptyDescription() {
        Expense expense = new Expense();
        expense.setDescription("");
        expense.setCategory("Food");
        expense.setAmount(15.0);
        expense.setDate(LocalDate.now());

        expenseController.addExpense(expense);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddExpense_NullCategory() {
        Expense expense = new Expense();
        expense.setDescription("Lunch");
        expense.setCategory(null);
        expense.setAmount(15.0);
        expense.setDate(LocalDate.now());

        expenseController.addExpense(expense);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddExpense_EmptyCategory() {
        Expense expense = new Expense();
        expense.setDescription("Lunch");
        expense.setCategory("");
        expense.setAmount(15.0);
        expense.setDate(LocalDate.now());

        expenseController.addExpense(expense);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddExpense_CategoryWithNumbers() {
        Expense expense = new Expense();
        expense.setDescription("Lunch");
        expense.setCategory("123Food");
        expense.setAmount(15.0);
        expense.setDate(LocalDate.now());

        expenseController.addExpense(expense);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddExpense_NegativeAmount() {
        Expense expense = new Expense();
        expense.setDescription("Lunch");
        expense.setCategory("Food");
        expense.setAmount(-15.0);
        expense.setDate(LocalDate.now());

        expenseController.addExpense(expense);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddExpense_ZeroAmount() {
        Expense expense = new Expense();
        expense.setDescription("Lunch");
        expense.setCategory("Food");
        expense.setAmount(0.0);
        expense.setDate(LocalDate.now());

        expenseController.addExpense(expense);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddExpense_NullDate() {
        Expense expense = new Expense();
        expense.setDescription("Lunch");
        expense.setCategory("Food");
        expense.setAmount(15.0);
        expense.setDate(null);

        expenseController.addExpense(expense);
    }

    @Test
    public void testGetAllExpenses_ValidData() throws Exception {
        Expense expense1 = new Expense();
        expense1.setDescription("Lunch");
        expense1.setCategory("Food");
        expense1.setAmount(15.0);
        expense1.setDate(LocalDate.now());

        Expense expense2 = new Expense();
        expense2.setDescription("Taxi");
        expense2.setCategory("Transport");
        expense2.setAmount(20.0);
        expense2.setDate(LocalDate.now());

        List<Expense> mockExpenses = Arrays.asList(expense1, expense2);
        when(mockExpenseRepository.getAllExpenses()).thenReturn(mockExpenses);

        List<Expense> result = expenseController.getAllExpenses();

        assertEquals(2, result.size());
        assertEquals("Lunch", result.get(0).getDescription());
        assertEquals("Taxi", result.get(1).getDescription());
        verify(mockExpenseRepository, times(1)).getAllExpenses();
    }

    @Test
    public void testGetAllExpenses_NoData() throws Exception {
        when(mockExpenseRepository.getAllExpenses()).thenReturn(Collections.emptyList());

        List<Expense> result = expenseController.getAllExpenses();

        assertTrue(result.isEmpty());
        verify(mockExpenseRepository, times(1)).getAllExpenses();
    }

    @Test
    public void testUpdateExpense_ValidExpense() throws Exception {
        Expense expense = new Expense();
        expense.setId(1);
        expense.setDescription("Updated Lunch");
        expense.setCategory("Food");
        expense.setAmount(20.0);
        expense.setDate(LocalDate.now());

        doNothing().when(mockExpenseRepository).updateExpense(expense);

        expenseController.updateExpense(expense);

        verify(mockExpenseRepository, times(1)).updateExpense(expense);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateExpense_InvalidExpense() {
        Expense expense = new Expense();
        expense.setId(1);
        expense.setDescription("");
        expense.setCategory("");
        expense.setAmount(-10.0);
        expense.setDate(null);

        expenseController.updateExpense(expense);
    }

    @Test
    public void testDeleteExpense_ValidId() throws Exception {
        // Arrange
        int expenseId = 1;

        doNothing().when(mockExpenseRepository).deleteExpense(expenseId);

        // Act
        expenseController.deleteExpense(expenseId);

        // Assert
        verify(mockExpenseRepository, times(1)).deleteExpense(expenseId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteExpense_InvalidNegativeId() {
        expenseController.deleteExpense(-1);
    }

    @Test
    public void testDeleteExpense_NonExistentId() throws Exception {
        int expenseId = 999;

        doNothing().when(mockExpenseRepository).deleteExpense(expenseId);

        expenseController.deleteExpense(expenseId);

        verify(mockExpenseRepository, times(1)).deleteExpense(expenseId);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteExpense_InvalidId_Zero() {
        // Arrange
        int expenseId = 0;

        // Act
        expenseController.deleteExpense(expenseId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteExpense_InvalidId_Negative() {
        // Arrange
        int expenseId = -1;

        // Act
        expenseController.deleteExpense(expenseId);
    }
    
    
}
