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
        descriptionField.setName("descriptionTextField");
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
                String description = descriptionField.getText();
                String amountText = amountField.getText();
                String category = categoryField.getText();
                String dateText = dateField.getText();

                // Validate fields
                if (description.isEmpty() || amountText.isEmpty() || category.isEmpty() || dateText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields must be filled out.");
                    return;
                }

                double amount;
                LocalDate date;

                try {
                    amount = Double.parseDouble(amountText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Amount must be a valid number.");
                    return;
                }

                try {
                    date = LocalDate.parse(dateText);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Date must be in the format YYYY-MM-DD.");
                    return;
                }

                Expense expense = new Expense();
                expense.setDescription(description);
                expense.setAmount(amount);
                expense.setCategory(category);
                expense.setDate(date);

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
    
    public JTextField getDescriptionField() {
        return descriptionField;
    }

    public JTextField getAmountField() {
        return amountField;
    }

    public JTextField getCategoryField() {
        return categoryField;
    }

    public JTextField getDateField() {
        return dateField;
    }

    public JButton getSaveButton() {
        return saveButton;
    }
}
