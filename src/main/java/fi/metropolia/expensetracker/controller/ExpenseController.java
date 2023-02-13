package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.Calculator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Currency;

public class ExpenseController {
    @FXML
    private AnchorPane content;
    @FXML
    private Label expense;
    @FXML
    private TextField addExpense;
    private Calculator calculator;
    private Currency currency;

    public void backToMain(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(MainApplication.class.getResource("main-view.fxml"));
        content.getChildren().setAll(pane);
    }

    public void setCalculator(Calculator calculator) {
        this.calculator = calculator;
        currency = Currency.getInstance(calculator.getCurrentCurrency());
        this.expense.setText(calculator.getExpense().toString() + " " + currency.getSymbol());

    }

    @FXML
    protected void onExpenseAddClick() {
        calculator.calulate("subtractWithExpenses", Double.parseDouble(addExpense.getText()));
        expense.setText(calculator.getExpense().toString() + " " + currency.getSymbol());
    }

}
