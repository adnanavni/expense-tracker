package fi.metropolia.expensetracker.module;

import java.util.ArrayList;

public class Budget {

    private double amount;
    private String name;
    private ArrayList<Expense> expenses = new ArrayList<Expense>();
    private static Integer currentId = 1;
    private Integer id;
    private String currency;

    public Budget(Double amount, String name, String currency) {
        this.amount = amount;
        this.name = name;
        this.currency = currency;
        id = currentId;
        currentId++;
    }

    public void addExpenseToBudget(Expense expense) {
        expenses.add(expense);
    }

    public void removeExpenseFromBudget(Expense expense) {
        expenses.remove(expense);
    }

    public ArrayList<Expense> getExpenses() {
        return expenses;
    }

    public String getName() {
        return name;
    }

    public void increaseAmount(Double amount) {
        this.amount += amount;
    }

    public void decreaseAmount(Double amount) {
        this.amount -= amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Double getAmount() {
        return amount;
    }

    public Integer getId() {
        return id;
    }
}