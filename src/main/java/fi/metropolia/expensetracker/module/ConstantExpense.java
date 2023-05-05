package fi.metropolia.expensetracker.module;

import java.util.Currency;

/**
 * Constant Expense class for the easy usage of the database.
 */

public class ConstantExpense {
    private String type;
    private Double amount;
    private Integer id;

    /**
     * for the localization of the name
     */
    private LocalizationManager lan = LocalizationManager.getInstance();

    public ConstantExpense(Integer id, String type, Double amount) {
        this.type = type;
        this.amount = amount;
        this.id = id;
    }

    public ConstantExpense() {

    }

    public String getType() {
        return type;
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

    /**
     * Type localized for the views. Checks that the constant expenses name comes right
     * @return the string for the views to use
     */
    public String toString() {
        Currency currency = Currency.getInstance(Variables.getInstance().getCurrentCurrency());
        type = type.toLowerCase();
        if (type.equals("water bill")) type = "waterbill";
        if (type.equals("car payment")) type = "carpayment";
        if (type.equals("cell phone")) type = "cellphone";
        return lan.getString(type) + " " + String.format("%.2f", amount) + " " + currency.getSymbol();
    }
}
