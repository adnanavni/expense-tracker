package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.module.Calculator;
import javafx.fxml.FXML;
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

    private Calculator calculator = new Calculator();

    @FXML
    protected void onExpenseAddClick() {
        if(calculator.getBudget() > 0 && addExpense.getText() != "") {
            calculator.calulate("subtractWithExpenses", Integer.parseInt(addExpense.getText()));
            expense.setText(calculator.getExpense().toString());
            budget.setText(calculator.getBudget().toString());
        }
    }

    @FXML
    protected void onBudgetAddClick() {
        if(addBudget.getText() != "") {
            calculator.calulate("addToBudget", Integer.parseInt(addBudget.getText()));
            budget.setText(calculator.getBudget().toString());
        }
    }

    @FXML
    protected void onBudgetRemoveClick() {
        if(addBudget.getText() != "") {
            calculator.calulate("subtractFromBudget", Integer.parseInt(addBudget.getText()));
            budget.setText(calculator.getBudget().toString());
        }
    }

    @FXML
    protected void onIncomeAddClick() {
        if(addBudget.getText() != "") {
            calculator.calulate("addToIncome", Integer.parseInt(addIncome.getText()));
            income.setText(calculator.getIncome().toString());
        }
    }
    @FXML
    protected void none() {
        System.out.println("Somethinghere");
    }
}