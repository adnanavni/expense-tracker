package fi.metropolia.expensetracker.module;

import java.util.ArrayList;

public class Budget {

    private double amount;
    private String name;
    private ArrayList<Expense> expenses = new ArrayList<Expense>();

    private Integer id;


    public Budget(Integer id, Double amount, String name) {
        this.id = id;
        this.amount = amount;
        this.name = name;

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

    public void resetExpenses(){
        expenses = new ArrayList<>();
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

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getAmount() {
        return amount;
    }

    public Integer getId() {
        return id;
    }
}
