package fi.metropolia.expensetracker.module;

public class Calculator {

    private Integer budget = 0;
    private Integer expense = 0;
    private Integer income = 0;

    public void calulate(String calculation, Integer amount) {
        switch (calculation) {
            case("addToBudget"):
                budget += amount;
                break;
            case("addWithIncome"):
                income += amount;
                budget += income;
                break;
            case("addToIncome"):
                income += amount;
                break;
            case("subtractFromBudget"):
                budget -= amount;
                break;
            case("subtractWithExpenses"):
                expense += amount;
                budget -= expense;
                break;
        }
    }

    public Integer getBudget() {
        return budget;
    }

    public Integer getExpense() {
        return expense;
    }

    public Integer getIncome() {
        return income;
    }
}
