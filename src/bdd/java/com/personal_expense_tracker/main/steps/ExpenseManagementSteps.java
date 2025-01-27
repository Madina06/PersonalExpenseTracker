package com.personal_expense_tracker.main.steps;

import static org.assertj.swing.launcher.ApplicationLauncher.application;

import javax.swing.JFrame;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ExpenseManagementSteps {

    private FrameFixture window;

    @After
    public void tearDown() {
        if (window != null) {
            window.cleanUp();
        }
    }

    @Given("the application is running")
    public void theApplicationIsRunning() {
        application("com.personal_expense_tracker.main.App").start();
        window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
            @Override
            protected boolean isMatching(JFrame frame) {
                return "Personal Expense Tracker".equals(frame.getTitle()) && frame.isShowing();
            }
        }).using(BasicRobot.robotWithCurrentAwtHierarchy());
    }

    @When("the user clicks the {string} button")
    public void theUserClicksTheButton(String buttonText) {
        window.button(JButtonMatcher.withText(buttonText)).click();
    }
    
    @When("the user clicks the dialog {string} button")
    public void theUserClicksTheDialogButton(String buttonText) {
        window.dialog().button(JButtonMatcher.withText(buttonText)).click();
    }

    @When("the user enters {string} as the description, {string} as the category, {string} as the amount, and {string} as the date")
    public void theUserEntersDetails(String description, String category, String amount, String date) {
        window.dialog().textBox("descriptionTextField").setText(description);
        window.dialog().textBox("categoryTextField").setText(category);
        window.dialog().textBox("amountTextField").setText(amount);
        window.dialog().textBox("dateField").setText(date);
    }

    @Then("the expense table should contain {int} row")
    public void theExpenseTableShouldContainRow(int rowCount) {
        window.table("expenseTable").requireRowCount(rowCount);
    }

    @Then("the row should display {string}, {string}, {string}, {string}")
    public void theRowShouldDisplay(String description, String category, String amount, String date) {
        String[] expectedRow = { "1", description, category, amount, date };
        window.table("expenseTable").requireContents(new String[][] { expectedRow });
    }

    @Given("an expense with description {string}, category {string}, amount {string}, and date {string} exists")
    public void anExpenseWithDetailsExists(String description, String category, String amount, String date) {
        window.dialog().textBox("descriptionTextField").setText(description);
        window.dialog().textBox("categoryTextField").setText(category);
        window.dialog().textBox("amountTextField").setText(amount);
        window.dialog().textBox("dateField").setText(date);
    }

    @When("the user selects the expense with description {string}")
    public void theUserSelectsTheExpense(String description) {
        int rowIndex = findRowByDescription(description);
        window.table("expenseTable").selectRows(rowIndex);
    }

    private int findRowByDescription(String description) {
        String[][] tableContents = window.table("expenseTable").contents();
        for (int i = 0; i < tableContents.length; i++) {
            if (tableContents[i][1].equals(description)) {
                return i;
            }
        }
        throw new RuntimeException("Row with description " + description + " not found");
    }

    @When("the user updates the description to {string}, category to {string}, amount to {string}, and date to {string}")
    public void theUserUpdatesDetails(String description, String category, String amount, String date) {
        window.dialog().textBox("descriptionTextField").setText(description);
        window.dialog().textBox("categoryTextField").setText(category);
        window.dialog().textBox("amountTextField").setText(amount);
        window.dialog().textBox("dateField").setText(date);
    }

    @Then("the expense table should be empty")
    public void theExpenseTableShouldBeEmpty() {
        window.table("expenseTable").requireRowCount(0);
    }
    
    @Then("a confirmation dialog with message {string} should appear")
    public void aConfirmationDialogWithMessageShouldAppear(String expectedMessage) {
        window.optionPane().requireMessage(expectedMessage);
        window.optionPane().okButton().click();
    }
}