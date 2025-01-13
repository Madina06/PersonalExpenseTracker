package com.personal_expense_tracker.main.controller;

import com.personal_expense_tracker.main.model.Expense;
import com.personal_expense_tracker.main.repository.ExpenseRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
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
    public void testAddExpense() throws Exception {
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
    public void testAddExpenseInvalid() {
        // Arrange
        Expense expense = new Expense(); // Missing required fields

        // Act
        expenseController.addExpense(expense);
    }

    @Test
    public void testGetAllExpenses() throws Exception {
        // Arrange
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

        // Act
        List<Expense> result = expenseController.getAllExpenses();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Lunch", result.get(0).getDescription());
        assertEquals("Taxi", result.get(1).getDescription());
        verify(mockExpenseRepository, times(1)).getAllExpenses();
    }

    @Test
    public void testUpdateExpense() throws Exception {
        // Arrange
        Expense expense = new Expense();
        expense.setId(1);
        expense.setDescription("Lunch Updated");
        expense.setCategory("Food");
        expense.setAmount(20.0);
        expense.setDate(LocalDate.now());

        doNothing().when(mockExpenseRepository).updateExpense(expense);

        // Act
        expenseController.updateExpense(expense);

        // Assert
        verify(mockExpenseRepository, times(1)).updateExpense(expense);
    }

    @Test
    public void testDeleteExpense() throws Exception {
        // Arrange
        int expenseId = 1;

        doNothing().when(mockExpenseRepository).deleteExpense(expenseId);

        // Act
        expenseController.deleteExpense(expenseId);

        // Assert
        verify(mockExpenseRepository, times(1)).deleteExpense(expenseId);
    }
}
