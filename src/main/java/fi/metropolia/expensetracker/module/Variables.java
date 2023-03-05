package fi.metropolia.expensetracker.module;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public class Variables {
    private static Variables INSTANCE = null;
    private Integer loggedUserId;
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

    private ArrayList<ConstantExpense> constantExpenses = new ArrayList<>();

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

    public void setLoggedCurrency(String course) {
        currentCourseMultiplier = currencies.get(course);
        currentCurrency = course;
    }

    public void setCurrentCourseMultiplier(String course) {
        Dao loginSignupDao = new Dao();
        IncomeDao incomeDao = new IncomeDao();
        if (budgets.size() > 0) {
            for (Budget budget : budgets) {
                budget.setAmount(budget.getAmount() / currentCourseMultiplier);
            }

        }
        if (constantExpenses.size() > 0) {
            for (ConstantExpense constantExpense : constantExpenses) {
                constantExpense.setAmount(constantExpense.getAmount() / currentCourseMultiplier);
            }
        }


        ArrayList<Salary> incomes = incomeDao.getAllSalaries(loggedUserId);

        if (incomes.size() > 0) {
            for (Salary salary : incomes) {
                salary.setSalary(salary.getSalary() / currentCourseMultiplier);
            }
        }


        Double multiplierBefore = currentCourseMultiplier;

        currentCourseMultiplier = currencies.get(course);
        currentCurrency = course;

        if (incomes.size() > 0) {

            for (Salary salary : incomes) {

                salary.setSalary(salary.getSalary() * currentCourseMultiplier);
                salary.setUsedCurrency(currentCurrency);

                incomeDao.changeIncomeValues(salary.getId(), salary.getSalary(), salary.getSalaryMinusTaxes(salary.getType()), currentCurrency);
            }
        }

        if (budgets.size() > 0) {
            for (Budget budget : budgets) {
                budget.setAmount(budget.getAmount() * currentCourseMultiplier);
                loginSignupDao.changeBudgetMoney(budget.getId(), budget.getAmount());
                if (budget.getExpenses().size() > 0) {

                    for (Expense expense : budget.getExpenses()) {
                        double newPrice = expense.getPrice() / multiplierBefore;
                        newPrice = newPrice * currentCourseMultiplier;
                        expense.setPrice(newPrice);
                        loginSignupDao.changeExpenseMoney(expense.getId(), expense.getPrice());
                    }
                }
            }
        }
        if (constantExpenses.size() > 0) {
            for (ConstantExpense constantExpense : constantExpenses) {
                constantExpense.setAmount(constantExpense.getAmount() * currentCourseMultiplier);
                loginSignupDao.changeConstantExpenseValue(constantExpense.getId(), constantExpense.getAmount());
            }
        }

    }

    public void createNewBudget(Budget newBudget) {
        budgets.add(newBudget);
    }


    public void modifyBudget(String newName, double amount) {
        activeBudget.setAmount(amount);
        activeBudget.setName(newName);
    }

    public void deleteBudget() {
        int index = budgets.indexOf(activeBudget);
        if (index != -1) {
            budgets.remove(index);
        }
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

    public void resetBudgets() {
        budgets = new ArrayList<>();
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
        totalBudget = 0.00;
        for (Budget budget : getBudgets()) {
            Double budgetExpenses = 0.00;
            for (Expense expense : budget.getExpenses()) {
                budgetExpenses += expense.getPrice();
            }
            totalBudget += (budget.getAmount() - budgetExpenses);
        }
        return totalBudget;
    }

    public ArrayList<String> getTopics() {
        return new ArrayList<>(categories.keySet());
    }

    public void setActiveBudget(String name) {
        for (Budget budget : budgets) {
            if (budget.getName().equals(name)) {
                activeBudget = budget;
            }
        }
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

    public void setLoggedUserId(Integer id) {
        loggedUserId = id;
    }

    public Integer getLoggedUserId() {
        return loggedUserId;
    }

    public void addConstantExpense(ConstantExpense constantExpense) {
        constantExpenses.add(constantExpense);
    }

    public Boolean constantExpenseNameExists(String name) {
        for (ConstantExpense constantExpense : constantExpenses) {
            if (constantExpense.getType().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void removeConstantExpense(ConstantExpense constantExpense) {
        constantExpenses.remove(constantExpense);
    }

    public ArrayList<ConstantExpense> getConstantExpenseArray() {
        return constantExpenses;
    }

    public void resetAll() {
        loggedUserId = null;
        currentCourseMultiplier = 1.00;
        currentCurrency = "EUR";
        activeBudget = null;
        budgets.clear();
        budgetObject = null;
        totalBudget = 0.00;
        expense = 0.00;
        income = 0.00;
        constantExpenses = new ArrayList<>();
    }

    public void resetAndSetDefaults() {
        currentCourseMultiplier = 1.00;
        currentCurrency = "EUR";
        activeBudget = null;
        budgets.clear();
        budgetObject = null;
        totalBudget = 0.00;
        expense = 0.00;
        income = 0.00;
        constantExpenses = new ArrayList<>();
        ThemeManager.getInstance().setCurrentColor("#85bb65");

        Dao loginSignupDao = new Dao();

        ConstantExpense[] constantExpenses = loginSignupDao.getConstantExpenses(Variables.getInstance().getLoggedUserId());

        if(constantExpenses.length == 0){
            ArrayList<String> defaultConstExpenseNames = Variables.getInstance().getConstExpenses();
            for (String defaultConstExpenseName : defaultConstExpenseNames) {
                loginSignupDao.saveConstantExpense(Variables.getInstance().getLoggedUserId(), defaultConstExpenseName, 0.00);
            }
            ConstantExpense[] defaultConstExpenses = loginSignupDao.getConstantExpenses(Variables.getInstance().getLoggedUserId());
            for (ConstantExpense constantExpense : defaultConstExpenses) {
                Variables.getInstance().addConstantExpense(constantExpense);
            }
        }
    }
}
