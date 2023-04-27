package fi.metropolia.expensetracker.module;

import com.almasb.fxgl.core.serialization.Bundle;

import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class Expense {
    private Double price;
    private String type;
    private Integer id;
    private Date date;

    private LocalizationManager lan = LocalizationManager.getInstance();

    public Expense(Integer id, Double price, String type, Date date) {
        this.id = id;
        this.price = price;
        type = type.toLowerCase();
        this.type = type;
        this.date = date;
    }

    public String toString() {
        Currency currency = Currency.getInstance(Variables.getInstance().getCurrentCurrency());
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
