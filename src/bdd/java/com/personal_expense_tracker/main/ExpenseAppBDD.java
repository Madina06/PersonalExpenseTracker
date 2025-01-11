package com.personal_expense_tracker.main;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/bdd/resources", 
        glue = "com.personal_expense_tracker.main.steps", 
        plugin = {"pretty", "html:target/cucumber-reports/cucumber.html", "json:target/cucumber-reports/cucumber.json"},
        monochrome = true
)
public class ExpenseAppBDD {

    public static final int POSTGRES_PORT =
            Integer.parseInt(System.getProperty("postgres.port", "5433")); // Укажите порт для Postgres

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install(); // Проверка потокобезопасности для Swing
    }
}
