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

        // Используем мок вместо реального объекта
        addExpenseDialog = new AddExpenseDialog(mockExpenseController, mockParentView);
    }

    @Test
    public void testSaveNewExpenseValid() {
        // Arrange
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

        // Act
        saveButton.doClick();

        // Assert
        verify(mockExpenseController, times(1)).addExpense(any(Expense.class));
        verify(mockParentView, times(1)).refreshExpenseTable();
    }

    @Test
    public void testSaveNewExpenseInvalidAmount() {
        // Arrange
        JTextField descriptionField = addExpenseDialog.getDescriptionField();
        JTextField amountField = addExpenseDialog.getAmountField();
        JTextField categoryField = addExpenseDialog.getCategoryField();
        JTextField dateField = addExpenseDialog.getDateField();
        JButton saveButton = addExpenseDialog.getSaveButton();

        descriptionField.setText("Groceries");
        amountField.setText("invalid");
        categoryField.setText("Food");
        dateField.setText(LocalDate.now().toString());

        // Act
        saveButton.doClick();

        // Assert
        verify(mockExpenseController, never()).addExpense(any(Expense.class));
    }

    @Test
    public void testSaveNewExpenseEmptyFields() {
        // Arrange
        JTextField descriptionField = addExpenseDialog.getDescriptionField();
        JTextField amountField = addExpenseDialog.getAmountField();
        JTextField categoryField = addExpenseDialog.getCategoryField();
        JTextField dateField = addExpenseDialog.getDateField();
        JButton saveButton = addExpenseDialog.getSaveButton();

        descriptionField.setText("");
        amountField.setText("");
        categoryField.setText("");
        dateField.setText("");

        // Act
        saveButton.doClick();

        // Assert
        verify(mockExpenseController, never()).addExpense(any(Expense.class));
    }

    @Test
    public void testCancelButtonClosesDialog() {
        // Arrange
        JButton cancelButton = addExpenseDialog.getCancelButton();

        // Act
        cancelButton.doClick();

        // Assert
        assertFalse(addExpenseDialog.isVisible());
    }
}
