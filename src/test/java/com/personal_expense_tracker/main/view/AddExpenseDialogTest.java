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
    public void testSaveNewExpenseValid() {
        JTextField descriptionField = addExpenseDialog.getDescriptionField();
        JTextField amountField = addExpenseDialog.getAmountField();
        JTextField categoryField = addExpenseDialog.getCategoryField();
        JTextField dateField = addExpenseDialog.getDateField();
        JButton saveButton = addExpenseDialog.getSaveButton();

        descriptionField.setText("Groceries");
        amountField.setText("150.00");
        categoryField.setText("Food");
        dateField.setText(LocalDate.now().toString());
        
        addExpenseDialog.setOptionPaneFactory((parent, message) -> {
            assertEquals("Expense added successfully!", message);
        });

        doNothing().when(mockExpenseController).addExpense(any(Expense.class));
        doNothing().when(mockParentView).refreshExpenseTable();

        saveButton.doClick();

        verify(mockExpenseController, times(1)).addExpense(any(Expense.class));
        verify(mockParentView, times(1)).refreshExpenseTable();
    }

    @Test
    public void testSaveNewExpenseInvalidAmount() {
        JTextField descriptionField = addExpenseDialog.getDescriptionField();
        JTextField amountField = addExpenseDialog.getAmountField();
        JTextField categoryField = addExpenseDialog.getCategoryField();
        JTextField dateField = addExpenseDialog.getDateField();
        JButton saveButton = addExpenseDialog.getSaveButton();

        descriptionField.setText("Groceries");
        amountField.setText("invalid");
        categoryField.setText("Food");
        dateField.setText(LocalDate.now().toString());
        
        addExpenseDialog.setOptionPaneFactory((parent, message) -> {
            assertEquals("Amount must be a valid number.", message);
        });

        saveButton.doClick();

        verify(mockExpenseController, never()).addExpense(any(Expense.class));
    }

    @Test
    public void testSaveNewExpenseEmptyFields() {
        JTextField descriptionField = addExpenseDialog.getDescriptionField();
        JTextField amountField = addExpenseDialog.getAmountField();
        JTextField categoryField = addExpenseDialog.getCategoryField();
        JTextField dateField = addExpenseDialog.getDateField();
        JButton saveButton = addExpenseDialog.getSaveButton();

        descriptionField.setText("");
        amountField.setText("");
        categoryField.setText("");
        dateField.setText("");
        
        addExpenseDialog.setOptionPaneFactory((parent, message) -> {
            assertEquals("All fields must be filled out.", message);
        });

        saveButton.doClick();

        verify(mockExpenseController, never()).addExpense(any(Expense.class));
    }

    @Test
    public void testCancelButtonClosesDialog() {
        JButton cancelButton = addExpenseDialog.getCancelButton();

        cancelButton.doClick();

        assertFalse(addExpenseDialog.isVisible());
    }
}