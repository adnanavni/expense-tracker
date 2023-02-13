package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.module.Calculator;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Currency;

public class MainController {
    @FXML
    private Label expense;

    @FXML
    private Label income;

    @FXML
    private Label budget;

    @FXML
    private TextField addExpense;

    @FXML
    private TextField addIncome;

    @FXML
    private TextField addBudget;

    @FXML
    private ComboBox selectCurrency;

    private Calculator calculator = new Calculator();
    private Currency currency = Currency.getInstance(calculator.getCurrentCurrency());

    @FXML
    public void initialize() {
        selectCurrency.getItems().addAll(calculator.getCurrencyCodes());
        expense.setText(calculator.getExpense().toString() + " " + currency.getSymbol());
        budget.setText(calculator.getBudget().toString() + " " + currency.getSymbol());
        income.setText(calculator.getIncome().toString() + " " + currency.getSymbol());
    }

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
    protected void onChooseCurrencyBtnClick() {

            calculator.setCurrentCourseMultiplier(selectCurrency.getSelectionModel().getSelectedItem().toString());
            currency = Currency.getInstance(calculator.getCurrentCurrency());
            income.setText(calculator.getIncome().toString() + " " + currency.getSymbol());
            expense.setText(calculator.getExpense().toString() + " " + currency.getSymbol());
            budget.setText(calculator.getBudget().toString() + " " + currency.getSymbol());

    }
    @FXML
    protected void none() {
        System.out.println("Somethinghere");
    }
}