package fi.metropolia.expensetracker.module;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public class Variables {
    private static Variables INSTANCE = null;

    private Map<String, Double> currencies = Map.ofEntries(
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

    private Map<String, Double> categories = new HashMap<>() {{
        put("Groceries", 0.00);
        put("Restaurants", 0.00);
        put("Hobbies", 0.00);
        put("Clothes", 0.00);
        put("Well-being", 0.00);
        put("Medicines", 0.00);
        put("Transport", 0.00);
        put("Other", 0.00);
    }};

    private Double currentCourseMultiplier = 1.00;
    private String currentCurrency = "EUR";
    private Budget activeBudget;

    private ArrayList<Budget> budgets = new ArrayList<Budget>();
    private Budget budgetObject;
    private Double totalBudget = 0.00;
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
                totalBudget += amount;
                break;
            case ("addWithIncome"):
                income += amount;
                totalBudget += amount;
                break;
            case ("addToIncome"):
                income += amount;
                break;
            case ("subtractFromBudget"):
                totalBudget -= amount;
                break;
            case ("subtractWithExpenses"):
                expense += amount;
                activeBudget.decreaseAmount(amount);
                break;
            case ("subtractExpense"):
                expense -= amount;
                activeBudget.increaseAmount(amount);
                break;
        }
    }

    public void setCategories(String category, Double amount, Boolean addMoney) {
        double newAmount = categories.get(category);
        if (addMoney) {
            newAmount += amount;
        } else {
            newAmount -= amount;
        }
        newAmount += amount;
        categories.put(category, newAmount);
    }

    public void setCurrentCourseMultiplier(String course) {

        //totalBudget = totalBudget / currentCourseMultiplier;
        //income = income / currentCourseMultiplier;
        //expense = expense / currentCourseMultiplier;
        if(budgets.size() > 0){
            for (Budget budget: budgets) {
                budget.setAmount(budget.getAmount()/currentCourseMultiplier);
            }

        }
        Double multiplierBefore = currentCourseMultiplier;

        currentCourseMultiplier = currencies.get(course);
        currentCurrency = course;

        if(budgets.size() > 0){
            for (Budget budget: budgets) {
                budget.setCurrency(currentCurrency);
                budget.setAmount(budget.getAmount()*currentCourseMultiplier);
                if(budget.getExpenses().size() > 0){

                    for (Expense expense:budget.getExpenses()) {
                        Double newPrice = expense.getPrice()/multiplierBefore;
                        newPrice = newPrice*currentCourseMultiplier;
                        expense.setPrice(newPrice);
                        expense.setUsedCurrency(activeBudget.getCurrency());
                    }
                }
            }



        }

        //totalBudget = totalBudget * currentCourseMultiplier;
        //income = income * currentCourseMultiplier;
        //expense = expense * currentCourseMultiplier;
    }

    public void createNewBudget(Budget newBudget) {

        budgets.add(newBudget);
    }

    public ArrayList<String> getBudgetNames() {
        ArrayList<String> names = new ArrayList<>();
        names.add("New");

        for (Budget budget : budgets) {
            names.add(budget.getName());
        }

        return names;
    }
    public ArrayList<Budget> getBudgets(){
        return  budgets;
    }
    public Double getSpecificBudget(String name) {
        HashMap<String, Double> budgetsMap = new HashMap<>();

        for (Budget budget : budgets) {
            budgetsMap.put(budget.getName(), budget.getAmount());
        }
        return budgetsMap.get(name);
    }

    public Double getTotalExpenses(){
        Double totalExpenses = 0.00;
        for(Integer i=0; i<activeBudget.getExpenses().size(); i++){
            totalExpenses += activeBudget.getExpenses().get(i).getPrice();
        }
        return totalExpenses;
    }

    public String getCurrentCurrency() {
        return currentCurrency;
    }
    public Double getCurrentCourseMultiplier(){
        return currentCourseMultiplier;
    }
    public ArrayList<String> getCurrencyCodes() {
        return new ArrayList<>(currencies.keySet());
    }

    public Double getBudget() {
        return totalBudget;
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
    public void setActiveBudget(Budget budget){
        activeBudget = budget;
    }
    public Budget getActiveBudget(){
        return activeBudget;
    }
}