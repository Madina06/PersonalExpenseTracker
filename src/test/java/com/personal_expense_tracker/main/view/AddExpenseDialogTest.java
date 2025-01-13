package com.personal_expense_tracker.main.view;

import com.personal_expense_tracker.main.controller.ExpenseController;
import com.personal_expense_tracker.main.model.Expense;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class AddExpenseDialogTest {

    @Mock
    private ExpenseController mockExpenseController;

    @Mock
    private ExpenseView mockParentView;

    private AddExpenseDialog addExpenseDialog;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        addExpenseDialog = new AddExpenseDialog(mockExpenseController, mockParentView);
    }

    @Test
    public void testSaveNewExpense_AllFieldsEmpty() {
        JTextField descriptionField = addExpenseDialog.getDescriptionField();
        JTextField amountField = addExpenseDialog.getAmountField();
        JTextField categoryField = addExpenseDialog.getCategoryField();
        JTextField dateField = addExpenseDialog.getDateField();
        JButton saveButton = addExpenseDialog.getSaveButton();

        descriptionField.setText("");
        amountField.setText("");
        categoryField.setText("");
        dateField.setText("");

        saveButton.doClick();

        verify(mockExpenseController, never()).addExpense(any(Expense.class));
    }

    @Test
    public void testSaveNewExpense_OnlyDescriptionFilled() {
        JTextField descriptionField = addExpenseDialog.getDescriptionField();
        JTextField amountField = addExpenseDialog.getAmountField();
        JTextField categoryField = addExpenseDialog.getCategoryField();
        JTextField dateField = addExpenseDialog.getDateField();
        JButton saveButton = addExpenseDialog.getSaveButton();

        descriptionField.setText("Groceries");
        amountField.setText("");
        categoryField.setText("");
        dateField.setText("");

        saveButton.doClick();

        verify(mockExpenseController, never()).addExpense(any(Expense.class));
    }

    @Test
    public void testSaveNewExpense_InvalidAmountFormat() {
        JTextField descriptionField = addExpenseDialog.getDescriptionField();
        JTextField amountField = addExpenseDialog.getAmountField();
        JTextField categoryField = addExpenseDialog.getCategoryField();
        JTextField dateField = addExpenseDialog.getDateField();
        JButton saveButton = addExpenseDialog.getSaveButton();

        descriptionField.setText("Groceries");
        amountField.setText("abc");
        categoryField.setText("Food");
        dateField.setText(LocalDate.now().toString());

        saveButton.doClick();

        verify(mockExpenseController, never()).addExpense(any(Expense.class));
    }

    @Test
    public void testSaveNewExpense_InvalidDateFormat() {
        JTextField descriptionField = addExpenseDialog.getDescriptionField();
        JTextField amountField = addExpenseDialog.getAmountField();
        JTextField categoryField = addExpenseDialog.getCategoryField();
        JTextField dateField = addExpenseDialog.getDateField();
        JButton saveButton = addExpenseDialog.getSaveButton();

        descriptionField.setText("Groceries");
        amountField.setText("150.00");
        categoryField.setText("Food");
        dateField.setText("invalid-date");

        saveButton.doClick();

        verify(mockExpenseController, never()).addExpense(any(Expense.class));
    }

    @Test
    public void testSaveNewExpense_ValidFields() {
        JTextField descriptionField = addExpenseDialog.getDescriptionField();
        JTextField amountField = addExpenseDialog.getAmountField();
        JTextField categoryField = addExpenseDialog.getCategoryField();
        JTextField dateField = addExpenseDialog.getDateField();
        JButton saveButton = addExpenseDialog.getSaveButton();

        descriptionField.setText("Groceries");
        amountField.setText("150.00");
        categoryField.setText("Food");
        dateField.setText(LocalDate.now().toString());

        doNothing().when(mockExpenseController).addExpense(any(Expense.class));
        doNothing().when(mockParentView).refreshExpenseTable();

        saveButton.doClick();

        verify(mockExpenseController, times(1)).addExpense(any(Expense.class));
        verify(mockParentView, times(1)).refreshExpenseTable();
    }

    @Test
    public void testSaveNewExpense_ExceptionThrown() {
        JTextField descriptionField = addExpenseDialog.getDescriptionField();
        JTextField amountField = addExpenseDialog.getAmountField();
        JTextField categoryField = addExpenseDialog.getCategoryField();
        JTextField dateField = addExpenseDialog.getDateField();
        JButton saveButton = addExpenseDialog.getSaveButton();

        descriptionField.setText("Groceries");
        amountField.setText("150.00");
        categoryField.setText("Food");
        dateField.setText(LocalDate.now().toString());

        doThrow(new RuntimeException("Test Exception")).when(mockExpenseController).addExpense(any(Expense.class));

        saveButton.doClick();

        verify(mockExpenseController, times(1)).addExpense(any(Expense.class));
    }
    
    @Test
    public void testSaveNewExpense_EmptyDescriptionAndOthersValid() {
        JTextField descriptionField = addExpenseDialog.getDescriptionField();
        JTextField amountField = addExpenseDialog.getAmountField();
        JTextField categoryField = addExpenseDialog.getCategoryField();
        JTextField dateField = addExpenseDialog.getDateField();
        JButton saveButton = addExpenseDialog.getSaveButton();

        descriptionField.setText(""); 
        amountField.setText("100.00"); 
        categoryField.setText("Food"); 
        dateField.setText(LocalDate.now().toString()); 

        saveButton.doClick();

        verify(mockExpenseController, never()).addExpense(any(Expense.class));
    }

    @Test
    public void testSaveNewExpense_EmptyAmountAndOthersValid() {
        JTextField descriptionField = addExpenseDialog.getDescriptionField();
        JTextField amountField = addExpenseDialog.getAmountField();
        JTextField categoryField = addExpenseDialog.getCategoryField();
        JTextField dateField = addExpenseDialog.getDateField();
        JButton saveButton = addExpenseDialog.getSaveButton();

        descriptionField.setText("Groceries"); 
        amountField.setText(""); 
        categoryField.setText("Food"); 
        dateField.setText(LocalDate.now().toString()); 

        saveButton.doClick();

        verify(mockExpenseController, never()).addExpense(any(Expense.class));
    }

    @Test
    public void testSaveNewExpense_EmptyCategoryAndOthersValid() {
        JTextField descriptionField = addExpenseDialog.getDescriptionField();
        JTextField amountField = addExpenseDialog.getAmountField();
        JTextField categoryField = addExpenseDialog.getCategoryField();
        JTextField dateField = addExpenseDialog.getDateField();
        JButton saveButton = addExpenseDialog.getSaveButton();

        descriptionField.setText("Groceries"); 
        amountField.setText("100.00"); 
        categoryField.setText(""); 
        dateField.setText(LocalDate.now().toString()); 

        saveButton.doClick();

        verify(mockExpenseController, never()).addExpense(any(Expense.class));
    }

    @Test
    public void testSaveNewExpense_EmptyDateAndOthersValid() {
        JTextField descriptionField = addExpenseDialog.getDescriptionField();
        JTextField amountField = addExpenseDialog.getAmountField();
        JTextField categoryField = addExpenseDialog.getCategoryField();
        JTextField dateField = addExpenseDialog.getDateField();
        JButton saveButton = addExpenseDialog.getSaveButton();

        descriptionField.setText("Groceries"); 
        amountField.setText("100.00"); 
        categoryField.setText("Food"); 
        dateField.setText(""); 

        saveButton.doClick();

        verify(mockExpenseController, never()).addExpense(any(Expense.class));
    }


    @Test
    public void testCancelButton_ClosesDialog() {
        JButton cancelButton = addExpenseDialog.getCancelButton();

        cancelButton.doClick();

        assertFalse(addExpenseDialog.isVisible());
    }
}
