package com.personal_expense_tracker.main.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;

import javax.swing.*;

import static org.assertj.swing.launcher.ApplicationLauncher.application;

public class AddExpenseSteps {

    private JFrame mainFrame;
//    private AddExpenseDialog addExpenseDialog;

    private DialogFixture addExpenseDialog;

    private FrameFixture window;

    @Given("the main window is displayed")
    public void theMainWindowIsDisplayed() {
        // Создание основного окна
        //mainFrame = WindowFinder.findFrame("");
        //mainFrame.setVisible(true);
        application("com.personal_expense_tracker.main.App").start();
        window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
            @Override
            protected boolean isMatching(JFrame frame) {
                return "Personal Expense Tracker".equals(frame.getTitle()) && frame.isShowing();
            }
        }).using(BasicRobot.robotWithCurrentAwtHierarchy());
    }

    @When("the user clicks the Add Expense button")
    public void theUserClicksTheAddExpensesButton() {

        JButtonFixture addExpense = window.button(JButtonMatcher.withText("Add Expense"));
        addExpense.click();

        addExpenseDialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
            @Override
            protected boolean isMatching(JDialog dialog) {
                return "Add Expense".equals(dialog.getTitle()) && dialog.isShowing();
            }
        }).using(window.robot());
    }


    @Then("the Add Expense dialog should appear")
    public void theAddExpenseDialogShouldAppear() {
        // Проверяем, что диалог отображается
        addExpenseDialog.requireVisible();

    }

    @Given("the Add Expense dialog is open")
    public void theDialogIsOpen() {
        addExpenseDialog.requireVisible();
    }

    @When("the user enters a description {string}, category {string}, amount {string}, and date {string}")
    public void theUserEntersADescriptionCategoryAmountAndDate(String description,
                                                               String category,
                                                               String amount,
                                                               String date) {
        JTextComponentFixture descriptionField = addExpenseDialog.textBox("descriptionTextField");
        JTextComponentFixture categoryField = addExpenseDialog.textBox("categoryTextField");
        JTextComponentFixture amountField = addExpenseDialog.textBox("amountTextField");
        JTextComponentFixture dateField = addExpenseDialog.textBox("dateField");

        descriptionField.setText(description);
        categoryField.setText(category);
        amountField.setText(amount);
        dateField.setText(date);


    }

    @And("the user clicks the Save button")
    public void theUserClicksTheButton() {
        // Нажимаем кнопку Save
        JButtonFixture saveButton = addExpenseDialog.button(new GenericTypeMatcher<JButton>(JButton.class) {
            @Override
            protected boolean isMatching(JButton button) {
                return "Save".equals(button.getText());
            }
        });
        saveButton.click();

    }

    @Then("the expense should be saved with the correct description, category, amount, and date")
    public void theExpenseShouldBeSavedWithTheCorrectDescriptionCategoryAmountAndDate() {
    }
}
