package com.personal_expense_tracker.main.view;

import javax.swing.*;
import java.awt.*;

public class AddExpenseView extends JFrame {
    private JTextField amountField;
    private JTextField descriptionField;
    private JTextField categoryField;
    private JTextField dateField;
    private JButton saveButton;

    public AddExpenseView() {
        setTitle("Add Expense");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        // Add fields for Expense data
        add(new JLabel("Amount:"));
        amountField = new JTextField();
        add(amountField);

        add(new JLabel("Description:"));
        descriptionField = new JTextField();
        add(descriptionField);

        add(new JLabel("Category:"));
        categoryField = new JTextField();
        add(categoryField);

        add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        add(dateField);

        saveButton = new JButton("Save");
        add(saveButton);
    }

    // Getters for fields
    public JTextField getAmountField() {
        return amountField;
    }

    public JTextField getDescriptionField() {
        return descriptionField;
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
