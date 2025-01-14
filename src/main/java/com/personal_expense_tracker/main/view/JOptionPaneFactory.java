package com.personal_expense_tracker.main.view;

import java.awt.Component;

@FunctionalInterface
public interface JOptionPaneFactory {
    void showMessage(Component parentComponent, String message);
}