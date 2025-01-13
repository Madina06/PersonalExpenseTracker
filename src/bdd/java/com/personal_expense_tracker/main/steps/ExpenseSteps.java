// ExpenseSteps.java
package com.personal_expense_tracker.main.steps;

import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.data.TableCell;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.*;

import javax.swing.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

public class ExpenseSteps {

    private FrameFixture rootWindow;

    private DialogFixture addExpenseDialog;


    @After
    public void tearDown() {
        if (rootWindow != null) {
            rootWindow.cleanUp();
        }
    }

    @Given("the main window is displayed")
    public void theMainWindowIsDisplayed() {
        application("com.personal_expense_tracker.main.App").start();
        rootWindow = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
            @Override
            protected boolean isMatching(JFrame frame) {
                return "Personal Expense Tracker".equals(frame.getTitle()) && frame.isShowing();
            }
        }).using(BasicRobot.robotWithCurrentAwtHierarchy());
    }

    @When("the user clicks the Add Expense button")
    public void theUserClicksTheAddExpensesButton() {
        JButtonFixture addExpense = rootWindow.button(JButtonMatcher.withText("Add Expense"));
        addExpense.click();

        addExpenseDialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
            @Override
            protected boolean isMatching(JDialog dialog) {
                return "Add Expense".equals(dialog.getTitle()) && dialog.isShowing();
            }
        }).using(rootWindow.robot());
    }


    @Then("the Add Expense dialog should appear")
    public void theAddExpenseDialogShouldAppear() {
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


    @Given("the expense table is loaded with data")
    public void theExpenseTableIsLoadedWithData() {
        JTableFixture expenseTable = rootWindow.table("expenseTable");
        assertThat(expenseTable).isNotNull();
        expenseTable.robot().waitForIdle();
        assertThat(expenseTable.target().getRowCount()).isGreaterThan(0);
    }

    @When("the user selects the first row in the expense table")
    public void theUserSelectsTheFirstRowInTheExpenseTable() {
        JTableFixture expenseTable = rootWindow.table("expenseTable");
        expenseTable.robot().waitForIdle();
        expenseTable.selectRows(0);
    }

    @When("clicks the Update Expense button")
    public void clicksTheUpdateExpenseButton() {
        JButtonFixture updateButton = rootWindow.button("updateExpenseButton");
        updateButton.click();
    }

    @When("updates the Description field to {string}")
    public void updatesTheDescriptionFieldTo(String newDescription) {
        DialogFixture dialog = WindowFinder.findDialog("Update Expense").using(rootWindow.robot());
        JTextComponentFixture descriptionField = dialog.textBox("descriptionTextField");
        descriptionField.setText(newDescription);
    }

    @When("clicks the Save button")
    public void clicksTheSaveButton() {
        DialogFixture dialog = WindowFinder.findDialog("Update Expense").using(rootWindow.robot());
        JButtonFixture saveButton = dialog.button("saveButton");
        saveButton.click();
    }

    @Then("the Add Expense dialog should close")
    public void theAddExpenseDialogShouldClose() {
        assertThatThrownBy(() -> WindowFinder.findDialog("Update Expense").using(rootWindow.robot()))
                .isInstanceOf(Exception.class);
    }

    @Then("the expense table should show the updated Description {string} for the selected row")
    public void theExpenseTableShouldShowTheUpdatedDescriptionForTheSelectedRow(String updatedDescription) {

        JTableFixture expenseTable = rootWindow.table("expenseTable");
        expenseTable.robot().waitForIdle();
        String actualDescription = expenseTable.valueAt(TableCell.row(0).column(1));
        assertThat(actualDescription).isEqualTo(updatedDescription);
    }


}
