package com.personal_expense_tracker.main;


import com.personal_expense_tracker.main.controller.ExpenseController;
import com.personal_expense_tracker.main.repository.ExpenseRepository;
import com.personal_expense_tracker.main.utils.DatabaseConnection;
import com.personal_expense_tracker.main.view.ExpenseView;
import picocli.CommandLine;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@CommandLine.Command(mixinStandardHelpOptions = true)
public class App implements Callable<Void> {

    public static void main(String[] args) {
        new CommandLine(new App()).execute(args);
    }

    @Override
    public Void call() {
        EventQueue.invokeLater(() -> {
            try (Connection connection = DatabaseConnection.connect()) {
                ExpenseRepository expenseRepository = new ExpenseRepository(connection);
                ExpenseController expenseController = new ExpenseController(expenseRepository);
                ExpenseView expenseView = new ExpenseView(expenseController);
                expenseView.setVisible(true);
                //new MainUI(connection).setVisible(true);
//                SwingUtilities.invokeLater(() -> {
//                });

//                synchronized (MainUI.class) {
//                    MainUI.class.wait();
//                }
            } catch (Exception e) {
                Logger.getLogger(getClass().getName())
                        .log(Level.SEVERE, "Exception", e);
                JOptionPane.showMessageDialog(null, "Failed to connect to the database: " + e.getMessage());
            }
        });
        return null;
    }

    /*
    * @Override
    public Void call() throws Exception {
        EventQueue.invokeLater(() -> {
            try {
                OrderMongoRepository orderRepository = new OrderMongoRepository(
                    new MongoClient(new ServerAddress(mongoHost, mongoPort)), databaseName, collectionName);
                OrderController orderController = new OrderController(orderRepository);
                OrderManagerViewSwingImpl orderView = new OrderManagerViewSwingImpl(orderController);
                orderView.setVisible(true);
            } catch (Exception e) {
                Logger.getLogger(getClass().getName())
                    .log(Level.SEVERE, "Exception", e);
            }
        });
        return null;
    }
    *
    * */
}
