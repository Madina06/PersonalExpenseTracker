package com.personal_expense_tracker.main.steps;

import com.personal_expense_tracker.main.controller.ExpenseController;
import com.personal_expense_tracker.main.model.Expense;
import com.personal_expense_tracker.main.repository.ExpenseRepository;
import com.personal_expense_tracker.main.utils.DatabaseConnection;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.sql.Connection;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ExpenseSteps {

    private Connection connection;
    private ExpenseController expenseController;
    private Expense expense;

    @Given("the application is running")
    public void theApplicationIsRunning() throws Exception {
        connection = DatabaseConnection.connect();
        ExpenseRepository expenseRepository = new ExpenseRepository(connection);
        expenseController = new ExpenseController(expenseRepository);
    }

    @When("I add an expense with description {string}, amount {string}, category {string}, and date {string}")
    public void iAddAnExpenseWithDescriptionAmountCategoryAndDate(String description, String amount, String category, String date) {
        expense = new Expense();
        expense.setDescription(description);
        expense.setAmount(Double.parseDouble(amount));
        expense.setCategory(category);
        expense.setDate(LocalDate.parse(date));
        expenseController.addExpense(expense);
    }

    @Then("the expense should be added to the database")
    public void theExpenseShouldBeAddedToTheDatabase() {
        Expense fetchedExpense = expenseController.getAllExpenses().get(0);
        assertNotNull(fetchedExpense);
        assertEquals(expense.getDescription(), fetchedExpense.getDescription());
    }


    @Given("an expense with description {string} exists in the database")
    public void anExpenseExistsInTheDatabase(String description) {
        expense = new Expense();
        expense.setDescription(description);
        expense.setAmount(50.0);
        expense.setCategory("Food");
        expense.setDate(LocalDate.of(2023, 5, 1));
        expenseController.addExpense(expense);
    }

    @When("I update the expense description to {string}")
    public void iUpdateTheExpenseDescription(String newDescription) {
        expense.setDescription(newDescription);
        expenseController.updateExpense(expense);
    }

    @Then("the expense description should be updated in the database")
    public void theExpenseDescriptionShouldBeUpdatedInTheDatabase() {
        Expense fetchedExpense = expenseController.getAllExpenses().get(0);
        assertEquals(expense.getDescription(), fetchedExpense.getDescription());
    }


}
