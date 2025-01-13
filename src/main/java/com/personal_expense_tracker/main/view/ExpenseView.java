package com.personal_expense_tracker.main.view;

import com.personal_expense_tracker.main.controller.ExpenseController;
import com.personal_expense_tracker.main.model.Expense;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ExpenseView extends JFrame {

    public JTable expenseTable;
    private JButton addExpenseButton;
    private JButton updateExpenseButton;
    private JButton deleteExpenseButton;
    private DefaultTableModel tableModel;
    private final ExpenseController expenseController;


    public ExpenseView(ExpenseController expenseController) {
        this.expenseController = expenseController;
        setTitle("Personal Expense Tracker");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(15, 15));

<<<<<<< HEAD
        String[] columnNames = {"ID", "Description", "Category", "Amount", "Date"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        expenseTable = new JTable(tableModel);
        expenseTable.setName("expenseTable");
        JScrollPane scrollPane = new JScrollPane(expenseTable);

        JPanel buttonPanel = new JPanel();
        
        expenseTable = new JTable(tableModel);
        expenseTable.setName("expenseTable");
        
=======
        JPanel expensePanel = createExpensePanel();
        contentPane.add(expensePanel, BorderLayout.NORTH);
        add(expensePanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = createButtonsPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Populate Table
        refreshExpenseTable();

        // Event Listeners
        addExpenseButton.addActionListener(e -> new AddExpenseDialog(expenseController, this).setVisible(true));
        updateExpenseButton.addActionListener(e -> updateSelectedExpense());
        deleteExpenseButton.addActionListener(e -> deleteSelectedExpense());
    }

    private void updateSelectedExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense to update.");
        } else {
            int expenseId = Integer.parseInt(expenseTable.getValueAt(selectedRow, 0).toString());
            String description = expenseTable.getValueAt(selectedRow, 1).toString();
            String category = expenseTable.getValueAt(selectedRow, 2).toString();
            double amount = Double.parseDouble(expenseTable.getValueAt(selectedRow, 3).toString());
            LocalDate date = LocalDate.parse(expenseTable.getValueAt(selectedRow, 4).toString());

            Expense expense = new Expense();
            expense.setId(expenseId);
            expense.setDescription(description);
            expense.setCategory(category);
            expense.setAmount(amount);
            expense.setDate(date);
            new AddExpenseDialog(expenseController, this, expense).setVisible(true);
            refreshExpenseTable();
        }
    }

    private void deleteSelectedExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense to delete.", "No Selected row", 0);
        } else {
            int expenseId = Integer.parseInt(expenseTable.getValueAt(selectedRow, 0).toString());
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete Expense ID: " + expenseId + "?", "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                expenseController.deleteExpense(expenseId);
                refreshExpenseTable();
                JOptionPane.showMessageDialog(this, "Expense deleted successfully!");
            }
        }
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel();
>>>>>>> c57b429e11f3ff35ea6189b73561bd208d5c3be5
        addExpenseButton = new JButton("Add Expense");
        addExpenseButton.setName("addExpenseButton");
        addExpenseButton.setBackground(new Color(76, 175, 80));

        updateExpenseButton = new JButton("Update Expense");
        updateExpenseButton.setName("updateExpenseButton");
        updateExpenseButton.setBackground(new Color(33, 150, 243));

        deleteExpenseButton = new JButton("Delete Expense");
        deleteExpenseButton.setName("deleteExpenseButton");
        deleteExpenseButton.setBackground(new Color(244, 67, 54));

        buttonsPanel.add(addExpenseButton);
        buttonsPanel.add(updateExpenseButton);
        buttonsPanel.add(deleteExpenseButton);
        return buttonsPanel;
    }


<<<<<<< HEAD
        refreshExpenseTable();

        addExpenseButton.addActionListener(e -> new AddExpenseDialog(expenseController, this).setVisible(true));

        updateExpenseButton.addActionListener(e -> {
            int selectedRow = expenseTable.getSelectedRow();
            if (selectedRow >= 0) {
                int expenseId = Integer.parseInt(expenseTable.getValueAt(selectedRow, 0).toString());
                String description = expenseTable.getValueAt(selectedRow, 1).toString();
                String category = expenseTable.getValueAt(selectedRow, 2).toString();
                double amount = Double.parseDouble(expenseTable.getValueAt(selectedRow, 3).toString());
                LocalDate date = LocalDate.parse(expenseTable.getValueAt(selectedRow, 4).toString());

                Expense expense = new Expense();
                expense.setId(expenseId);
                expense.setDescription(description);
                expense.setCategory(category);
                expense.setAmount(amount);
                expense.setDate(date);

                new AddExpenseDialog(expenseController, this, expense).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Please select an expense to update.");
            }
        });

        deleteExpenseButton.addActionListener(e -> {
            int selectedRow = expenseTable.getSelectedRow();
            if (selectedRow >= 0) {
                int expenseId = Integer.parseInt(expenseTable.getValueAt(selectedRow, 0).toString());
                expenseController.deleteExpense(expenseId);
                refreshExpenseTable();
                JOptionPane.showMessageDialog(this, "Expense deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Please select an expense to delete.");
            }
        });
=======
    private JPanel createExpensePanel() {
        JPanel expensesPanel = new JPanel(new BorderLayout(5, 5));
        expensesPanel.setBorder(new TitledBorder(new LineBorder(Color.GRAY, 1, true), "Expenses",
                TitledBorder.LEADING, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.DARK_GRAY));
        String[] columnNames = {"ID", "Description", "Category", "Amount", "Date"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        expenseTable = new JTable(tableModel);
        expenseTable.setName("expenseTable");
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(expenseTable);
        expensesPanel.add(scrollPane, BorderLayout.CENTER);
        return expensesPanel;
>>>>>>> c57b429e11f3ff35ea6189b73561bd208d5c3be5
    }

    public void refreshExpenseTable() {
        try {
            tableModel = (DefaultTableModel) expenseTable.getModel();
            tableModel.setRowCount(0); // Clear existing rows
            List<Expense> expenses = expenseController.getAllExpenses();
            for (Expense expense : expenses) {
                tableModel.addRow(new Object[]{
                        expense.getId(),
                        expense.getDescription(),
                        expense.getCategory(),
                        expense.getAmount(),
                        expense.getDate()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load expenses: " + e.getMessage());
        }
    }

    public JButton getAddExpenseButton() {
        return addExpenseButton;
    }

    public void setAddExpenseButton(JButton addExpenseButton) {
        this.addExpenseButton = addExpenseButton;
    }

    public JButton getUpdateExpenseButton() {
        return updateExpenseButton;
    }

    public void setUpdateExpenseButton(JButton updateExpenseButton) {
        this.updateExpenseButton = updateExpenseButton;
    }

    public JButton getDeleteExpenseButton() {
        return deleteExpenseButton;
    }

    public void setDeleteExpenseButton(JButton deleteExpenseButton) {
        this.deleteExpenseButton = deleteExpenseButton;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(DefaultTableModel tableModel) {
        this.tableModel = tableModel;
    }
}
