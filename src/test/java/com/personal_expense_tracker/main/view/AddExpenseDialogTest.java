package com.personal_expense_tracker.main.view;

import com.personal_expense_tracker.main.controller.ExpenseController;
import com.personal_expense_tracker.main.model.Expense;

import javax.swing.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

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
    public void testSaveNewExpense() {
        // Arrange
        addExpenseDialog.getDescriptionField().setText("Groceries");
        addExpenseDialog.getAmountField().setText("150.00");
        addExpenseDialog.getCategoryField().setText("Food");
        addExpenseDialog.getDateField().setText("2023-01-15");

        JButton saveButton = addExpenseDialog.getSaveButton();

        // Act
        saveButton.doClick();

        // Assert with Custom Matcher
        verify(mockExpenseController, times(1)).addExpense(argThat(expense ->
            "Groceries".equals(expense.getDescription()) &&
            expense.getAmount() == 150.0 &&
            "Food".equals(expense.getCategory()) &&
            LocalDate.of(2023, 1, 15).equals(expense.getDate())
        ));
        verify(mockParentView, times(1)).refreshExpenseTable();
    }

    @Test
    public void testUpdateExistingExpense() {
        // Arrange
        Expense existingExpense = new Expense();
        existingExpense.setId(1);
        existingExpense.setDescription("Utilities");
        existingExpense.setCategory("Bills");
        existingExpense.setAmount(200.0);
        existingExpense.setDate(LocalDate.of(2023, 1, 10));

        addExpenseDialog = new AddExpenseDialog(mockExpenseController, mockParentView, existingExpense);

        addExpenseDialog.getDescriptionField().setText("Updated Utilities");
        addExpenseDialog.getAmountField().setText("250.00");
        addExpenseDialog.getCategoryField().setText("Bills");
        addExpenseDialog.getDateField().setText("2023-01-20");

        JButton saveButton = addExpenseDialog.getSaveButton();

        // Act
        saveButton.doClick();

        // Assert with Custom Matcher
        verify(mockExpenseController, times(1)).updateExpense(argThat(expense ->
            expense.getId() == 1 &&
            "Updated Utilities".equals(expense.getDescription()) &&
            expense.getAmount() == 250.0 &&
            "Bills".equals(expense.getCategory()) &&
            LocalDate.of(2023, 1, 20).equals(expense.getDate())
        ));
        verify(mockParentView, times(1)).refreshExpenseTable();
    }

    @Test
    public void testValidationError() {
        // Arrange
        addExpenseDialog.getDescriptionField().setText(""); // Invalid description
        addExpenseDialog.getAmountField().setText("150.00");
        addExpenseDialog.getCategoryField().setText("Food");
        addExpenseDialog.getDateField().setText("2023-01-15");

        JButton saveButton = addExpenseDialog.getSaveButton();

        // Act
        saveButton.doClick();

        // Assert
        verify(mockExpenseController, times(0)).addExpense(any(Expense.class));
        verify(mockParentView, times(0)).refreshExpenseTable();
    }

}
