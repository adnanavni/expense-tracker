package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.*;
import fi.metropolia.expensetracker.module.Dao.BudgetExpenseDao;
import fi.metropolia.expensetracker.module.Dao.RegisterLoginDao;
import fi.metropolia.expensetracker.module.Dao.SettingsDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class BudgetController {

    @FXML
    private AnchorPane content;
    @FXML
    private Label activeBudget;
    @FXML
    private TextField addBudget;
    @FXML
    private TextField budgetName;
    @FXML
    private ComboBox selectTopic;
    @FXML
    private AnchorPane budgetPane;
    @FXML
    private ComboBox expenseCombo;
    @FXML
    private AnchorPane newBudget;
    @FXML
    private AnchorPane editBudget;
    @FXML
    private TextField modifyName;
    @FXML
    private TextField modifyAmount;
    @FXML
    private Label total;
    @FXML
    private Label active;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button modifyBtn;
    @FXML
    private Button addBtn;
    @FXML
    private Button back;
    @FXML
    private Button ConstExpenseBtn;
    @FXML
    private AnchorPane modifyBudget;
    @FXML
    private Button modify;
    @FXML
    private Button delete;
    @FXML
    private Button shared;

    @FXML
    private BarChart<String, Double> barStats;
    @FXML
    private PieChart pieStats;
    @FXML
    private ComboBox selectedTimeFrame;
    @FXML
    private Label show;


    private Boolean barChartShown = true;
    private Variables variables;
    private Currency currency;
    private LocalizationManager language = LocalizationManager.getInstance();
    private BudgetExpenseDao budgetExpenseDao = new BudgetExpenseDao();
    private SettingsDao settingsDao = new SettingsDao();
    private ObservableList<PieChart.Data> currentPieValues;

    public void initialize() {
        ThemeManager themeManager = ThemeManager.getInstance();
        content.setStyle(themeManager.getStyle());

        back.setText(language.getString("back"));
        total.setText(language.getString("total"));
        active.setText(language.getString("active"));
        selectTopic.setPromptText(language.getString("budget"));
        activeBudget.setText(language.getString("noActive"));

        budgetName.setPromptText(language.getString("name"));
        addBudget.setPromptText(language.getString("amount"));
        addBtn.setText(language.getString("add"));

        modifyName.setPromptText(language.getString("name"));
        modifyAmount.setPromptText(language.getString("amount"));
        modifyBtn.setText(language.getString("modify"));
        deleteBtn.setText(language.getString("delete"));

        expenseCombo.setPromptText(language.getString("constantExpense"));
        ConstExpenseBtn.setText(language.getString("remove"));

        modify.setText(language.getString("modify"));
        delete.setText(language.getString("delete"));

        shared.setText(language.getString("shared"));
        show.setText(language.getString("show"));


    }

    public void setVariables(Variables variables) {

        this.variables = variables;
        currency = Currency.getInstance(variables.getCurrentCurrency());

        total.setText(language.getString("total"));
        selectTopic.getItems().addAll(variables.getBudgetNames());

        if (variables.getActiveBudget() != null) {
            selectTopic.setValue(variables.getActiveBudget().getName());
            modifyBudget.setVisible(true);
            budgetPane.setVisible(true);
        }

        for (ConstantExpense constantExpense : variables.getConstantExpenseArray()) {
            expenseCombo.getItems().add(constantExpense);
        }

        if (variables.getActiveBudget() != null) {
            String budgetText = String.format("%.2f", variables.getBudget());
            activeBudget.setText(variables.getActiveBudget().getName());
            total.setText(language.getString("total") + " " + budgetText + " " + currency.getSymbol());
        }
        selectedTimeFrame.getItems().add("All time");
        selectedTimeFrame.getItems().add("This year");
        selectedTimeFrame.getItems().add("This month");
        selectedTimeFrame.getItems().add("This week");
        selectedTimeFrame.getItems().add("Today");
        barStats.setAnimated(false);
        pieStats.setAnimated(false);
        budgetName.setText(variables.getActiveBudget().getName());
        ArrayList<String> chartNames = variables.getTopics();

        HashMap<String, Double> allValues = calculateValues("All time");


        barStats.setTitle("Expenses");


        XYChart.Series series = new XYChart.Series();
        series.setName("Money spent");
        series.getData().add(new XYChart.Data("Groceries", allValues.get("Groceries")));
        series.getData().add(new XYChart.Data("Restaurants", allValues.get("Restaurants")));
        series.getData().add(new XYChart.Data("Hobbies", allValues.get("Hobbies")));
        series.getData().add(new XYChart.Data("Clothes", allValues.get("Clothes")));
        series.getData().add(new XYChart.Data("Well-being", allValues.get("Well-being")));
        series.getData().add(new XYChart.Data("Medicines", allValues.get("Medicines")));
        series.getData().add(new XYChart.Data("Transport", allValues.get("Transport")));
        series.getData().add(new XYChart.Data("Other", allValues.get("Other")));
        series.getData().add(new XYChart.Data("Constant expenses", allValues.get("Constant expenses")));
        barStats.getData().add(series);

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(new PieChart.Data("Groceries", allValues.get("Groceries")), new PieChart.Data("Restaurants", allValues.get("Restaurants")), new PieChart.Data("Hobbies", allValues.get("Hobbies")), new PieChart.Data("Clothes", allValues.get("Clothes")), new PieChart.Data("Well-being", allValues.get("Well-being")), new PieChart.Data("Medicines", allValues.get("Medicines")), new PieChart.Data("Transport", allValues.get("Transport")), new PieChart.Data("Other", allValues.get("Other")), new PieChart.Data("Constant expenses", allValues.get("Constant expenses")));

        pieStats.getData().addAll(pieChartData);
        currentPieValues = pieChartData;
        pieStats.setTitle("Expenses");

        selectedTimeFrame.setOnAction((event) -> {
            String selection = selectedTimeFrame.getValue().toString();
            ObservableList<PieChart.Data> newPieChartData;
            HashMap<String, Double> values;
            Integer matches = 0;
            switch (selection) {
                case ("All time"):
                    values = calculateValues("All time");
                    newPieChartData = FXCollections.observableArrayList(new PieChart.Data("Groceries", values.get("Groceries")), new PieChart.Data("Restaurants", values.get("Restaurants")), new PieChart.Data("Hobbies", values.get("Hobbies")), new PieChart.Data("Clothes", values.get("Clothes")), new PieChart.Data("Well-being", values.get("Well-being")), new PieChart.Data("Medicines", values.get("Medicines")), new PieChart.Data("Transport", values.get("Transport")), new PieChart.Data("Other", values.get("Other")), new PieChart.Data("Constant expenses", values.get("Constant expenses")));
                    matches = 0;
                    for (PieChart.Data data : newPieChartData) {
                        for (PieChart.Data data2 : currentPieValues) {
                            if (data.getPieValue() == data2.getPieValue() && data.getName().equals(data2.getName())) {
                                matches++;
                            }
                        }
                    }
                    if (matches < 9) {
                        pieStats.getData().clear();
                        pieStats.getData().addAll(newPieChartData);
                        currentPieValues = newPieChartData;
                    }

                    barStats.getData().clear();
                    series.getData().clear();
                    series.getData().add(new XYChart.Data("Groceries", values.get("Groceries")));
                    series.getData().add(new XYChart.Data("Restaurants", values.get("Restaurants")));
                    series.getData().add(new XYChart.Data("Hobbies", values.get("Hobbies")));
                    series.getData().add(new XYChart.Data("Clothes", values.get("Clothes")));
                    series.getData().add(new XYChart.Data("Well-being", values.get("Well-being")));
                    series.getData().add(new XYChart.Data("Medicines", values.get("Medicines")));
                    series.getData().add(new XYChart.Data("Transport", values.get("Transport")));
                    series.getData().add(new XYChart.Data("Other", values.get("Other")));
                    series.getData().add(new XYChart.Data("Constant expenses", values.get("Constant expenses")));
                    barStats.getData().add(series);
                    break;
                case ("This year"):
                    values = calculateValues("This year");
                    newPieChartData = FXCollections.observableArrayList(new PieChart.Data("Groceries", values.get("Groceries")), new PieChart.Data("Restaurants", values.get("Restaurants")), new PieChart.Data("Hobbies", values.get("Hobbies")), new PieChart.Data("Clothes", values.get("Clothes")), new PieChart.Data("Well-being", values.get("Well-being")), new PieChart.Data("Medicines", values.get("Medicines")), new PieChart.Data("Transport", values.get("Transport")), new PieChart.Data("Other", values.get("Other")), new PieChart.Data("Constant expenses", values.get("Constant expenses")));

                    matches = 0;
                    for (PieChart.Data data : newPieChartData) {
                        for (PieChart.Data data2 : currentPieValues) {
                            if (data.getPieValue() == data2.getPieValue() && data.getName().equals(data2.getName())) {
                                matches++;
                            }
                        }
                    }
                    if (matches < 9) {
                        pieStats.getData().clear();
                        pieStats.getData().addAll(newPieChartData);
                        currentPieValues = newPieChartData;
                    }
                    barStats.getData().clear();
                    series.getData().clear();
                    series.getData().add(new XYChart.Data("Groceries", values.get("Groceries")));
                    series.getData().add(new XYChart.Data("Restaurants", values.get("Restaurants")));
                    series.getData().add(new XYChart.Data("Hobbies", values.get("Hobbies")));
                    series.getData().add(new XYChart.Data("Clothes", values.get("Clothes")));
                    series.getData().add(new XYChart.Data("Well-being", values.get("Well-being")));
                    series.getData().add(new XYChart.Data("Medicines", values.get("Medicines")));
                    series.getData().add(new XYChart.Data("Transport", values.get("Transport")));
                    series.getData().add(new XYChart.Data("Other", values.get("Other")));
                    series.getData().add(new XYChart.Data("Constant expenses", values.get("Constant expenses")));
                    barStats.getData().add(series);
                    break;
                case ("This month"):
                    values = calculateValues("This month");
                    newPieChartData = FXCollections.observableArrayList(new PieChart.Data("Groceries", values.get("Groceries")), new PieChart.Data("Restaurants", values.get("Restaurants")), new PieChart.Data("Hobbies", values.get("Hobbies")), new PieChart.Data("Clothes", values.get("Clothes")), new PieChart.Data("Well-being", values.get("Well-being")), new PieChart.Data("Medicines", values.get("Medicines")), new PieChart.Data("Transport", values.get("Transport")), new PieChart.Data("Other", values.get("Other")), new PieChart.Data("Constant expenses", values.get("Constant expenses")));
                    matches = 0;
                    for (PieChart.Data data : newPieChartData) {
                        for (PieChart.Data data2 : currentPieValues) {
                            if (data.getPieValue() == data2.getPieValue() && data.getName().equals(data2.getName())) {
                                matches++;
                            }
                        }
                    }
                    if (matches < 9) {
                        pieStats.getData().clear();
                        pieStats.getData().addAll(newPieChartData);
                        currentPieValues = newPieChartData;
                    }
                    barStats.getData().clear();
                    series.getData().clear();
                    series.getData().add(new XYChart.Data("Groceries", values.get("Groceries")));
                    series.getData().add(new XYChart.Data("Restaurants", values.get("Restaurants")));
                    series.getData().add(new XYChart.Data("Hobbies", values.get("Hobbies")));
                    series.getData().add(new XYChart.Data("Clothes", values.get("Clothes")));
                    series.getData().add(new XYChart.Data("Well-being", values.get("Well-being")));
                    series.getData().add(new XYChart.Data("Medicines", values.get("Medicines")));
                    series.getData().add(new XYChart.Data("Transport", values.get("Transport")));
                    series.getData().add(new XYChart.Data("Other", values.get("Other")));
                    series.getData().add(new XYChart.Data("Constant expenses", values.get("Constant expenses")));
                    barStats.getData().add(series);
                    break;
                case ("This week"):
                    values = calculateValues("This week");
                    newPieChartData = FXCollections.observableArrayList(new PieChart.Data("Groceries", values.get("Groceries")), new PieChart.Data("Restaurants", values.get("Restaurants")), new PieChart.Data("Hobbies", values.get("Hobbies")), new PieChart.Data("Clothes", values.get("Clothes")), new PieChart.Data("Well-being", values.get("Well-being")), new PieChart.Data("Medicines", values.get("Medicines")), new PieChart.Data("Transport", values.get("Transport")), new PieChart.Data("Other", values.get("Other")), new PieChart.Data("Constant expenses", values.get("Constant expenses")));
                    matches = 0;
                    for (PieChart.Data data : newPieChartData) {
                        for (PieChart.Data data2 : currentPieValues) {
                            if (data.getPieValue() == data2.getPieValue() && data.getName().equals(data2.getName())) {
                                matches++;
                            }
                        }
                    }
                    if (matches < 9) {
                        pieStats.getData().clear();
                        pieStats.getData().addAll(newPieChartData);
                        currentPieValues = newPieChartData;
                    }
                    barStats.getData().clear();
                    series.getData().clear();
                    series.getData().add(new XYChart.Data("Groceries", values.get("Groceries")));
                    series.getData().add(new XYChart.Data("Restaurants", values.get("Restaurants")));
                    series.getData().add(new XYChart.Data("Hobbies", values.get("Hobbies")));
                    series.getData().add(new XYChart.Data("Clothes", values.get("Clothes")));
                    series.getData().add(new XYChart.Data("Well-being", values.get("Well-being")));
                    series.getData().add(new XYChart.Data("Medicines", values.get("Medicines")));
                    series.getData().add(new XYChart.Data("Transport", values.get("Transport")));
                    series.getData().add(new XYChart.Data("Other", values.get("Other")));
                    series.getData().add(new XYChart.Data("Constant expenses", values.get("Constant expenses")));
                    barStats.getData().add(series);
                    break;
                case ("Today"):
                    values = calculateValues("Today");
                    newPieChartData = FXCollections.observableArrayList(new PieChart.Data("Groceries", values.get("Groceries")), new PieChart.Data("Restaurants", values.get("Restaurants")), new PieChart.Data("Hobbies", values.get("Hobbies")), new PieChart.Data("Clothes", values.get("Clothes")), new PieChart.Data("Well-being", values.get("Well-being")), new PieChart.Data("Medicines", values.get("Medicines")), new PieChart.Data("Transport", values.get("Transport")), new PieChart.Data("Other", values.get("Other")), new PieChart.Data("Constant expenses", values.get("Constant expenses")));
                    matches = 0;
                    for (PieChart.Data data : newPieChartData) {
                        for (PieChart.Data data2 : currentPieValues) {
                            if (data.getPieValue() == data2.getPieValue() && data.getName().equals(data2.getName())) {
                                matches++;
                            }
                        }
                    }
                    if (matches < 9) {
                        pieStats.getData().clear();
                        pieStats.getData().addAll(newPieChartData);
                        currentPieValues = newPieChartData;
                    }
                    barStats.getData().clear();
                    series.getData().clear();
                    series.getData().add(new XYChart.Data("Groceries", values.get("Groceries")));
                    series.getData().add(new XYChart.Data("Restaurants", values.get("Restaurants")));
                    series.getData().add(new XYChart.Data("Hobbies", values.get("Hobbies")));
                    series.getData().add(new XYChart.Data("Clothes", values.get("Clothes")));
                    series.getData().add(new XYChart.Data("Well-being", values.get("Well-being")));
                    series.getData().add(new XYChart.Data("Medicines", values.get("Medicines")));
                    series.getData().add(new XYChart.Data("Transport", values.get("Transport")));
                    series.getData().add(new XYChart.Data("Other", values.get("Other")));
                    series.getData().add(new XYChart.Data("Constant expenses", values.get("Constant expenses")));
                    barStats.getData().add(series);
                    break;
            }
        });
    }

    @FXML
    protected void backToMain(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(MainApplication.class.getResource("main-view.fxml"));
        content.getChildren().setAll(pane);
    }

    @FXML
    protected void addToBudget() {

        String text = budgetName.getText();
        String number = addBudget.getText();

        if (text.matches("[a-zA-Z]+") && number.matches("^[0-9]+$")) {
            if (selectTopic.getSelectionModel().getSelectedItem() != null && addBudget.getText() != "") {
                if (selectTopic.getValue() == "New") {
                    boolean willAdd = true;
                    for (Budget budget : variables.getBudgets()) {
                        if (Objects.equals(budget.getName(), budgetName.getText())) {
                            willAdd = false;
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Name Exists");
                            alert.setHeaderText("Budget with given name already exists!");
                            alert.setContentText("Choose another name.");
                            alert.showAndWait();
                        }
                    }
                    if (willAdd) {
                        RegisterLoginDao loginSignupDao = new RegisterLoginDao();
                        budgetExpenseDao.saveBudget(variables.getLoggedUserId(), budgetName.getText(), Double.parseDouble(addBudget.getText()));
                        variables.resetBudgets();
                        Budget[] budgets = budgetExpenseDao.getBudgets(variables.getLoggedUserId());
                        for (Budget budget : budgets) {
                            variables.createNewBudget(budget);
                        }
                        variables.setActiveBudget(budgetName.getText());
                        activeBudget.setText(variables.getActiveBudget().getName());
                        String budgetText = String.format("%.2f", variables.getBudget());
                        total.setText(language.getString("total") + " " + budgetText + " " + currency.getSymbol());

                    }
                }
            }
            selectTopic.getItems().setAll(variables.getBudgetNames());

            selectTopic.setValue(variables.getActiveBudget().getName());
            addBudget.setText(null);
            budgetName.setText(null);
            newBudget.setVisible(false);
            editBudget.setVisible(false);
            budgetPane.setVisible(true);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Add a budget");
            alert.setHeaderText("You cant add a budget");
            alert.setContentText("Fill the form correctly");
            alert.showAndWait();
        }
    }

    @FXML
    protected void onSelectTopic() {
        if (selectTopic.getValue() == "New") {
            modifyBudget.setVisible(false);
            editBudget.setVisible(false);
            newBudget.setVisible(true);
        } else if (selectTopic.getValue() != null) {
            newBudget.setVisible(false);
            modifyBudget.setVisible(true);
            editBudget.setVisible(false);
            budgetPane.setVisible(true);

            for (Budget budget : variables.getBudgets()) {
                if (budget.getName() == selectTopic.getValue()) {
                    variables.setActiveBudget(budget.getName());
                    update();
                }
            }
        }
    }


    @FXML
    protected void modifyBudget() {
        modifyBudget.setVisible(false);
        editBudget.setVisible(true);
    }

    @FXML
    protected void removeBtn() {
        ConstantExpense selectedConstExpense = (ConstantExpense) expenseCombo.getSelectionModel().getSelectedItem();


        RegisterLoginDao loginSignupDao = new RegisterLoginDao();
        budgetExpenseDao.saveExpense(variables.getActiveBudget().getId(), selectedConstExpense.getType(), selectedConstExpense.getAmount(), new Date());
        variables.getActiveBudget().resetExpenses();
        Expense[] expenses = budgetExpenseDao.getExpenses(variables.getActiveBudget().getId());
        for (Expense expense : expenses) {
            variables.getActiveBudget().addExpenseToBudget(expense);
        }
        String budgetText = String.format("%.2f", variables.getBudget());
        total.setText(language.getString("total") + " " + budgetText + " " + currency.getSymbol());
        Double budgetExpenses = 0.00;
        if (variables.getActiveBudget().getExpenses().size() > 0) {
            for (Expense expense : variables.getActiveBudget().getExpenses()) {
                budgetExpenses += expense.getPrice();
            }
        }
    }

    @FXML
    protected void modifyBtnClick() {
        String text = modifyName.getText();
        String number = modifyAmount.getText();

        if (text.matches("[a-zA-Z]+") && number.matches("^[0-9]+$")) {
            RegisterLoginDao loginSignupDao = new RegisterLoginDao();
            if (modifyName.getText() != null && modifyAmount.getText() != null) {
                budgetExpenseDao.ModifyBudget(variables.getActiveBudget().getName(), Double.parseDouble(modifyAmount.getText()), modifyName.getText());

                variables.modifyBudget(modifyName.getText(), Double.parseDouble(modifyAmount.getText()));
                variables.setActiveBudget(modifyName.getText());

                update();
                selectTopic.getItems().setAll(variables.getBudgetNames());

                editBudget.setVisible(false);
            } else if (modifyName.getText() != null && modifyAmount.getText() == null) {

                budgetExpenseDao.ModifyBudget(variables.getActiveBudget().getName(), variables.getActiveBudget().getAmount(), modifyName.getText());

                variables.modifyBudget(modifyName.getText(), variables.getActiveBudget().getAmount());
                variables.setActiveBudget(modifyName.getText());

                update();
                selectTopic.getItems().setAll(variables.getBudgetNames());

                editBudget.setVisible(false);
            } else if (modifyName.getText() == null && modifyAmount.getText() != null) {
                budgetExpenseDao.ModifyBudget(variables.getActiveBudget().getName(), Double.parseDouble(modifyAmount.getText()), variables.getActiveBudget().getName());

                variables.modifyBudget(variables.getActiveBudget().getName(), Double.parseDouble(modifyAmount.getText()));
                variables.setActiveBudget(variables.getActiveBudget().getName());

                update();
                selectTopic.getItems().setAll(variables.getBudgetNames());

                editBudget.setVisible(false);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Modify your budget");
            alert.setHeaderText("Your budget is not being modified");
            alert.setContentText("Fill the form correctly");
            alert.showAndWait();
        }


        selectTopic.setValue(null);
        modifyAmount.setText(null);
        modifyName.setText(null);
    }

    @FXML
    protected void deleteBtnClick() {

        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDelete.setTitle("Confirm Delete");
        confirmDelete.setHeaderText("This action deletes your current budget. Are you sure you want to delete your current budget?");

        Optional<ButtonType> result = confirmDelete.showAndWait();

        if (result.get() == ButtonType.OK) {
            RegisterLoginDao loginSignupDao = new RegisterLoginDao();

            budgetExpenseDao.deleteBudget(variables.getActiveBudget().getId());
            variables.deleteBudget();

            update();
            activeBudget.setText("None");

            selectTopic.getItems().setAll(variables.getBudgetNames());

            selectTopic.setValue(null);
            modifyAmount.setText(null);
            modifyName.setText(null);
            editBudget.setVisible(false);
            budgetPane.setVisible(false);
        }
    }

    private void update() {
        String budgetText = String.format("%.2f", variables.getBudget());
        total.setText(language.getString("total") + " " + budgetText + " " + currency.getSymbol());
        activeBudget.setText(variables.getActiveBudget().getName());
        Double budgetExpenses = 0.00;
        if (variables.getActiveBudget().getExpenses().size() > 0) {
            for (Expense expense : variables.getActiveBudget().getExpenses()) {
                budgetExpenses += expense.getPrice();
            }
        }
    }

    private HashMap<String, Double> calculateValues(String timeFrame) {
        HashMap<String, Double> values = new HashMap<>();
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        ZoneId defaultZoneId = ZoneId.systemDefault();
        LocalDate currentDate = LocalDate.now();
        cal1.setTime(java.sql.Date.from(currentDate.atStartOfDay(defaultZoneId).toInstant()));


        values.put("Groceries", 0.00);

        values.put("Restaurants", 0.00);

        values.put("Hobbies", 0.00);

        values.put("Clothes", 0.00);

        values.put("Well-being", 0.00);

        values.put("Medicines", 0.00);

        values.put("Transport", 0.00);

        values.put("Other", 0.00);

        values.put("Constant expenses", 0.00);

        Double currentValue;

        if (timeFrame.equals("All time") && variables.getActiveBudget().getExpenses().size() > 0) {
            for (Expense expense : variables.getActiveBudget().getExpenses()) {
                switch (expense.getType()) {
                    case ("Groceries"):
                        currentValue = values.get("Groceries");
                        values.put("Groceries", currentValue + expense.getPrice());
                        break;
                    case ("Restaurants"):
                        currentValue = values.get("Restaurants");
                        values.put("Restaurants", currentValue + expense.getPrice());
                        break;
                    case ("Hobbies"):
                        currentValue = values.get("Hobbies");
                        values.put("Hobbies", currentValue + expense.getPrice());
                        break;
                    case ("Clothes"):
                        currentValue = values.get("Clothes");
                        values.put("Clothes", currentValue + expense.getPrice());
                        break;
                    case ("Well-being"):
                        currentValue = values.get("Well-being");
                        values.put("Well-being", currentValue + expense.getPrice());
                        break;
                    case ("Medicines"):
                        currentValue = values.get("Medicines");
                        values.put("Medicines", currentValue + expense.getPrice());
                        break;
                    case ("Transport"):
                        currentValue = values.get("Transport");
                        values.put("Transport", currentValue + expense.getPrice());
                        break;
                    case ("Other"):
                        currentValue = values.get("Other");
                        values.put("Other", currentValue + expense.getPrice());
                        break;
                    default:
                        currentValue = values.get("Constant expenses");
                        values.put("Constant expenses", currentValue + expense.getPrice());
                }

            }
        } else if (timeFrame.equals("This year") && variables.getActiveBudget().getExpenses().size() > 0) {
            for (Expense expense : variables.getActiveBudget().getExpenses()) {
                cal2.setTime(expense.getDate());
                if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
                    switch (expense.getType()) {
                        case ("Groceries"):
                            currentValue = values.get("Groceries");
                            values.put("Groceries", currentValue + expense.getPrice());
                            break;
                        case ("Restaurants"):
                            currentValue = values.get("Restaurants");
                            values.put("Restaurants", currentValue + expense.getPrice());
                            break;
                        case ("Hobbies"):
                            currentValue = values.get("Hobbies");
                            values.put("Hobbies", currentValue + expense.getPrice());
                            break;
                        case ("Clothes"):
                            currentValue = values.get("Clothes");
                            values.put("Clothes", currentValue + expense.getPrice());
                            break;
                        case ("Well-being"):
                            currentValue = values.get("Well-being");
                            values.put("Well-being", currentValue + expense.getPrice());
                            break;
                        case ("Medicines"):
                            currentValue = values.get("Medicines");
                            values.put("Medicines", currentValue + expense.getPrice());
                            break;
                        case ("Transport"):
                            currentValue = values.get("Transport");
                            values.put("Transport", currentValue + expense.getPrice());
                            break;
                        case ("Other"):
                            currentValue = values.get("Other");
                            values.put("Other", currentValue + expense.getPrice());
                            break;
                        default:
                            currentValue = values.get("Constant expenses");
                            values.put("Constant expenses", currentValue + expense.getPrice());
                    }
                }
            }
        } else if (timeFrame.equals("This month") && variables.getActiveBudget().getExpenses().size() > 0) {
            for (Expense expense : variables.getActiveBudget().getExpenses()) {
                cal2.setTime(expense.getDate());
                if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
                    if (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)) {
                        switch (expense.getType()) {
                            case ("Groceries"):
                                currentValue = values.get("Groceries");
                                values.put("Groceries", currentValue + expense.getPrice());
                                break;
                            case ("Restaurants"):
                                currentValue = values.get("Restaurants");
                                values.put("Restaurants", currentValue + expense.getPrice());
                                break;
                            case ("Hobbies"):
                                currentValue = values.get("Hobbies");
                                values.put("Hobbies", currentValue + expense.getPrice());
                                break;
                            case ("Clothes"):
                                currentValue = values.get("Clothes");
                                values.put("Clothes", currentValue + expense.getPrice());
                                break;
                            case ("Well-being"):
                                currentValue = values.get("Well-being");
                                values.put("Well-being", currentValue + expense.getPrice());
                                break;
                            case ("Medicines"):
                                currentValue = values.get("Medicines");
                                values.put("Medicines", currentValue + expense.getPrice());
                                break;
                            case ("Transport"):
                                currentValue = values.get("Transport");
                                values.put("Transport", currentValue + expense.getPrice());
                                break;
                            case ("Other"):
                                currentValue = values.get("Other");
                                values.put("Other", currentValue + expense.getPrice());
                                break;
                            default:
                                currentValue = values.get("Constant expenses");
                                values.put("Constant expenses", currentValue + expense.getPrice());
                        }
                    }
                }
            }
        } else if (timeFrame.equals("This week") && variables.getActiveBudget().getExpenses().size() > 0) {
            for (Expense expense : variables.getActiveBudget().getExpenses()) {
                cal2.setTime(expense.getDate());
                if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
                    if (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)) {
                        if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)) {
                            switch (expense.getType()) {
                                case ("Groceries"):
                                    currentValue = values.get("Groceries");
                                    values.put("Groceries", currentValue + expense.getPrice());
                                    break;
                                case ("Restaurants"):
                                    currentValue = values.get("Restaurants");
                                    values.put("Restaurants", currentValue + expense.getPrice());
                                    break;
                                case ("Hobbies"):
                                    currentValue = values.get("Hobbies");
                                    values.put("Hobbies", currentValue + expense.getPrice());
                                    break;
                                case ("Clothes"):
                                    currentValue = values.get("Clothes");
                                    values.put("Clothes", currentValue + expense.getPrice());
                                    break;
                                case ("Well-being"):
                                    currentValue = values.get("Well-being");
                                    values.put("Well-being", currentValue + expense.getPrice());
                                    break;
                                case ("Medicines"):
                                    currentValue = values.get("Medicines");
                                    values.put("Medicines", currentValue + expense.getPrice());
                                    break;
                                case ("Transport"):
                                    currentValue = values.get("Transport");
                                    values.put("Transport", currentValue + expense.getPrice());
                                    break;
                                case ("Other"):
                                    currentValue = values.get("Other");
                                    values.put("Other", currentValue + expense.getPrice());
                                    break;
                                default:
                                    currentValue = values.get("Constant expenses");
                                    values.put("Constant expenses", currentValue + expense.getPrice());
                            }
                        }
                    }
                }
            }
        } else if (timeFrame.equals("Today") && variables.getActiveBudget().getExpenses().size() > 0) {
            for (Expense expense : variables.getActiveBudget().getExpenses()) {
                cal2.setTime(expense.getDate());
                if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
                    if (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)) {
                        if (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {
                            switch (expense.getType()) {
                                case ("Groceries"):
                                    currentValue = values.get("Groceries");
                                    values.put("Groceries", currentValue + expense.getPrice());
                                    break;
                                case ("Restaurants"):
                                    currentValue = values.get("Restaurants");
                                    values.put("Restaurants", currentValue + expense.getPrice());
                                    break;
                                case ("Hobbies"):
                                    currentValue = values.get("Hobbies");
                                    values.put("Hobbies", currentValue + expense.getPrice());
                                    break;
                                case ("Clothes"):
                                    currentValue = values.get("Clothes");
                                    values.put("Clothes", currentValue + expense.getPrice());
                                    break;
                                case ("Well-being"):
                                    currentValue = values.get("Well-being");
                                    values.put("Well-being", currentValue + expense.getPrice());
                                    break;
                                case ("Medicines"):
                                    currentValue = values.get("Medicines");
                                    values.put("Medicines", currentValue + expense.getPrice());
                                    break;
                                case ("Transport"):
                                    currentValue = values.get("Transport");
                                    values.put("Transport", currentValue + expense.getPrice());
                                    break;
                                case ("Other"):
                                    currentValue = values.get("Other");
                                    values.put("Other", currentValue + expense.getPrice());
                                    break;
                                default:
                                    currentValue = values.get("Constant expenses");
                                    values.put("Constant expenses", currentValue + expense.getPrice());
                            }
                        }
                    }
                }
            }
        }
        return values;
    }

    public void onChangeChartClick() {
        if (barChartShown) {
            pieStats.setVisible(true);
            barStats.setVisible(false);
            barChartShown = false;
        } else {
            pieStats.setVisible(false);
            barStats.setVisible(true);
            barChartShown = true;
        }
    }
}