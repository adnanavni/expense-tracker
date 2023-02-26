package fi.metropolia.expensetracker.module;


import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public class Variables {
    private static Variables INSTANCE = null;

    private final Map<String, Double> currencies = Map.ofEntries(entry("EUR", 1.00), entry("USD", 1.08), entry("SEK", 11.1091), entry("JPY", 140.64), entry("ISK", 152.58), entry("CAD", 1.43), entry("RUB", 78.21), entry("CHF", 0.99), entry("NOK", 10.84), entry("DKK", 7.45), entry("GBP", 0.89));

    private final Map<String, Double> categories = new HashMap<>() {{
        put("Groceries", 0.00);
        put("Restaurants", 0.00);
        put("Hobbies", 0.00);
        put("Clothes", 0.00);
        put("Well-being", 0.00);
        put("Medicines", 0.00);
        put("Transport", 0.00);
        put("Other", 0.00);
    }};

    private Map<String, Double> constExpenses = new HashMap<>() {{
        put("Rent", 0.00);
        put("Water bill", 0.00);
        put("Insurance", 0.00);
        put("Car payment", 0.00);
        put("Cell phone", 0.00);
        put("Internet", 0.00);
    }};

    private Double currentCourseMultiplier = 1.00;
    private String currentCurrency = "EUR";
    private Budget activeBudget;

    private ArrayList<Budget> budgets = new ArrayList<>();
    private Budget budgetObject;
    private Double totalBudget = 0.00;
    private Double expense = 0.00;
    private Double income = 0.00;

    private Variables() {
    }

    public static Variables getInstance() {
        if (INSTANCE == null) INSTANCE = new Variables();
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
                activeBudget.decreaseAmount(amount);
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

        if (budgets.size() > 0) {
            for (Budget budget : budgets) {
                budget.setAmount(budget.getAmount() / currentCourseMultiplier);
            }

        }
        Double multiplierBefore = currentCourseMultiplier;

        currentCourseMultiplier = currencies.get(course);
        currentCurrency = course;

        if (budgets.size() > 0) {
            for (Budget budget : budgets) {
                budget.setCurrency(currentCurrency);
                budget.setAmount(budget.getAmount() * currentCourseMultiplier);
                if (budget.getExpenses().size() > 0) {

                    for (Expense expense : budget.getExpenses()) {
                        double newPrice = expense.getPrice() / multiplierBefore;
                        newPrice = newPrice * currentCourseMultiplier;
                        expense.setPrice(newPrice);
                        expense.setUsedCurrency(Currency.getInstance(currentCurrency).getSymbol());
                    }
                }
            }
        }
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

    public ArrayList<String> getConstExpenses() {
        return new ArrayList<>(constExpenses.keySet());
    }

    public void setConstExpenses(String key, Double amount) {
        constExpenses.put(key, amount);
    }

    public Double getConstExpense(String key) {
        return constExpenses.get(key);
    }

    public ArrayList<Budget> getBudgets() {
        return budgets;
    }

    public Double getSpecificBudget(String name) {
        HashMap<String, Double> budgetsMap = new HashMap<>();

        for (Budget budget : budgets) {
            budgetsMap.put(budget.getName(), budget.getAmount());
        }
        return budgetsMap.get(name);
    }

    public Double getTotalExpenses() {
        Double totalExpenses = 0.00;
        for (int i = 0; i < activeBudget.getExpenses().size(); i++) {
            totalExpenses += activeBudget.getExpenses().get(i).getPrice();
        }
        return totalExpenses;
    }

    public String getCurrentCurrency() {
        return currentCurrency;
    }

    public ArrayList<String> getCurrencyCodes() {
        return new ArrayList<>(currencies.keySet());
    }

    public Double getBudget() {
        Double allBudgets = 0.00;
        for (int i = 0; i < getBudgets().size(); i++) {
            allBudgets += getBudgets().get(i).getAmount();
        }
        return allBudgets;
    }

    public ArrayList<String> getTopics() {
        return new ArrayList<>(categories.keySet());
    }

    public void setActiveBudget(Budget budget) {
        activeBudget = budget;
    }

    public Budget getActiveBudget() {
        return activeBudget;
    }

    public void convertConstExpense() {
        for (Map.Entry<String, Double> entry : constExpenses.entrySet()) {
            String expense = entry.getKey();
            Double cost = entry.getValue() / currentCourseMultiplier;
        }

        for (Map.Entry<String, Double> entry : constExpenses.entrySet()) {
            String expense = entry.getKey();
            Double cost = entry.getValue() * currentCourseMultiplier;
            setConstExpenses(expense, cost);
        }
    }
}