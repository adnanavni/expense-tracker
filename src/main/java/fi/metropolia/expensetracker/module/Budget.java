package fi.metropolia.expensetracker.module;

public class Budget {

    private double amount;
    private String name;

    public Budget(Double amount, String name) {
        this.amount = amount;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Double getAmount() {
        return amount;
    }
}
