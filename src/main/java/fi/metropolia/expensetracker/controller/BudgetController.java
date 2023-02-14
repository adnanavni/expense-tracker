package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.Variables;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Currency;

public class BudgetController {

    @FXML
    private AnchorPane content;

    private Variables variables;
    private Currency currency;

    @FXML
    private Label budget;

    public void setCalculator(Variables variables) {
        this.variables = variables;
        currency = Currency.getInstance(variables.getCurrentCurrency());
        String budgetText = String.format("%.2f", variables.getBudget());
        budget.setText(budgetText + " " + currency.getSymbol());
    }

    public void backToMain(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(MainApplication.class.getResource("main-view.fxml"));
        content.getChildren().setAll(pane);
    }

}
