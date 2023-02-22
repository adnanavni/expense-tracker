package fi.metropolia.expensetracker.module;

public class ConstantExpense {
    private String type;
    private Double amount;
    private String usedCurrency;

    public ConstantExpense(String type, Double amount, String usedCurrency){
        this.type = type;
        this.amount = amount;
        this.usedCurrency = usedCurrency;
    }

    public void setUsedCurrency(String usedCurrency) {
        this.usedCurrency = usedCurrency;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType(){
        return type;
    }
    public Double getAmount(){
        return amount;
    }

    public String getUsedCurrency() {
        return usedCurrency;
    }

    public String toString(){
        return type + " " + String.format("%.2f", amount) + " " + usedCurrency;
    }
}
