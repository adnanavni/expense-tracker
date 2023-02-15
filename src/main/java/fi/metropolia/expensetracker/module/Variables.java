package fi.metropolia.expensetracker.module;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public class Variables {
    private static Variables INSTANCE = null;

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

    Map<String, Double> categories = new HashMap<>() {{
        put("Groceries", 0.00);
        put("Restaurants", 0.00);
        put("Hobbies", 0.00);
        put("Clothes", 0.00);
        put("Well-being", 0.00);
        put("Medicines", 0.00);
        put("Other", 0.00);
    }};

    private Double currentCourseMultiplier = 1.00;
    private String currentCurrency = "EUR";

    private Double budget = 0.00;
    private Double expense = 0.00;
    private Double income = 0.00;

    private Variables() {
    }

    public static Variables getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Variables();
        return INSTANCE;
    }

    public void calculate(String calculation, Double amount) {
        switch (calculation) {
            case ("addToBudget"):
                budget += amount;
                break;
            case ("addWithIncome"):
                income += amount;
                budget += amount;
                break;
            case ("addToIncome"):
                income += amount;
                break;
            case ("subtractFromBudget"):
                budget -= amount;
                break;
            case ("subtractWithExpenses"):
                expense += amount;
                budget -= amount;
                break;
            case ("subtractExpense"):
                expense -= amount;
                budget += amount;
                break;
        }
    }

    public void setCategories(String category, Double amount, Boolean addMoney) {
        double newAmount = categories.get(category);
        if(addMoney){
            newAmount += amount;
        }
        else {
            newAmount -= amount;
        }
        newAmount += amount;
        categories.put(category, newAmount);
    }

    public void setCurrentCourseMultiplier(String course) {

        budget = budget / currentCourseMultiplier;
        income = income / currentCourseMultiplier;
        expense = expense / currentCourseMultiplier;

        currentCourseMultiplier = currencies.get(course);
        currentCurrency = course;

        budget = budget * currentCourseMultiplier;
        income = income * currentCourseMultiplier;
        expense = expense * currentCourseMultiplier;
    }

    public String getCurrentCurrency() {
        return currentCurrency;
    }

    public ArrayList<String> getCurrencyCodes() {
        return new ArrayList<>(currencies.keySet());
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

    public ArrayList<String> getTopics() {
        return new ArrayList<>(categories.keySet());
    }
}