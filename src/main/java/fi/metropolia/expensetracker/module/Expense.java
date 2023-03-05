package fi.metropolia.expensetracker.module;

import java.util.Currency;
import java.util.Date;

public class Expense {
    private Double price;
    private String type;
    private Integer id;
    private Date date;

    public Expense(Integer id, Double price, String type, Date date) {
        this.id = id;
        this.price = price;
        this.type = type;
        this.date = date;
    }

    public String toString() {
        Currency currency = Currency.getInstance(Variables.getInstance().getCurrentCurrency());
        return type + ", added on: " + date + ", money spent: " + String.format("%.2f", price) + " " + currency.getSymbol();
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
