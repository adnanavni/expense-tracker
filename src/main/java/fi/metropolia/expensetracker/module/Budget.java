package fi.metropolia.expensetracker.module;

import java.util.ArrayList;

/**
 * Budget-object, which represents a budget created by the user
 * */
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

    public Budget(Double amount, String name) {
        this.amount = amount;
        this.name = name;
    }

    /**
     * Adds an expense to the budgets expense list
     * @param expense data of the expense to be added
     * */
    public void addExpenseToBudget(Expense expense) {
        expenses.add(expense);
    }

    /**
     * Removes an expense from the budgets expense list
     * @param expense data of the expense to be removed
     * */
    public void removeExpenseFromBudget(Expense expense) {
        expenses.remove(expense);
    }

    public ArrayList<Expense> getExpenses() {
        return expenses;
    }

    /**
     * Creates an empty ArrayList for expenses to reset the expenses of the budget
     * */
    public void resetExpenses() {
        expenses = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }
}
