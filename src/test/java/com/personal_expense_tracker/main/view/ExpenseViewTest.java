package com.personal_expense_tracker.main.view;

import com.personal_expense_tracker.main.controller.ExpenseController;
import com.personal_expense_tracker.main.model.Expense;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

public class ExpenseViewTest {

    @Mock
    private ExpenseController mockExpenseController;

    private ExpenseView expenseView;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        expenseView = new ExpenseView(mockExpenseController);
    }

    @Test
    public void testRefreshExpenseTable_Success() {
        Expense expense1 = new Expense(1, "Groceries", "Food", 100.0, LocalDate.of(2023, 1, 1));
        Expense expense2 = new Expense(2, "Utilities", "Bills", 200.0, LocalDate.of(2023, 1, 2));
        when(mockExpenseController.getAllExpenses()).thenReturn(Arrays.asList(expense1, expense2));

        expenseView.refreshExpenseTable();

        JTable expenseTable = expenseView.expenseTable;
        DefaultTableModel model = (DefaultTableModel) expenseTable.getModel();
        assert model.getRowCount() == 2;
        assert model.getValueAt(0, 1).equals("Groceries");
        assert model.getValueAt(1, 1).equals("Utilities");
    }

    @Test
    public void testRefreshExpenseTable_Exception() {
        when(mockExpenseController.getAllExpenses()).thenThrow(new RuntimeException("Database error"));

        expenseView.refreshExpenseTable();
        
        JTable expenseTable = expenseView.expenseTable;
        DefaultTableModel model = (DefaultTableModel) expenseTable.getModel();
        assert model.getRowCount() == 0; 
    }

    @Test
    public void testAddExpenseButton() {
        JButton addButton = expenseView.addExpenseButton;
        addButton.doClick();

        verify(mockExpenseController, times(0)).addExpense(any(Expense.class)); // Непосредственно контроллер не вызывается
    }

    @Test
    public void testUpdateExpenseButton_ValidSelection() {
        JTable expenseTable = expenseView.expenseTable;
        DefaultTableModel model = (DefaultTableModel) expenseTable.getModel();
        model.addRow(new Object[]{1, "Groceries", "Food", 100.0, LocalDate.of(2023, 1, 1).toString()});
        expenseTable.setRowSelectionInterval(0, 0);

        JButton updateButton = expenseView.updateExpenseButton;
        updateButton.doClick();

        verify(mockExpenseController, times(0)).updateExpense(any(Expense.class));
    }

    @Test
    public void testUpdateExpenseButton_NoSelection() {
        JButton updateButton = expenseView.updateExpenseButton;
        updateButton.doClick();

        verify(mockExpenseController, times(0)).updateExpense(any(Expense.class));
    }

    @Test
    public void testDeleteExpenseButton_ValidSelection() {
        JTable expenseTable = expenseView.expenseTable;
        DefaultTableModel model = (DefaultTableModel) expenseTable.getModel();
        model.addRow(new Object[]{1, "Groceries", "Food", 100.0, LocalDate.of(2023, 1, 1).toString()});
        expenseTable.setRowSelectionInterval(0, 0);

        JButton deleteButton = expenseView.deleteExpenseButton;
        deleteButton.doClick();

        verify(mockExpenseController, times(1)).deleteExpense(1);
    }

    @Test
    public void testDeleteExpenseButton_NoSelection() {
        JButton deleteButton = expenseView.deleteExpenseButton;
        deleteButton.doClick();

        verify(mockExpenseController, times(0)).deleteExpense(anyInt());
    }
}