package fi.metropolia.expensetracker.module;

import java.time.LocalDate;
import java.util.Date;


public class Expense {
    private Double price;
    private String type;
    private static Integer currentId = 1;
    private Integer id;
    private LocalDate date;
    private String usedCurrency;


    public Expense(Double price, String type, LocalDate date, String usedCurrency) {
        this.price = price;
        this.type = type;
        this.date = date;
        this.usedCurrency = usedCurrency;

        id = currentId;
        currentId++;
    }

    public String toString() {
        return type + ", added on: " + date + ", money spent: " + String.format("%.2f", price) + " " + usedCurrency;
    }
    public LocalDate getDate(){
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

    public void setUsedCurrency(String currency) {
        usedCurrency = currency;
    }

}
