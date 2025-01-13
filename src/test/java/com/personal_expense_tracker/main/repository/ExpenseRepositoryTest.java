package com.personal_expense_tracker.main.repository;

import com.personal_expense_tracker.main.model.Expense;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;
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
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        expenseRepository = new ExpenseRepository(mockConnection);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Test
    public void testCreateTableIfNotExists() throws Exception {
        // Arrange
        when(mockConnection.createStatement()).thenReturn(mock(Statement.class));

        // Act
        expenseRepository.createTableIfNotExists();

        // Assert
        verify(mockConnection, times(1)).createStatement();
    }

    @Test
    public void testSaveExpense() throws Exception {
        // Arrange
        Expense expense = new Expense();
        expense.setDescription("Groceries");
        expense.setCategory("Food");
        expense.setAmount(150.0);
        expense.setDate(LocalDate.now());

        // Act
        expenseRepository.saveExpense(expense);

        // Assert
        verify(mockPreparedStatement, times(1)).setString(1, "Groceries");
        verify(mockPreparedStatement, times(1)).setString(2, "Food");
        verify(mockPreparedStatement, times(1)).setDouble(3, 150.0);
        verify(mockPreparedStatement, times(1)).setDate(4, Date.valueOf(LocalDate.now()));
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testGetAllExpenses() throws Exception {
        // Arrange
        when(mockResultSet.next()).thenReturn(true, true, false);

        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("description")).thenReturn("Groceries", "Transport");
        when(mockResultSet.getString("category")).thenReturn("Food", "Travel");
        when(mockResultSet.getDouble("amount")).thenReturn(150.0, 50.0);
        when(mockResultSet.getDate("date")).thenReturn(Date.valueOf(LocalDate.now()));

        // Act
        List<Expense> expenses = expenseRepository.getAllExpenses();

        // Assert
        assertEquals(2, expenses.size());
        assertEquals("Groceries", expenses.get(0).getDescription());
        assertEquals("Food", expenses.get(0).getCategory());
        assertEquals(150.0, expenses.get(0).getAmount(), 0.01);
        assertEquals("Transport", expenses.get(1).getDescription());
    }

    @Test
    public void testUpdateExpense() throws Exception {
        // Arrange
        Expense expense = new Expense();
        expense.setId(1);
        expense.setDescription("Groceries Updated");
        expense.setCategory("Food");
        expense.setAmount(200.0);
        expense.setDate(LocalDate.now());

        // Act
        expenseRepository.updateExpense(expense);

        // Assert
        verify(mockPreparedStatement, times(1)).setString(1, "Groceries Updated");
        verify(mockPreparedStatement, times(1)).setString(2, "Food");
        verify(mockPreparedStatement, times(1)).setDouble(3, 200.0);
        verify(mockPreparedStatement, times(1)).setDate(4, Date.valueOf(LocalDate.now()));
        verify(mockPreparedStatement, times(1)).setInt(5, 1);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testDeleteExpense() throws Exception {
        // Act
        expenseRepository.deleteExpense(1);

        // Assert
        verify(mockPreparedStatement, times(1)).setInt(1, 1);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }
}
