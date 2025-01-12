package com.personal_expense_tracker.main.model;

import java.time.LocalDate;
import java.util.Objects;

public class Expense {
    private int id;
    private String description;
    private String category;
    private double amount;
    private LocalDate date;

    public Expense(int id, String description, String category, double amount, LocalDate date) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    public Expense() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Double.compare(expense.amount, amount) == 0 &&
                Objects.equals(description, expense.description) &&
                Objects.equals(category, expense.category) &&
                Objects.equals(date, expense.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, category, amount, date);
    }
}
