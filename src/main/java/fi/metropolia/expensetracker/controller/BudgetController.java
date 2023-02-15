package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.Variables;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Currency;

public class BudgetController {

    @FXML
    private AnchorPane content;

    @FXML
    private Label budget;

    @FXML
    private TextField addBudget;

    @FXML
    private TextField budgetName;

    @FXML
    private ComboBox selectTopic;

    @FXML
    private Button addBtn;

    @FXML
    private Label specificBudget;

    private Variables variables;
    private Currency currency;

    public void setCalculator(Variables variables) {
        this.variables = variables;
        currency = Currency.getInstance(variables.getCurrentCurrency());
        String budgetText = String.format("%.2f", variables.getBudget());
        budget.setText("Total: " + budgetText + " " + currency.getSymbol());
        selectTopic.getItems().addAll(variables.getBudgetNames());
    }

    @FXML
    protected void backToMain(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(MainApplication.class.getResource("main-view.fxml"));
        content.getChildren().setAll(pane);
    }

    @FXML
    protected void addToBudget() {
        if (selectTopic.getSelectionModel().getSelectedItem() != null && addBudget.getText() != "") {
            variables.calculate("addToBudget", Double.parseDouble(addBudget.getText()));
            budget.setText("Total: " + variables.getBudget().toString() + " " + currency.getSymbol());
            if (selectTopic.getValue() == "New") {
                variables.createNewBudget(Double.parseDouble(addBudget.getText()), budgetName.getText());
            }
        }
        selectTopic.getItems().setAll(variables.getBudgetNames());
    }

    @FXML
    protected void onSelectTopic() {
        if (selectTopic.getValue() == "New") {
            budgetName.setVisible(true);
        } else if (selectTopic.getValue() != null) {
            budgetName.setVisible(false);
            specificBudget.setText(selectTopic.getValue().toString() + " " + (variables.getSpecificBudget(selectTopic.getValue().toString())).toString() + " " + currency.getSymbol());
            specificBudget.setVisible(true);
        }
    }
}
