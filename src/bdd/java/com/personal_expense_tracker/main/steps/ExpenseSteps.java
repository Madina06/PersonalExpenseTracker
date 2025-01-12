package com.personal_expense_tracker.main.steps;

import io.cucumber.java.After;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;

import javax.swing.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

public class ExpenseSteps {

//    private Connection connection;
//    private ExpenseController expenseController;
//    private Expense expense;
//
//    public void cleanDatabase() throws Exception {
//        connection = DatabaseConnection.connect();
//        try (Statement stmt = connection.createStatement()) {
//            stmt.execute("DELETE FROM expenses");
//        }
//    }

    private FrameFixture window;

    @After
    public void tearDown() {
        if (window != null) {
            window.cleanUp();
        }
    }

    @When("The Expense View is shown")
    public void the_Expense_View_is_shown() {
        application("com.personal_expense_tracker.main.view.MainUI")
                .start();
        window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
            @Override
            protected boolean isMatching(JFrame frame) {
                return "Personal Expense Tracker".equals(frame.getTitle()) && frame.isShowing();
            }
        }).using(BasicRobot.robotWithCurrentAwtHierarchy());
    }

    @Then("The displayed expenses should match the expenses in the database")
    public void the_displayed_expenses_should_match_the_expenses_in_the_database() {
        List<String> expectedExpenses = Arrays.asList(
                "ID: 100    Customer: Customer A    Company: Company A    Product: Product A    Status: Pending",
                "ID: 200    Customer: Customer B    Company: Company B    Product: Product A    Status: Pending");

        List<String> displayedOrders = new ArrayList<>();

        for (int i = 0; i < window.list("listAllOrders").contents().length; i++) {
            String orderHtml = window.list("listAllOrders").contents()[i];
            String cleanedOrder = orderHtml.replaceAll("<.*?>", "").replaceAll("\\s+", " ").replace("&nbsp;", " ");
            displayedOrders.add(cleanedOrder.trim());
        }

        assertThat(displayedOrders).containsExactlyElementsOf(expectedExpenses);
    }


//    @Given("the application is running")
//    public void theApplicationIsRunning() throws Exception {
//        connection = DatabaseConnection.connect();
//        cleanDatabase();
//        ExpenseRepository expenseRepository = new ExpenseRepository(connection);
//        expenseController = new ExpenseController(expenseRepository);
//    }
//
//    @When("I add an expense with description {string}, amount {string}, category {string}, and date {string}")
//    public void iAddAnExpenseWithDescriptionAmountCategoryAndDate(String description, String amount, String category, String date) {
//        expense = new Expense();
//        expense.setDescription(description);
//        expense.setAmount(Double.parseDouble(amount));
//        expense.setCategory(category);
//        expense.setDate(LocalDate.parse(date));
//        expenseController.addExpense(expense);
//    }
//
//    @Then("the expense should be added to the database")
//    public void theExpenseShouldBeAddedToTheDatabase() {
//        Expense fetchedExpense = expenseController.getAllExpenses().get(0);
//        assertNotNull(fetchedExpense);
//        assertEquals(expense.getDescription(), fetchedExpense.getDescription());
//    }
//
//    @Given("an expense with description {string} exists in the database")
//    public void anExpenseExistsInTheDatabase(String description) {
//        expense = new Expense();
//        expense.setId(1);
//        expense.setDescription(description);
//        expense.setAmount(51.0);
//        expense.setCategory("Food");
//        expense.setDate(LocalDate.of(2023, 5, 1));
//        expenseController.addExpense(expense);
//    }
//
//    @When("I update the expense description to {string}")
//    public void iUpdateTheExpenseDescription(String newDescription) {
//        expense = expenseController.getAllExpenses().stream()
//                .findFirst()
//                .orElse(null);
//        assertNotNull(expense);
//        expense.setDescription(newDescription);
//        expenseController.updateExpense(expense);
//    }
//
//    @Then("the expense description should be updated in the database")
//    public void theExpenseDescriptionShouldBeUpdatedInTheDatabase() {
//        Expense fetchedExpense = expenseController.getAllExpenses().stream()
//                .findFirst()
//                .orElse(null);
//        assertNotNull(fetchedExpense);
//        assertEquals(expense.getDescription(), fetchedExpense.getDescription());
//    }

}
