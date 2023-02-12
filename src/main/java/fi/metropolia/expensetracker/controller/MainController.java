package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.module.Calculator;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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

    @FXML
    public void initialize() {
        selectCurrency.getItems().addAll("SEK", "EUR", "USD");
    }

    @FXML
    protected void onExpenseAddClick() {
        if(calculator.getBudget() > 0 && addExpense.getText() != "") {
            calculator.calulate("subtractWithExpenses", Double.parseDouble(addExpense.getText()));
            expense.setText(calculator.getExpense().toString());
            budget.setText(calculator.getBudget().toString());
        }
    }

    @FXML
    protected void onBudgetAddClick() {
        if(addBudget.getText() != "") {
            calculator.calulate("addToBudget", Double.parseDouble(addBudget.getText()));
            budget.setText(calculator.getBudget().toString());
        }
    }

    @FXML
    protected void onBudgetRemoveClick() {
        if(addBudget.getText() != "") {
            calculator.calulate("subtractFromBudget", Double.parseDouble(addBudget.getText()));
            budget.setText(calculator.getBudget().toString());
        }
    }

    @FXML
    protected void onIncomeAddClick() {
        if(addBudget.getText() != "") {
            calculator.calulate("addToIncome", Double.parseDouble(addIncome.getText()));
            income.setText(calculator.getIncome().toString());
        }
    }

    @FXML
    protected void onChooseCurrencyBtnClick() {

            calculator.setCurrentCourseMultiplier(selectCurrency.getSelectionModel().getSelectedItem().toString());
            income.setText(calculator.getIncome().toString());
            expense.setText(calculator.getExpense().toString());
            budget.setText(calculator.getBudget().toString());

    }
    @FXML
    protected void none() {
        System.out.println("Somethinghere");
    }
}