package com.personal_expense_tracker.main;


import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import com.personal_expense_tracker.main.controller.ExpenseController;
import com.personal_expense_tracker.main.repository.ExpenseRepository;
import com.personal_expense_tracker.main.utils.DatabaseConnection;
import com.personal_expense_tracker.main.view.ExpenseView;

import picocli.CommandLine;

@CommandLine.Command(mixinStandardHelpOptions = true)
public class App implements Callable<Void> {

    public static void main(String[] args) {
        new CommandLine(new App()).execute(args);
    }

    @Override
    public Void call() {
        EventQueue.invokeLater(() -> {
            try {
                Connection connection = DatabaseConnection.connect();
                ExpenseRepository expenseRepository = new ExpenseRepository(connection);
                ExpenseController expenseController = new ExpenseController(expenseRepository);
                ExpenseView expenseView = new ExpenseView(expenseController);
                expenseView.setVisible(true);
            } catch (SQLException e) {
                Logger.getLogger(getClass().getName())
                        .log(Level.SEVERE, "Exception", e);
                JOptionPane.showMessageDialog(null, "Failed to connect to the database: " + e.getMessage());
            }

        });
        return null;
    }
}
