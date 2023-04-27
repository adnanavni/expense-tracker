package fi.metropolia.expensetracker.module;

import java.util.Currency;

public class ConstantExpense {
    private String type;
    private Double amount;
    private Integer id;

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

    public String toString() {
        Currency currency = Currency.getInstance(Variables.getInstance().getCurrentCurrency());
        type = type.toLowerCase();
        if (type.equals("water bill") || type.equals("waterbill")) type = "waterBill";
        if (type.equals("car payment") || type.equals("carpayment")) type = "carPayment";
        if (type.equals("cell phone") || type.equals("cellphone")) type = "cellPhone";
        return lan.getString(type) + " " + String.format("%.2f", amount) + " " + currency.getSymbol();
    }
}
