package com.personal_expense_tracker.main.view;

import com.personal_expense_tracker.main.controller.ExpenseController;
import com.personal_expense_tracker.main.model.Expense;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class AddExpenseDialog extends JDialog {

    private JTextField descriptionField;
    private JTextField amountField;
    private JTextField categoryField;
    private JTextField dateField;
    private JButton saveButton;

    private final ExpenseController expenseController;
    private final ExpenseView parentView;
    private final Expense existingExpense;

    public AddExpenseDialog(ExpenseController expenseController, ExpenseView parentView) {
        this(expenseController, parentView, null);
    }

    public AddExpenseDialog(ExpenseController expenseController, ExpenseView parentView, Expense existingExpense) {
        this.expenseController = expenseController;
        this.parentView = parentView;
        this.existingExpense = existingExpense;

        setTitle(existingExpense == null ? "Add Expense" : "Update Expense");
        setSize(400, 300);
        setLayout(new GridLayout(5, 2));

        add(new JLabel("Description:"));
        descriptionField = new JTextField(existingExpense != null ? existingExpense.getDescription() : "");
        add(descriptionField);

        add(new JLabel("Amount:"));
        amountField = new JTextField(existingExpense != null ? String.valueOf(existingExpense.getAmount()) : "");
        add(amountField);

        add(new JLabel("Category:"));
        categoryField = new JTextField(existingExpense != null ? existingExpense.getCategory() : "");
        add(categoryField);

        add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField(existingExpense != null ? existingExpense.getDate().toString() : "");
        add(dateField);

        saveButton = new JButton("Save");
        add(saveButton);

        saveButton.addActionListener(e -> {
            try {
                Expense expense = new Expense();
                expense.setDescription(descriptionField.getText());
                expense.setAmount(Double.parseDouble(amountField.getText()));
                expense.setCategory(categoryField.getText());
                expense.setDate(LocalDate.parse(dateField.getText()));

                if (existingExpense != null) {
                    expense.setId(existingExpense.getId());
                    expenseController.updateExpense(expense);
                    JOptionPane.showMessageDialog(this, "Expense updated successfully!");
                } else {
                    expenseController.addExpense(expense);
                    JOptionPane.showMessageDialog(this, "Expense added successfully!");
                }

                parentView.refreshExpenseTable();
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to save expense: " + ex.getMessage());
            }
        });
    }
}
