package com.personal_expense_tracker.main.view;


import com.personal_expense_tracker.main.controller.ExpenseController;
import com.personal_expense_tracker.main.repository.ExpenseRepository;
import com.personal_expense_tracker.main.utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class MainUI extends JFrame {
	

    private JButton manageExpensesButton;
    private final ExpenseController expenseController;

    public MainUI(Connection connection) {
    	
    	
        setTitle("Personal Expense Tracker");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 1));

        // Controllers
        ExpenseRepository expenseRepository = new ExpenseRepository(connection);
        expenseController = new ExpenseController(expenseRepository);
        
        try {
            expenseRepository.createTableIfNotExists();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to initialize database: " + e.getMessage());
            System.exit(1); // Завершаем приложение, если база данных не инициализируется
        }

        // Buttons
        manageExpensesButton = new JButton("Manage Expenses");

        add(manageExpensesButton);

        // Event Listeners
        manageExpensesButton.addActionListener(e -> new ExpenseView(expenseController).setVisible(true));
        
    }

    public static void main(String[] args) {
    	
        
            try (Connection connection = DatabaseConnection.connect()) {
                new MainUI(connection).setVisible(true);
                SwingUtilities.invokeLater(() -> {
                });
                
                synchronized (MainUI.class) {
					MainUI.class.wait();
				}
            } catch (Exception e) {
            	
                JOptionPane.showMessageDialog(null, "Failed to connect to the database: " + e.getMessage());
            
            }

    }
}
