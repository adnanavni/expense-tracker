package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Currency;
import java.util.Date;
import java.util.Objects;

public class BudgetController {

    @FXML
    private AnchorPane content;

    @FXML
    private Label budget;

    @FXML
    private Label activeBudget;
    @FXML
    private TextField addBudget;

    @FXML
    private TextField budgetName;

    @FXML
    private ComboBox selectTopic;

    @FXML
    private Label specificBudget;

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

    private Variables variables;
    private Currency currency;

    public void initialize() {
        ThemeManager themeManager = ThemeManager.getInstance();
        content.setStyle(themeManager.getStyle());
    }

    public void setVariables(Variables variables) {

        this.variables = variables;
        currency = Currency.getInstance(variables.getCurrentCurrency());

        budget.setText("Budget");
        selectTopic.getItems().addAll(variables.getBudgetNames());

        for (ConstantExpense constantExpense : variables.getConstantExpenseArray()) {
            expenseCombo.getItems().add(constantExpense);
        }

        if (variables.getActiveBudget() != null) {
            String budgetText = String.format("%.2f", variables.getBudget());
            activeBudget.setText(variables.getActiveBudget().getName());
            budget.setText("Total: " + budgetText + " " + currency.getSymbol());
        }
    }

    @FXML
    protected void backToMain(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(MainApplication.class.getResource("main-view.fxml"));
        content.getChildren().setAll(pane);
    }

    @FXML
    protected void addToBudget() {
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
                    Dao loginSignupDao = new Dao();
                    loginSignupDao.saveBudget(variables.getLoggedUserId(), budgetName.getText(), Double.parseDouble(addBudget.getText()));
                    variables.resetBudgets();
                    Budget[] budgets = loginSignupDao.getBudgets(variables.getLoggedUserId());
                    for (Budget budget : budgets) {
                        variables.createNewBudget(budget);
                    }
                    variables.setActiveBudget(budgetName.getText());
                    activeBudget.setText(variables.getActiveBudget().getName());
                    String budgetText = String.format("%.2f", variables.getBudget());
                    budget.setText("Total: " + budgetText + " " + currency.getSymbol());

                }
            }
        }
        selectTopic.getItems().setAll(variables.getBudgetNames());
        specificBudget.setText(variables.getActiveBudget().getName() + " " + variables.getActiveBudget().getAmount() + " " + currency.getSymbol());

        selectTopic.setValue(null);
        addBudget.setText(null);
        budgetName.setText(null);
        newBudget.setVisible(false);
        editBudget.setVisible(false);
        budgetPane.setVisible(true);
    }

    @FXML
    protected void onSelectTopic() {
        if (selectTopic.getValue() == "New") {
            editBudget.setVisible(false);
            newBudget.setVisible(true);
        } else if (selectTopic.getValue() != null) {
            newBudget.setVisible(false);
            editBudget.setVisible(true);
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
    protected void removeBtn() {
        ConstantExpense selectedConstExpense = (ConstantExpense) expenseCombo.getSelectionModel().getSelectedItem();


        Dao loginSignupDao = new Dao();
        loginSignupDao.saveExpense(variables.getActiveBudget().getId(), selectedConstExpense.getType(), selectedConstExpense.getAmount(), new Date());
        variables.getActiveBudget().resetExpenses();
        Expense[] expenses = loginSignupDao.getExpenses(variables.getActiveBudget().getId());
        for (Expense expense : expenses) {
            variables.getActiveBudget().addExpenseToBudget(expense);
        }
        String budgetText = String.format("%.2f", variables.getBudget());
        this.budget.setText("Total: " + budgetText + " " + currency.getSymbol());
        Double budgetExpenses = 0.00;
        if (variables.getActiveBudget().getExpenses().size() > 0) {
            for (Expense expense : variables.getActiveBudget().getExpenses()) {
                budgetExpenses += expense.getPrice();
            }
        }

        specificBudget.setText(variables.getActiveBudget().getName() + " " + (variables.getActiveBudget().getAmount() - budgetExpenses) + " " + currency.getSymbol());


    }

    @FXML
    protected void modifyBtnClick() {
        Dao loginSignupDao = new Dao();

        if (modifyName.getText() == "") {
            modifyName.setText(variables.getActiveBudget().getName());
        }
        if (modifyAmount.getText() == "") {
            modifyAmount.setText(variables.getActiveBudget().getAmount().toString());
        }

        loginSignupDao.ModifyBudget(selectTopic.getValue().toString(), Double.parseDouble(modifyAmount.getText()), modifyName.getText());

        variables.modifyBudget(modifyName.getText(), Double.parseDouble(modifyAmount.getText()));
        variables.setActiveBudget(modifyName.getText());

        update();
        selectTopic.getItems().setAll(variables.getBudgetNames());


        selectTopic.setValue(null);
        modifyAmount.setText(null);
        modifyName.setText(null);
        editBudget.setVisible(false);
    }

    @FXML
    protected void deleteBtnClick() {
        Dao loginSignupDao = new Dao();

        loginSignupDao.deleteBudget(variables.getActiveBudget().getId());
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

    private void update() {
        String budgetText = String.format("%.2f", variables.getBudget());
        this.budget.setText("Total: " + budgetText + " " + currency.getSymbol());
        activeBudget.setText(variables.getActiveBudget().getName());
        Double budgetExpenses = 0.00;
        if (variables.getActiveBudget().getExpenses().size() > 0) {
            for (Expense expense : variables.getActiveBudget().getExpenses()) {
                budgetExpenses += expense.getPrice();
            }
        }
        String activeBudgetText = String.format("%.2f", variables.getActiveBudget().getAmount() - budgetExpenses);
        specificBudget.setText(variables.getActiveBudget().getName() + " " + activeBudgetText + " " + currency.getSymbol());
    }
}