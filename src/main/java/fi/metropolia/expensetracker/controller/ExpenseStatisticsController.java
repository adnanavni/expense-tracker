package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.Expense;
import fi.metropolia.expensetracker.module.ThemeManager;
import fi.metropolia.expensetracker.module.Variables;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class ExpenseStatisticsController {
    @FXML
    private AnchorPane content;
    @FXML
   private BarChart<String, Double> barStats;
    @FXML
    private PieChart pieStats;
    @FXML
    private ComboBox selectedTimeFrame;
    @FXML
    private Button chartTypeBtn;
    private Boolean barChartShown = true;
    @FXML
    private Label budgetName;
    private Variables variables;
    private ObservableList<PieChart.Data> currentPieValues;
    private Currency currency;
    public void initialize() {
        ThemeManager themeManager = ThemeManager.getInstance();
        content.setStyle(themeManager.getStyle());

    }
    public void setVariables(Variables variables) {
        this.variables = variables;
        currency = Currency.getInstance(variables.getCurrentCurrency());
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

        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Groceries", allValues.get("Groceries")),
                        new PieChart.Data("Restaurants", allValues.get("Restaurants")),
                        new PieChart.Data("Hobbies", allValues.get("Hobbies")),
                        new PieChart.Data("Clothes", allValues.get("Clothes")),
                        new PieChart.Data("Well-being", allValues.get("Well-being")),
                        new PieChart.Data("Medicines", allValues.get("Medicines")),
                        new PieChart.Data("Transport", allValues.get("Transport")),
                        new PieChart.Data("Other", allValues.get("Other")),
                        new PieChart.Data("Constant expenses", allValues.get("Constant expenses")));

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
                    newPieChartData =
                            FXCollections.observableArrayList(
                                    new PieChart.Data("Groceries", values.get("Groceries")),
                                    new PieChart.Data("Restaurants", values.get("Restaurants")),
                                    new PieChart.Data("Hobbies", values.get("Hobbies")),
                                    new PieChart.Data("Clothes", values.get("Clothes")),
                                    new PieChart.Data("Well-being", values.get("Well-being")),
                                    new PieChart.Data("Medicines", values.get("Medicines")),
                                    new PieChart.Data("Transport", values.get("Transport")),
                                    new PieChart.Data("Other", values.get("Other")),
                                    new PieChart.Data("Constant expenses", values.get("Constant expenses")));
                    matches = 0;
                    for (PieChart.Data data : newPieChartData) {
                        for (PieChart.Data data2 :currentPieValues) {
                            if(data.getPieValue() == data2.getPieValue() && data.getName().equals(data2.getName())){
                                matches++;
                            }
                        }
                    }
                    if (matches<9){
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
                    newPieChartData =
                            FXCollections.observableArrayList(
                                    new PieChart.Data("Groceries", values.get("Groceries")),
                                    new PieChart.Data("Restaurants", values.get("Restaurants")),
                                    new PieChart.Data("Hobbies", values.get("Hobbies")),
                                    new PieChart.Data("Clothes", values.get("Clothes")),
                                    new PieChart.Data("Well-being", values.get("Well-being")),
                                    new PieChart.Data("Medicines", values.get("Medicines")),
                                    new PieChart.Data("Transport", values.get("Transport")),
                                    new PieChart.Data("Other", values.get("Other")),
                                    new PieChart.Data("Constant expenses", values.get("Constant expenses")));

                    matches = 0;
                    for (PieChart.Data data : newPieChartData) {
                        for (PieChart.Data data2 :currentPieValues) {
                            if(data.getPieValue() == data2.getPieValue() && data.getName().equals(data2.getName())){
                                matches++;
                            }
                        }
                    }
                    if (matches<9){
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
                    newPieChartData =
                            FXCollections.observableArrayList(
                                    new PieChart.Data("Groceries", values.get("Groceries")),
                                    new PieChart.Data("Restaurants", values.get("Restaurants")),
                                    new PieChart.Data("Hobbies", values.get("Hobbies")),
                                    new PieChart.Data("Clothes", values.get("Clothes")),
                                    new PieChart.Data("Well-being", values.get("Well-being")),
                                    new PieChart.Data("Medicines", values.get("Medicines")),
                                    new PieChart.Data("Transport", values.get("Transport")),
                                    new PieChart.Data("Other", values.get("Other")),
                                    new PieChart.Data("Constant expenses", values.get("Constant expenses")));
                    matches = 0;
                    for (PieChart.Data data : newPieChartData) {
                        for (PieChart.Data data2 :currentPieValues) {
                            if(data.getPieValue() == data2.getPieValue() && data.getName().equals(data2.getName())){
                                matches++;
                            }
                        }
                    }
                    if (matches<9){
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
                    newPieChartData =
                            FXCollections.observableArrayList(
                                    new PieChart.Data("Groceries", values.get("Groceries")),
                                    new PieChart.Data("Restaurants", values.get("Restaurants")),
                                    new PieChart.Data("Hobbies", values.get("Hobbies")),
                                    new PieChart.Data("Clothes", values.get("Clothes")),
                                    new PieChart.Data("Well-being", values.get("Well-being")),
                                    new PieChart.Data("Medicines", values.get("Medicines")),
                                    new PieChart.Data("Transport", values.get("Transport")),
                                    new PieChart.Data("Other", values.get("Other")),
                                    new PieChart.Data("Constant expenses", values.get("Constant expenses")));
                    matches = 0;
                    for (PieChart.Data data : newPieChartData) {
                        for (PieChart.Data data2 :currentPieValues) {
                            if(data.getPieValue() == data2.getPieValue() && data.getName().equals(data2.getName())){
                                matches++;
                            }
                        }
                    }
                    if (matches<9){
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
                    newPieChartData =
                            FXCollections.observableArrayList(
                                    new PieChart.Data("Groceries", values.get("Groceries")),
                                    new PieChart.Data("Restaurants", values.get("Restaurants")),
                                    new PieChart.Data("Hobbies", values.get("Hobbies")),
                                    new PieChart.Data("Clothes", values.get("Clothes")),
                                    new PieChart.Data("Well-being", values.get("Well-being")),
                                    new PieChart.Data("Medicines", values.get("Medicines")),
                                    new PieChart.Data("Transport", values.get("Transport")),
                                    new PieChart.Data("Other", values.get("Other")),
                                    new PieChart.Data("Constant expenses", values.get("Constant expenses")));
                    matches = 0;
                    for (PieChart.Data data : newPieChartData) {
                        for (PieChart.Data data2 :currentPieValues) {
                            if(data.getPieValue() == data2.getPieValue() && data.getName().equals(data2.getName())){
                                matches++;
                            }
                        }
                    }
                    if (matches<9){
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
    private HashMap<String, Double> calculateValues(String timeFrame){
        HashMap<String, Double> values = new HashMap<>();
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        ZoneId defaultZoneId = ZoneId.systemDefault();
        LocalDate currentDate = LocalDate.now();
        cal1.setTime(Date.from(currentDate.atStartOfDay(defaultZoneId).toInstant()));


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

        if(timeFrame.equals("All time") && variables.getActiveBudget().getExpenses().size() > 0){
            for (Expense expense: variables.getActiveBudget().getExpenses()) {
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
            for (Expense expense: variables.getActiveBudget().getExpenses()) {
                cal2.setTime(expense.getDate());
                if(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)){
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
            for (Expense expense: variables.getActiveBudget().getExpenses()) {
                cal2.setTime(expense.getDate());
                if(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)){
                    if(cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)){
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
            for (Expense expense: variables.getActiveBudget().getExpenses()) {
                cal2.setTime(expense.getDate());
                if(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)){
                    if(cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)){
                        if(cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)){
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
        } else if (timeFrame.equals("Today") && variables.getActiveBudget().getExpenses().size() > 0){
            for (Expense expense: variables.getActiveBudget().getExpenses()) {
                cal2.setTime(expense.getDate());
                if(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)){
                    if(cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)){
                        if(cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)){
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
    public void onChangeChartClick(){
        if(barChartShown){
            chartTypeBtn.setText("View as barchart");
            pieStats.setVisible(true);
            barStats.setVisible(false);
            barChartShown = false;
        }
        else {
            chartTypeBtn.setText("View as piechart");
            pieStats.setVisible(false);
            barStats.setVisible(true);
            barChartShown = true;
        }
    }

    public void backToExpense(ActionEvent event) throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("expense-view.fxml"));

        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);

        ExpenseController expenseController = fxmloader.getController();
        expenseController.setVariables(variables);
    }

}
