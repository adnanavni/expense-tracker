package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.*;
import fi.metropolia.expensetracker.module.Dao.BudgetExpenseDao;
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
    private Button cancelModifyBtn;
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


    private Boolean barChartShown = true;
    private Variables variables;
    private Currency currency;
    private LocalizationManager language = LocalizationManager.getInstance();
    private BudgetExpenseDao budgetExpenseDao = new BudgetExpenseDao();
    private HashMap<String, Double> expenses;
    private XYChart.Series<String, Double> series;

    public void initialize() {
        variables = Variables.getInstance();
        currency = Currency.getInstance(variables.getCurrentCurrency());

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

        expenseCombo.setPromptText(language.getString("constantExpense"));
        ConstExpenseBtn.setText(language.getString("remove"));

        modify.setText(language.getString("modify"));
        delete.setText(language.getString("delete"));

        shared.setText(language.getString("shared"));

        updateCharts();

    }

    public void setVariables(Variables variables) {
        this.variables = variables;
        total.setText(language.getString("total"));
        selectTopic.getItems().addAll(variables.getBudgetNames());

        if (variables.getActiveBudget() != null) {
            selectTopic.setValue(variables.getActiveBudget().getName());
            modifyBudget.setVisible(true);
            budgetPane.setVisible(true);
            String budgetText = String.format("%.2f", variables.getBudget());
            activeBudget.setText(variables.getActiveBudget().getName() + ": " + variables.getActiveBudget().getAmount() + " " + currency.getSymbol());
            total.setText(language.getString("total") + " " + budgetText + " " + currency.getSymbol());

        }

        for (ConstantExpense constantExpense : variables.getConstantExpenseArray()) {
            expenseCombo.getItems().add(constantExpense);
        }

        updateCharts();

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

        if (text != null && number.matches("^[0-9]+$")) {
            if (selectTopic.getSelectionModel().getSelectedItem() != null && addBudget.getText() != "") {
                if (selectTopic.getValue() == "New") {

                    if (!isSameBudgetName(text)) {
                        budgetExpenseDao.saveBudget(variables.getLoggedUserId(), budgetName.getText(), Double.parseDouble(addBudget.getText()));
                        variables.resetBudgets();
                        Budget[] budgets = budgetExpenseDao.getBudgets(variables.getLoggedUserId());
                        for (Budget budget : budgets) {
                            variables.createNewBudget(budget);
                        }
                        variables.setActiveBudget(budgetName.getText());
                        activeBudget.setText(variables.getActiveBudget().getName() + ": " + variables.getActiveBudget().getAmount() + " " + currency.getSymbol());
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
            alert.setTitle(language.getString("budget"));
            alert.setHeaderText(language.getString("addBudget"));
            alert.setContentText(language.getString("formCorrect"));
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
            updateCharts();
        }
    }

    @FXML
    protected void modifyBudget() {
        modifyName.setPromptText(language.getString("name"));
        modifyAmount.setPromptText(Variables.getInstance().getActiveBudget().getAmount().toString() + " " + currency.getSymbol());

        modifyBtn.setText(language.getString("modify"));
        cancelModifyBtn.setText(language.getString("cancel"));

        modifyBudget.setVisible(false);
        editBudget.setVisible(true);
    }

    @FXML
    protected void modifyBtnClick() {
        String text = modifyName.getText();
        String number = modifyAmount.getText();

        if ((number != null && number.matches("^[0-9]+$")) && (text == null || text.isEmpty())) {
            budgetExpenseDao.ModifyBudget(variables.getActiveBudget().getName(), Double.parseDouble(number), variables.getActiveBudget().getName());

            variables.modifyBudget(variables.getActiveBudget().getName(), Double.parseDouble(number));
            variables.setActiveBudget(variables.getActiveBudget().getName());

        } else if ((number == null || number.isEmpty()) && (text != null && !isSameBudgetName(text))) {

            budgetExpenseDao.ModifyBudget(variables.getActiveBudget().getName(), variables.getActiveBudget().getAmount(), text);
            variables.modifyBudget(text, variables.getActiveBudget().getAmount());
            variables.setActiveBudget(text);

        } else if ((number != null && number.matches("^[0-9]+$")) && (text != null && !isSameBudgetName(text))) {
            budgetExpenseDao.ModifyBudget(variables.getActiveBudget().getName(), Double.parseDouble(modifyAmount.getText()), modifyName.getText());
            variables.modifyBudget(text, Double.parseDouble(number));
            variables.setActiveBudget(text);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(language.getString("budget"));
            alert.setHeaderText(language.getString("modifyBudget"));
            alert.setContentText(language.getString("formCorrect"));
            alert.showAndWait();
        }
        selectTopic.getItems().setAll(variables.getBudgetNames());
        selectTopic.setValue(variables.getActiveBudget().getName());

        update();
        modifyAmount.setText(null);
        modifyName.setText(null);
        editBudget.setVisible(false);
        modifyBudget.setVisible(true);
    }

    @FXML
    protected void removeBtn() {
        ConstantExpense selectedConstExpense = (ConstantExpense) expenseCombo.getSelectionModel().getSelectedItem();
        budgetExpenseDao.saveExpense(variables.getActiveBudget().getId(), selectedConstExpense.getType(), selectedConstExpense.getAmount(), new Date());
        variables.getActiveBudget().resetExpenses();
        Expense[] expenses = budgetExpenseDao.getExpenses(variables.getActiveBudget().getId());
        for (Expense expense : expenses) {
            variables.getActiveBudget().addExpenseToBudget(expense);
        }
        String budgetText = String.format("%.2f", variables.getBudget());
        total.setText(language.getString("total") + " " + budgetText + " " + currency.getSymbol());
        activeBudget.setText(variables.getActiveBudget().getName() + ": " + variables.getActiveBudget().getAmount() + " " + currency.getSymbol());
        Double budgetExpenses = 0.00;
        if (variables.getActiveBudget().getExpenses().size() > 0) {
            for (Expense expense : variables.getActiveBudget().getExpenses()) {
                budgetExpenses += expense.getPrice();
            }
        }
        updateCharts();
    }

    @FXML
    protected void cancelModifyClick() {
        modifyBudget.setVisible(true);
        editBudget.setVisible(false);
    }

    @FXML
    protected void deleteBtnClick() {

        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDelete.setTitle(language.getString("budget"));
        confirmDelete.setHeaderText(language.getString("areYouSure"));

        Optional<ButtonType> result = confirmDelete.showAndWait();

        if (result.get() == ButtonType.OK) {

            budgetExpenseDao.deleteBudget(variables.getActiveBudget().getId());
            variables.deleteBudget();

            update();
            activeBudget.setText(language.getString("noActive"));
            barStats.setVisible(false);
            selectTopic.getItems().setAll(variables.getBudgetNames());

            selectTopic.setValue(null);
            modifyAmount.setText(null);
            modifyName.setText(null);
            editBudget.setVisible(false);
            budgetPane.setVisible(false);
            modifyBudget.setVisible(false);
        }
    }

    private boolean isSameBudgetName(String name) {
        boolean isSame = false;
        for (Budget budget : variables.getBudgets()) {
            if (Objects.equals(budget.getName(), name)) {
                isSame = true;
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle(language.getString("budget"));
                alert.setHeaderText(language.getString("budgetName"));
                alert.setContentText(language.getString("anotherName"));
                alert.showAndWait();
            }
        }
        return isSame;
    }

    private void update() {
        String totalBudgetAmount = String.format("%.2f", variables.getBudget());
        total.setText(language.getString("total") + " " + totalBudgetAmount + " " + currency.getSymbol());
        if (variables.getActiveBudget() != null)
            activeBudget.setText(variables.getActiveBudget().getName() + ": " + variables.getActiveBudget().getAmount() + " " + currency.getSymbol());
        else activeBudget.setText(language.getString("noActive"));
        Double budgetExpenses = 0.00;
        if (variables.getActiveBudget() != null && variables.getActiveBudget().getExpenses().size() > 0) {
            for (Expense expense : variables.getActiveBudget().getExpenses()) {
                budgetExpenses += expense.getPrice();
            }
        }
    }

    @FXML
    protected void onChangeChartClick() {
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

    @FXML
    protected void btnEnbale() {
        ConstExpenseBtn.setDisable(false);
    }

    public void updateCharts() {

        if (Variables.getInstance().getActiveBudget() != null) {
            expenses = budgetExpenseDao.getExpenseNameAndAmount(Variables.getInstance().getActiveBudget().getId());

            if (expenses.size() > 0) {
                barStats.setVisible(true);

                XYChart.Series<String, Double> barSeries = new XYChart.Series<>();
                barSeries.setName(language.getString("moneySpentt"));

                ObservableList<XYChart.Data<String, Double>> barData = FXCollections.observableArrayList();
                for (Map.Entry<String, Double> entry : expenses.entrySet()) {
                    String originalKey = entry.getKey();
                    String translatedKey = language.getString(originalKey.toLowerCase());
                    barData.add(new XYChart.Data<>(translatedKey, entry.getValue()));
                }

                barSeries.setData(barData);

                barStats.getData().clear();
                barStats.setTitle(language.getString("statistics"));
                barStats.getData().add(barSeries);

                ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
                for (Map.Entry<String, Double> entry : expenses.entrySet()) {
                    pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
                }

                pieStats.setData(pieData);

            } else {
                barStats.setVisible(false);
                pieStats.setVisible(false);
            }
        } else {
            barStats.setVisible(false);
            pieStats.setVisible(false);
        }
    }
}