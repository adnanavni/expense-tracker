package fi.metropolia.expensetracker.module;

import java.time.LocalDate;


public class Expense {
    private String price;
    private String type;
    private static Integer currentId = 1;
    private Integer id;
    private LocalDate date;
    private String usedCurrency;

    public Expense(String price, String type, LocalDate date, String usedCurrency){
        this.price = price;
        this.type = type;
        this.date = date;
        this.usedCurrency = usedCurrency;
        id = currentId;
        currentId++;
    }

    public String toString(){
        return type + ", added on: " + date + ", money spent: " + price + " " + usedCurrency;
    }
}
