// ExpenseSteps.java
package com.personal_expense_tracker.main.steps;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;

import javax.swing.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

public class ExpenseSteps {

    private FrameFixture window;

    @After
    public void tearDown() {
        if (window != null) {
            window.cleanUp();
        }
    }

    @When("The Expense View is shown")
    public void theExpenseViewIsShown() {
        application("com.personal_expense_tracker.main.App").start();
        window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
            @Override
            protected boolean isMatching(JFrame frame) {
                return "Personal Expense Tracker".equals(frame.getTitle()) && frame.isShowing();
            }
        }).using(BasicRobot.robotWithCurrentAwtHierarchy());
    }


    @When("The user clicks the {string} button")
    public void theUserClicksTheButton(String buttonText) {
        window.button(JButtonMatcher.withText(buttonText)).click();
    }

    @Then("The displayed expenses should include the added expense")
    public void theDisplayedExpensesShouldIncludeTheAddedExpense() {
        boolean found = false;
        for (String row : window.list("expensesList").contents()) {
            if (row.contains("Lunch") && row.contains("50.0") && row.contains("Food") && row.contains("2023-12-01")) {
                found = true;
                break;
            }
        }
        assertThat(found).isTrue();
    }

    @Given("The user provides expense details with description {string}, " +
            "amount {string}, category {string}, and date {string}")
    public void theUserProvidesExpenseDetailsWithDescriptionAmountCategoryAndDate(String description, String amount, String category, String date) {
        //window.textBox("descriptionTextField").enterText(description);
//        window.textBox("amountTextField").enterText(String.valueOf(amount));
//        window.textBox("categoryTextField").enterText(category);
//        window.textBox("dateTextField").enterText(date);
        System.out.println("description = " + description);
    }
}
