package fi.metropolia.expensetracker.module;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static java.util.Map.entry;

public class Calculator {
    private Double currentCourseMultiplier = 1.00;
    private String currentCurrency = "EUR";
    Map<String, Double> currencies = Map.ofEntries(
            entry("EUR", 1.00),
            entry("USD", 1.08),
            entry("SEK", 11.1091),
            entry("JPY", 140.64),
            entry("ISK", 152.58),
            entry("CAD", 1.43),
            entry("RUB", 78.21),
            entry("CHF", 0.99),
            entry("NOK", 10.84),
            entry("DKK", 7.45),
            entry("GBP", 0.89)
    );

    private Double budget = 0.00;
    private Double expense = 0.00;
    private Double income = 0.00;

    public void calulate(String calculation, Double amount) {
        switch (calculation) {
            case("addToBudget"):
                budget += amount;
                budget = Math.round(budget * 100.0) / 100.0;
                break;
            case("addWithIncome"):
                income += amount;
                budget += income;

                budget = Math.round(budget * 100.0) / 100.0;
                income = Math.round(income * 100.0) / 100.0;
                break;
            case("addToIncome"):
                income += amount;
                income = Math.round(income * 100.0) / 100.0;
                break;
            case("subtractFromBudget"):
                budget -= amount;
                budget = Math.round(budget * 100.0) / 100.0;
                break;
            case("subtractWithExpenses"):
                expense += amount;
                budget -= expense;

                budget = Math.round(budget * 100.0) / 100.0;
                expense = Math.round(expense * 100.0) / 100.0;
                break;
        }
    }

    public void setCurrentCourseMultiplier(String course){

        budget = budget/currentCourseMultiplier;
        income = income/currentCourseMultiplier;
        expense = expense/currentCourseMultiplier;

        currentCourseMultiplier = currencies.get(course);
        currentCurrency = course;

        budget = budget*currentCourseMultiplier;
        income = income*currentCourseMultiplier;
        expense = expense*currentCourseMultiplier;

        expense = Math.round(expense * 100.0) / 100.0;
        budget = Math.round(budget * 100.0) / 100.0;
        income = Math.round(income * 100.0) / 100.0;

    }
    public Double getCurrentCourseMultiplier(){return currentCourseMultiplier;}
    public String getCurrentCurrency(){return currentCurrency;}
    public ArrayList<String> getCurrencyCodes(){
        return new ArrayList<String>(currencies.keySet());
    }
    public Double getBudget() {
        return budget;
    }

    public Double getExpense() {
        return expense;
    }

    public Double getIncome() {
        return income;
    }
}
