package com.personal_expense_tracker.main.view;

import com.personal_expense_tracker.main.controller.ExpenseController;
import com.personal_expense_tracker.main.model.Expense;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ExpenseView extends JFrame {
	private JOptionPaneFactory optionPaneFactory = JOptionPane::showMessageDialog;
    
    public void setOptionPaneFactory(JOptionPaneFactory factory) {
        this.optionPaneFactory = factory;
    }

    private void showMessage(String message) {
        optionPaneFactory.showMessage(this, message);
    }

    private JPanel contentPane;
    public JTable expenseTable;
    JButton addExpenseButton;
    JButton updateExpenseButton;
    JButton deleteExpenseButton;
    private JList<Expense> listAllExpenses;
    DefaultListModel<Expense> listAllExpensesModel;

    private final ExpenseController expenseController;

    public ExpenseView(ExpenseController expenseController) {
        this.expenseController = expenseController;

        setTitle("Personal Expense Tracker");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel allExpensesPanel = new JPanel(new BorderLayout(15, 15));
        allExpensesPanel.setBorder(new TitledBorder(new LineBorder(Color.GRAY,
                1, true), "All Expenses",
                TitledBorder.LEADING, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14), Color.DARK_GRAY));
        setContentPane(allExpensesPanel);

        String[] columnNames = {"ID", "Description", "Category", "Amount", "Date"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        expenseTable = new JTable(tableModel);
        expenseTable.setName("expenseTable");
        JScrollPane scrollPane = new JScrollPane(expenseTable);

        JPanel buttonPanel = new JPanel();
        
        expenseTable = new JTable(tableModel);
        expenseTable.setName("expenseTable");
        
        addExpenseButton = new JButton("Add Expense");
        addExpenseButton.setName("addExpenseButton");
        addExpenseButton.setBackground(new Color(76, 175, 80));


        updateExpenseButton = new JButton("Update Expense");
        updateExpenseButton.setName("updateExpenseButton");
        updateExpenseButton.setBackground(new Color(33, 150, 243));

        deleteExpenseButton = new JButton("Delete Expense");
        deleteExpenseButton.setName("deleteExpenseButton");
        deleteExpenseButton.setBackground(new Color(244, 67, 54));

        buttonPanel.add(addExpenseButton);
        buttonPanel.add(updateExpenseButton);
        buttonPanel.add(deleteExpenseButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

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
            	showMessage("Please select an expense to update.");
            }
        });

        deleteExpenseButton.addActionListener(e -> {
            int selectedRow = expenseTable.getSelectedRow();
            if (selectedRow >= 0) {
                int expenseId = Integer.parseInt(expenseTable.getValueAt(selectedRow, 0).toString());
                expenseController.deleteExpense(expenseId);
                refreshExpenseTable();
                showMessage( "Expense deleted successfully!");
            } else {
                showMessage("Please select an expense to delete.");
            }
        });
    }

    public void refreshExpenseTable() {
        try {
            DefaultTableModel tableModel = (DefaultTableModel) expenseTable.getModel();
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
        	showMessage("Failed to load expenses: " + e.getMessage());
        }
    }

}