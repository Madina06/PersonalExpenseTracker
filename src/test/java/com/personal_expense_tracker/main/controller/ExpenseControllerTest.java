package com.personal_expense_tracker.main.controller;

import com.personal_expense_tracker.main.model.Expense;
import com.personal_expense_tracker.main.repository.ExpenseRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
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
    public void testAddExpenseValid() throws Exception {
        // Arrange
        Expense expense = new Expense();
        expense.setCategory("Food");
        expense.setAmount(100.0);
        expense.setDescription("Groceries");
        expense.setDate(LocalDate.of(2023, 1, 1));

        // Act
        expenseController.addExpense(expense);

        // Assert
        verify(mockExpenseRepository, times(1)).saveExpense(expense);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddExpenseInvalid() {
        // Arrange
        Expense expense = new Expense();
        expense.setCategory("Food");
        expense.setAmount(-100.0);
        expense.setDescription("Groceries");
        expense.setDate(LocalDate.of(2023, 1, 1));

        // Act
        expenseController.addExpense(expense);

        // Assert: Exception expected
    }

    @Test
    public void testGetAllExpenses() throws Exception {
        // Arrange
        Expense expense1 = new Expense();
        expense1.setId(1);
        expense1.setCategory("Food");
        expense1.setAmount(100.0);
        expense1.setDescription("Groceries");
        expense1.setDate(LocalDate.of(2023, 1, 1));

        Expense expense2 = new Expense();
        expense2.setId(2);
        expense2.setCategory("Bills");
        expense2.setAmount(200.0);
        expense2.setDescription("Utilities");
        expense2.setDate(LocalDate.of(2023, 1, 2));

        when(mockExpenseRepository.getAllExpenses()).thenReturn(Arrays.asList(expense1, expense2));

        // Act
        List<Expense> expenses = expenseController.getAllExpenses();

        // Assert
        verify(mockExpenseRepository, times(1)).getAllExpenses();
        assertEquals(2, expenses.size());
        assertEquals("Groceries", expenses.get(0).getDescription());
        assertEquals("Utilities", expenses.get(1).getDescription());
    }

    @Test
    public void testUpdateExpenseValid() throws Exception {
        // Arrange
        Expense expense = new Expense();
        expense.setId(1);
        expense.setCategory("Food");
        expense.setAmount(200.0);
        expense.setDescription("Updated Description");
        expense.setDate(LocalDate.of(2023, 1, 5));

        // Act
        expenseController.updateExpense(expense);

        // Assert
        verify(mockExpenseRepository, times(1)).updateExpense(expense);
    }

    @Test
    public void testDeleteExpense() throws Exception {
        // Arrange
        int expenseId = 1;

        // Act
        expenseController.deleteExpense(expenseId);

        // Assert
        verify(mockExpenseRepository, times(1)).deleteExpense(expenseId);
    }
}
