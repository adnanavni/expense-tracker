package fi.metropolia.expensetracker.module;

import java.util.Currency;
import java.util.Date;
/**
 * Expense-object, which includes info about a single expense of a budget.
 * */
public class Expense {
    private Double price;
    private String type;
    private Integer id;
    private Date date;

    /**
     * Reference to LocalizationManager - singleton, which is used to localize this Salary -object as well.
     * */
    private LocalizationManager lan = LocalizationManager.getInstance();

    public Expense(Integer id, Double price, String type, Date date) {
        this.id = id;
        this.price = price;
        this.type = type;
        this.date = date;
    }
    /**
     * @return expense-object in String-type with all expense info
     * */
    public String toString() {
        Currency currency = Currency.getInstance(Variables.getInstance().getCurrentCurrency());
        type = type.toLowerCase();
        return lan.getString(type) + lan.getString("addedon") + date + lan.getString("moneySpent") + String.format("%.2f", price) + " " + currency.getSymbol();
    }

    public Date getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getId() {
        return id;
    }
}
