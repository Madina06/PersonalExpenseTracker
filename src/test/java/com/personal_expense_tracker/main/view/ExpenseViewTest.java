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

import static org.junit.Assert.assertEquals;
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
    public void testRefreshExpenseTable() {
        Expense expense1 = new Expense();
        expense1.setId(1);
        expense1.setDescription("Groceries");
        expense1.setCategory("Food");
        expense1.setAmount(100.0);
        expense1.setDate(LocalDate.of(2023, 1, 1));

        Expense expense2 = new Expense();
        expense2.setId(2);
        expense2.setDescription("Utilities");
        expense2.setCategory("Bills");
        expense2.setAmount(200.0);
        expense2.setDate(LocalDate.of(2023, 1, 2));

        when(mockExpenseController.getAllExpenses()).thenReturn(Arrays.asList(expense1, expense2));

        expenseView.refreshExpenseTable();

        JTable expenseTable = expenseView.expenseTable;
        DefaultTableModel model = (DefaultTableModel) expenseTable.getModel();
        assert model.getRowCount() == 2;
        assert model.getValueAt(0, 1).equals("Groceries");
        assert model.getValueAt(1, 1).equals("Utilities");
    }

    @Test
    public void testAddExpenseButton() {
        JButton addButton = expenseView.addExpenseButton;
        addButton.doClick();

        verify(mockExpenseController, times(0)).addExpense(any(Expense.class)); 
    }

    @Test
    public void testUpdateExpenseButton() {
        JTable expenseTable = expenseView.expenseTable;
        DefaultTableModel model = (DefaultTableModel) expenseTable.getModel();
        model.addRow(new Object[]{1, "Groceries", "Food", 100.0, LocalDate.of(2023, 1, 1).toString()});

        expenseTable.setRowSelectionInterval(0, 0);

        JButton updateButton = expenseView.updateExpenseButton;
        updateButton.doClick();

        verify(mockExpenseController, times(0)).updateExpense(any(Expense.class)); 
    }

    @Test
    public void testDeleteExpenseButton() {
        JTable expenseTable = expenseView.expenseTable;
        DefaultTableModel model = (DefaultTableModel) expenseTable.getModel();
        model.addRow(new Object[]{1, "Groceries", "Food", 100.0, LocalDate.of(2023, 1, 1).toString()});

        expenseTable.setRowSelectionInterval(0, 0);
        expenseView.setOptionPaneFactory((parent, message) -> {
            assertEquals("Expense deleted successfully!", message);
        });

        JButton deleteButton = expenseView.deleteExpenseButton;
        deleteButton.doClick();

        verify(mockExpenseController, times(1)).deleteExpense(1);
    }

    @Test
    public void testNoSelectionForDelete() {
        JTable expenseTable = expenseView.expenseTable;
        DefaultTableModel model = (DefaultTableModel) expenseTable.getModel();
        model.addRow(new Object[]{1, "Groceries", "Food", 100.0, LocalDate.of(2023, 1, 1).toString()});

        expenseView.setOptionPaneFactory((parent, message) -> {
            assertEquals("Please select an expense to delete.", message);
        });
 
        JButton deleteButton = expenseView.deleteExpenseButton;
        expenseTable.clearSelection();
        deleteButton.doClick();

        verify(mockExpenseController, times(0)).deleteExpense(anyInt());
    }

    @Test
    public void testNoSelectionForUpdate() {
        JTable expenseTable = expenseView.expenseTable;
        DefaultTableModel model = (DefaultTableModel) expenseTable.getModel();
        model.addRow(new Object[]{1, "Groceries", "Food", 100.0, LocalDate.of(2023, 1, 1).toString()});

        expenseView.setOptionPaneFactory((parent, message) -> {
            assertEquals("Please select an expense to update.", message);
        });

        JButton updateButton = expenseView.updateExpenseButton;
        expenseTable.clearSelection();
        updateButton.doClick();
  
        verify(mockExpenseController, times(0)).updateExpense(any(Expense.class));
    }
}