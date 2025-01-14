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
        
        addExpenseDialog.setOptionPaneFactory((parent, message) -> {
            assertEquals("Expense added successfully!", message);
        });

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
        
        addExpenseDialog.setOptionPaneFactory((parent, message) -> {
            assertEquals("Amount must be a valid number.", message);
        });

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
        
        addExpenseDialog.setOptionPaneFactory((parent, message) -> {
            assertEquals("All fields must be filled out.", message);
        });

        // Act
        saveButton.doClick();

        descriptionField.setText("Test");
        saveButton.doClick();
        

        amountField.setText("1");
        saveButton.doClick();
        

        categoryField.setText("Test");
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
    
    @Test
    public void testSaveNewExpenseInvalidDateFormat() {
        // Arrange
        JTextField descriptionField = addExpenseDialog.getDescriptionField();
        JTextField amountField = addExpenseDialog.getAmountField();
        JTextField categoryField = addExpenseDialog.getCategoryField();
        JTextField dateField = addExpenseDialog.getDateField();
        JButton saveButton = addExpenseDialog.getSaveButton();

        descriptionField.setText("Sample Description");
        amountField.setText("100.0");
        categoryField.setText("Food");
        // Set invalid date format
        dateField.setText("2025/01/15");

        addExpenseDialog.setOptionPaneFactory((parent, message) -> {
            assertEquals("Date must be in the format YYYY-MM-DD.", message);
        });

        // Act
        saveButton.doClick();

        // Assert
        verify(mockExpenseController, never()).addExpense(any(Expense.class));
    }
}