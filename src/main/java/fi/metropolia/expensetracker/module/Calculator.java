package fi.metropolia.expensetracker.module;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Currency;

public class Calculator {
    private Double currentCourseMultiplier = 1.00;
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
        switch(course){
            case("SEK"):
                budget = budget/currentCourseMultiplier;
                income = income/currentCourseMultiplier;
                expense = expense/currentCourseMultiplier;
                currentCourseMultiplier = 11.1091;
                budget = budget*currentCourseMultiplier;
                income = income*currentCourseMultiplier;
                expense = expense*currentCourseMultiplier;
                expense = Math.round(expense * 100.0) / 100.0;
                budget = Math.round(budget * 100.0) / 100.0;
                income = Math.round(income * 100.0) / 100.0;
                break;
            case("EUR"):
                budget = budget/currentCourseMultiplier;
                income = income/currentCourseMultiplier;
                expense = expense/currentCourseMultiplier;
                currentCourseMultiplier = 1.00;
                budget = budget*currentCourseMultiplier;
                income = income*currentCourseMultiplier;
                expense = expense*currentCourseMultiplier;
                expense = Math.round(expense * 100.0) / 100.0;
                budget = Math.round(budget * 100.0) / 100.0;
                income = Math.round(income * 100.0) / 100.0;
                break;
            case("USD"):
                budget = budget/currentCourseMultiplier;
                income = income/currentCourseMultiplier;
                expense = expense/currentCourseMultiplier;
                currentCourseMultiplier = 1.08;
                budget = budget*currentCourseMultiplier;
                income = income*currentCourseMultiplier;
                expense = expense*currentCourseMultiplier;
                expense = Math.round(expense * 100.0) / 100.0;
                budget = Math.round(budget * 100.0) / 100.0;
                income = Math.round(income * 100.0) / 100.0;
                break;
        }
    }
    public Double getCurrentCourseMultiplier(){return currentCourseMultiplier;}
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
