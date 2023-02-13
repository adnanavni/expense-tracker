package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.Calculator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.util.Currency;

public class MainController {

    @FXML
    private Label budget;

    @FXML
    private ComboBox selectCurrency;

    @FXML
    private AnchorPane content;

    private Calculator calculator = new Calculator();
    private Currency currency = Currency.getInstance(calculator.getCurrentCurrency());

    @FXML
    public void initialize() {
        selectCurrency.getItems().addAll(calculator.getCurrencyCodes());
        budget.setText(calculator.getBudget().toString() + " " + currency.getSymbol());
    }

    public void changeWindowToBudget(ActionEvent event) throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("budget-view.fxml"));
        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);

        BudgetController budgetController = fxmloader.getController();
        budgetController.setCalculator(calculator);

    }

    public void changeWindowToIncome(ActionEvent event) throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("income-view.fxml"));
        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);

        IncomeController incomeController = fxmloader.getController();
        incomeController.setCalculator(calculator);
    }

    public void changeWindowToExpense(ActionEvent event) throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("expense-view.fxml"));
        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);

        ExpenseController expenseController = fxmloader.getController();
        expenseController.setCalculator(calculator);
    }

    @FXML
    protected void onChooseCurrencyBtnClick() {

        calculator.setCurrentCourseMultiplier(selectCurrency.getSelectionModel().getSelectedItem().toString());
        currency = Currency.getInstance(calculator.getCurrentCurrency());
        budget.setText(calculator.getBudget().toString() + " " + currency.getSymbol());

    }

    public Calculator getCalculator() {
        return calculator;
    }

/*
    @FXML
    protected void onExpenseAddClick() {
        if(calculator.getBudget() > 0 && addExpense.getText() != "") {
            calculator.calulate("subtractWithExpenses", Double.parseDouble(addExpense.getText()));
            expense.setText(calculator.getExpense().toString() + " " + currency.getSymbol());
            budget.setText(calculator.getBudget().toString() + " " + currency.getSymbol());
        }
    }

    @FXML
    protected void onBudgetAddClick() {
        if(addBudget.getText() != "") {
            calculator.calulate("addToBudget", Double.parseDouble(addBudget.getText()));
            budget.setText(calculator.getBudget().toString() + " " + currency.getSymbol());
        }
    }

    @FXML
    protected void onBudgetRemoveClick() {
        if(addBudget.getText() != "") {
            calculator.calulate("subtractFromBudget", Double.parseDouble(addBudget.getText()));
            budget.setText(calculator.getBudget().toString() + " " + currency.getSymbol());
        }
    }

    @FXML
    protected void onIncomeAddClick() {
        if(addBudget.getText() != "") {
            calculator.calulate("addToIncome", Double.parseDouble(addIncome.getText()));
            income.setText(calculator.getIncome().toString() + " " + currency.getSymbol());
        }
    }

    @FXML
    protected void none() {
        System.out.println("Somethinghere");
    }
*/
}