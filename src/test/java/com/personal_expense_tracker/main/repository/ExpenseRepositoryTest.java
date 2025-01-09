package com.personal_expense_tracker.main.repository;

import com.personal_expense_tracker.main.model.Expense;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ExpenseRepositoryTest {

    private ExpenseRepository expenseRepository;

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        expenseRepository = new ExpenseRepository(mockConnection);
    }

    @Test
    public void testSaveExpense() throws Exception {
        // Arrange
        Expense expense = new Expense();
        expense.setDescription("Groceries");
        expense.setCategory("Food");
        expense.setAmount(100.0);
        expense.setDate(LocalDate.of(2023, 1, 1));

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Act
        expenseRepository.saveExpense(expense);

        // Assert
        verify(mockPreparedStatement, times(1)).setString(1, expense.getDescription());
        verify(mockPreparedStatement, times(1)).setString(2, expense.getCategory());
        verify(mockPreparedStatement, times(1)).setDouble(3, expense.getAmount());
        verify(mockPreparedStatement, times(1)).setDate(4, Date.valueOf(expense.getDate()));
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testGetAllExpenses() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, false); // Simulate one row in the result set
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("description")).thenReturn("Groceries");
        when(mockResultSet.getString("category")).thenReturn("Food");
        when(mockResultSet.getDouble("amount")).thenReturn(100.0);
        when(mockResultSet.getDate("date")).thenReturn(Date.valueOf(LocalDate.of(2023, 1, 1)));

        // Act
        List<Expense> expenses = expenseRepository.getAllExpenses();

        // Assert
        assertEquals(1, expenses.size());
        Expense expense = expenses.get(0);
        assertEquals(1, expense.getId());
        assertEquals("Groceries", expense.getDescription());
        assertEquals("Food", expense.getCategory());
        assertEquals(100.0, expense.getAmount(), 0.01);
        assertEquals(LocalDate.of(2023, 1, 1), expense.getDate());
    }

    @Test
    public void testUpdateExpense() throws Exception {
        // Arrange
        Expense expense = new Expense();
        expense.setId(1);
        expense.setDescription("Updated Groceries");
        expense.setCategory("Food");
        expense.setAmount(150.0);
        expense.setDate(LocalDate.of(2023, 1, 2));

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Act
        expenseRepository.updateExpense(expense);

        // Assert
        verify(mockPreparedStatement, times(1)).setString(1, expense.getDescription());
        verify(mockPreparedStatement, times(1)).setString(2, expense.getCategory());
        verify(mockPreparedStatement, times(1)).setDouble(3, expense.getAmount());
        verify(mockPreparedStatement, times(1)).setDate(4, Date.valueOf(expense.getDate()));
        verify(mockPreparedStatement, times(1)).setInt(5, expense.getId());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testDeleteExpense() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Act
        expenseRepository.deleteExpense(1);

        // Assert
        verify(mockPreparedStatement, times(1)).setInt(1, 1);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }
}
