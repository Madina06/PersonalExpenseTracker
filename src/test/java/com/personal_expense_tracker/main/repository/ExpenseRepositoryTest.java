package com.personal_expense_tracker.main.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.personal_expense_tracker.main.model.Expense;

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
        Statement mockStatement = mock(Statement.class);
        when(mockConnection.createStatement()).thenReturn(mockStatement);

        expenseRepository.createTableIfNotExists();

        verify(mockConnection, times(1)).createStatement();
        verify(mockStatement, times(1)).execute(anyString());
    }

    @Test
    public void testSaveExpense() throws Exception {
        Expense expense = new Expense();
        expense.setDescription("Groceries");
        expense.setCategory("Food");
        expense.setAmount(150.0);
        expense.setDate(LocalDate.now());

        expenseRepository.saveExpense(expense);

        verify(mockPreparedStatement, times(1)).setString(1, "Groceries");
        verify(mockPreparedStatement, times(1)).setString(2, "Food");
        verify(mockPreparedStatement, times(1)).setDouble(3, 150.0);
        verify(mockPreparedStatement, times(1)).setDate(4, Date.valueOf(LocalDate.now()));
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test(expected = SQLException.class)
    public void testSaveExpense_ThrowsSQLException() throws Exception {
        Expense expense = new Expense();
        expense.setDescription("Test Expense");
        expense.setCategory("Test Category");
        expense.setAmount(100.0);
        expense.setDate(LocalDate.now());

        doThrow(SQLException.class).when(mockPreparedStatement).executeUpdate();

        expenseRepository.saveExpense(expense);
    }

    @Test
    public void testGetAllExpenses() throws Exception {
        when(mockResultSet.next()).thenReturn(true, true, false);

        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("description")).thenReturn("Groceries", "Transport");
        when(mockResultSet.getString("category")).thenReturn("Food", "Travel");
        when(mockResultSet.getDouble("amount")).thenReturn(150.0, 50.0);
        when(mockResultSet.getDate("date")).thenReturn(Date.valueOf(LocalDate.now()));

        List<Expense> expenses = expenseRepository.getAllExpenses();

        assertEquals(2, expenses.size());
        assertEquals("Groceries", expenses.get(0).getDescription());
        assertEquals("Food", expenses.get(0).getCategory());
        assertEquals(150.0, expenses.get(0).getAmount(), 0.01);
        assertEquals("Transport", expenses.get(1).getDescription());
    }

    @Test
    public void testGetAllExpenses_NoData() throws Exception {
        when(mockResultSet.next()).thenReturn(false);

        List<Expense> expenses = expenseRepository.getAllExpenses();

        assertTrue(expenses.isEmpty());
    }

    @Test
    public void testUpdateExpense() throws Exception {
        Expense expense = new Expense();
        expense.setId(1);
        expense.setDescription("Groceries Updated");
        expense.setCategory("Food");
        expense.setAmount(200.0);
        expense.setDate(LocalDate.now());

        expenseRepository.updateExpense(expense);

        verify(mockPreparedStatement, times(1)).setString(1, "Groceries Updated");
        verify(mockPreparedStatement, times(1)).setString(2, "Food");
        verify(mockPreparedStatement, times(1)).setDouble(3, 200.0);
        verify(mockPreparedStatement, times(1)).setDate(4, Date.valueOf(LocalDate.now()));
        verify(mockPreparedStatement, times(1)).setInt(5, 1);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test(expected = SQLException.class)
    public void testUpdateExpense_ThrowsSQLException() throws Exception {
        Expense expense = new Expense();
        expense.setId(1);
        expense.setDescription("Invalid Expense");
        expense.setCategory("Food");
        expense.setAmount(100.0);
        expense.setDate(LocalDate.now()); 

        doThrow(SQLException.class).when(mockPreparedStatement).executeUpdate();

        expenseRepository.updateExpense(expense);
    }


    @Test
    public void testDeleteExpense() throws Exception {
        expenseRepository.deleteExpense(1);

        verify(mockPreparedStatement, times(1)).setInt(1, 1);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test(expected = SQLException.class)
    public void testDeleteExpense_ThrowsSQLException() throws Exception {
        doThrow(SQLException.class).when(mockPreparedStatement).executeUpdate();

        expenseRepository.deleteExpense(1);
    }
    
    @Test
    public void testSetIdInGetAllExpenses() throws Exception {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(42); 
        when(mockResultSet.getString("description")).thenReturn("Test Description");
        when(mockResultSet.getString("category")).thenReturn("Test Category");
        when(mockResultSet.getDouble("amount")).thenReturn(123.45);
        when(mockResultSet.getDate("date")).thenReturn(Date.valueOf(LocalDate.now()));

        List<Expense> expenses = expenseRepository.getAllExpenses();

        assertEquals(1, expenses.size());
        assertEquals(42, expenses.get(0).getId()); 
    }
    
    @Test
    public void testSetDateInGetAllExpenses() throws Exception {
        LocalDate testDate = LocalDate.of(2025, 1, 1);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("description")).thenReturn("Test Description");
        when(mockResultSet.getString("category")).thenReturn("Test Category");
        when(mockResultSet.getDouble("amount")).thenReturn(100.0);
        when(mockResultSet.getDate("date")).thenReturn(Date.valueOf(testDate));

        List<Expense> expenses = expenseRepository.getAllExpenses();

        assertEquals(1, expenses.size());
        assertEquals(testDate, expenses.get(0).getDate()); 
    }


}
