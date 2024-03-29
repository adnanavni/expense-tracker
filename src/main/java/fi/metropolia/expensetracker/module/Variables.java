package fi.metropolia.expensetracker.module;


import fi.metropolia.expensetracker.module.Dao.BudgetExpenseDao;
import fi.metropolia.expensetracker.module.Dao.IncomeDao;
import fi.metropolia.expensetracker.module.Dao.RegisterLoginDao;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import static java.util.Map.entry;

/**
 * Singleton class to keep track of all the variables (expect salary)
 * All the data goes through this class
 */
public class Variables {
    private static Variables INSTANCE = null;
    private BudgetExpenseDao budgetExpenseDao = new BudgetExpenseDao();

    private final Map<String, Double> currencies;

    {
        try {
            currencies = Map.ofEntries(entry("EUR", 1.00), entry("USD", getCurrencyExchangeRateViaGETRequest("USD")), entry("SEK", getCurrencyExchangeRateViaGETRequest("SEK")), entry("JPY", getCurrencyExchangeRateViaGETRequest("JPY")), entry("ISK", getCurrencyExchangeRateViaGETRequest("ISK")), entry("CAD", getCurrencyExchangeRateViaGETRequest("CAD")), entry("RUB", getCurrencyExchangeRateViaGETRequest("RUB")), entry("CHF", getCurrencyExchangeRateViaGETRequest("CHF")), entry("NOK", getCurrencyExchangeRateViaGETRequest("NOK")), entry("DKK", getCurrencyExchangeRateViaGETRequest("DKK")), entry("GBP", getCurrencyExchangeRateViaGETRequest("GBP")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    LocalizationManager lan = LocalizationManager.getInstance();
    /**
     * for the tips in the main view
     */
    private ArrayList<String> quotes = new ArrayList<>() {{
        add("Saving money is a way to achieve financial freedom. Start small and make it a habit.");
        add("The key to saving is to spend less than you earn. Keep track of your expenses and create a budget.");
        add("Use cash instead of credit cards to avoid overspending. It's harder to part with actual money.");
        add("Avoid eating out and prepare your own meals. It's healthier and saves you money in the long run.");
        add("Take advantage of sales and discounts. Buy what you need, not what you want.");
        add("Always compare prices before making a purchase. Don't settle for the first offer you see.");
        add("Avoid impulse buying. Take some time to think about whether you really need that item.");
        add("Instead of buying new things, consider buying used or borrowing from friends.");
        add("Make use of free entertainment options like libraries, parks, and community events.");
        add("Remember that every little bit counts. Even small savings can add up over time.");
    }};
    /**
     * expense categories for the expense view+
     */
    private ArrayList<String> categories = new ArrayList<>() {{
        add("Groceries");
        add("Restaurants");
        add("Hobbies");
        add("Clothes");
        add("Well-being");
        add("Medicine");
        add("Transport");
        add("Other");
    }};
    private Integer loggedUserId;
    /**
     * constant expense categories for the budget and expense view, used as default
     */
    private ArrayList<String> constExpenses = new ArrayList<>() {{
        add("Rent");
        add("Water bill");
        add("Insurance");
        add("Car payment");
        add("Cell phone");
        add("Internet");
    }};
    /**
     * Constant expenses after localization and includes the amount also.
     */
    private ArrayList<ConstantExpense> constantExpenses = new ArrayList<>();

    private Double currentCourseMultiplier = 1.00;
    private String currentCurrency = "EUR";
    private Budget activeBudget;
    /**
     * list of the users budgets
     */
    private ArrayList<Budget> budgets = new ArrayList<>();
    private Double totalBudget = 0.00;

    private Variables() {
    }

    public static Variables getInstance() {
        if (INSTANCE == null) INSTANCE = new Variables();
        return INSTANCE;
    }

    public void setLoggedCurrency(String course) {
        currentCourseMultiplier = currencies.get(course);
        currentCurrency = course;
    }

    /**
     * Used in calculating the course after changing currency
     *
     * @param course of the coming currency
     */
    public void setCurrentCourseMultiplier(String course) {
        RegisterLoginDao loginSignupDao = new RegisterLoginDao();
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
                budgetExpenseDao.changeBudgetMoney(budget.getId(), budget.getAmount());
                if (budget.getExpenses().size() > 0) {

                    for (Expense expense : budget.getExpenses()) {
                        double newPrice = expense.getPrice() / multiplierBefore;
                        newPrice = newPrice * currentCourseMultiplier;
                        expense.setPrice(newPrice);
                        budgetExpenseDao.changeExpenseMoney(expense.getId(), expense.getPrice());
                    }
                }
            }
        }
        if (constantExpenses.size() > 0) {
            for (ConstantExpense constantExpense : constantExpenses) {
                constantExpense.setAmount(constantExpense.getAmount() * currentCourseMultiplier);
                budgetExpenseDao.changeConstantExpenseValue(constantExpense.getId(), constantExpense.getAmount());
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
        activeBudget = null;
    }

    /**
     * returns users budgets' names
     *
     * @return
     */
    public ArrayList<String> getBudgetNames() {
        ArrayList<String> names = new ArrayList<>();
        names.add("New");

        for (Budget budget : budgets) {
            names.add(budget.getName());
        }

        return names;
    }

    public ArrayList<String> getConstExpenses() {
        return constExpenses;
    }

    public ArrayList<Budget> getBudgets() {
        return budgets;
    }

    public void resetBudgets() {
        budgets = new ArrayList<>();
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

    public ArrayList<String> getCategories() {
        return categories;
    }

    /**
     * localizes the categories for the correct language, called upon initialization of the view
     */
    public void refreshCategories() {
        categories.clear();
        categories.add(lan.getString("groceries"));
        categories.add(lan.getString("restaurants"));
        categories.add(lan.getString("hobbies"));
        categories.add(lan.getString("clothes"));
        categories.add(lan.getString("well-being"));
        categories.add(lan.getString("medicine"));
        categories.add(lan.getString("transport"));
        categories.add(lan.getString("other"));
    }

    public Budget getActiveBudget() {
        return activeBudget;
    }

    public void setActiveBudget(String name) {
        for (Budget budget : budgets) {
            if (budget.getName().equals(name)) {
                activeBudget = budget;
            }
        }
    }

    public Integer getLoggedUserId() {

        return loggedUserId;
    }

    public void setLoggedUserId(Integer id) {
        loggedUserId = id;
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

    /**
     * resets all the variables for example when user logs out
     */
    public void resetAll() {
        loggedUserId = null;
        currentCourseMultiplier = 1.00;
        currentCurrency = "EUR";
        activeBudget = null;
        budgets.clear();
        totalBudget = 0.00;
        constantExpenses = new ArrayList<>();
    }

    /**
     * resets all variables and sets them to default for example upon registration
     */
    public void resetAndSetDefaults() {
        currentCourseMultiplier = 1.00;
        currentCurrency = "EUR";
        activeBudget = null;
        budgets.clear();
        totalBudget = 0.00;
        constantExpenses = new ArrayList<>();
        ThemeManager.getInstance().setCurrentColor("#85bb65");

        ConstantExpense[] constantExpenses = budgetExpenseDao.getConstantExpenses(Variables.getInstance().getLoggedUserId());

        ArrayList<String> defaultConstExpenseNames = Variables.getInstance().getConstExpenses();
        for (String defaultConstExpenseName : defaultConstExpenseNames) {
            budgetExpenseDao.saveConstantExpense(Variables.getInstance().getLoggedUserId(), defaultConstExpenseName, 0.00);
        }
        ConstantExpense[] defaultConstExpenses = budgetExpenseDao.getConstantExpenses(Variables.getInstance().getLoggedUserId());
        for (ConstantExpense constantExpense : defaultConstExpenses) {
            Variables.getInstance().addConstantExpense(constantExpense);
        }
    }

    /**
     * gets currencies from the API
     *
     * @param to
     * @return object's double of the currency's course
     * @throws IOException
     */
    public double getCurrencyExchangeRateViaGETRequest(String to) throws IOException {
        String GET_URL = "https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/eur/" + to.toLowerCase() + ".json";
        URL url = new URL(GET_URL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        int responseCode = httpURLConnection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject obj = new JSONObject(response.toString());
            return obj.getDouble(to.toLowerCase());

        }

        return 0;
    }

    public Double getCurrentCourseMultiplier() {
        return currentCourseMultiplier;
    }

    public String getQuote(int index) {
        return quotes.get(index);
    }

    /**
     * localizes the hints shown on main view, refreshed upon initialization.
     */
    public void refreshTips() {
        for (int i = 0; i < quotes.size(); i++) {
            String n = Integer.toString(i);
            quotes.set(i, lan.getString(n));
        }
    }
}
