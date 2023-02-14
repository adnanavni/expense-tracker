package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.Expense;
import fi.metropolia.expensetracker.module.Variables;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Date;

public class ExpenseController {
    @FXML
    private AnchorPane content;
    @FXML
    private Label expense;
    @FXML
    private TextField addExpense;

    @FXML
    private ComboBox selectTopic;

    @FXML
    private Button addBtn;
    private Variables variables;
    private Currency currency;
    @FXML
    private DatePicker selectedDate;

    @FXML
    private ListView expenseHistory;

    public void backToMain(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(MainApplication.class.getResource("main-view.fxml"));
        content.getChildren().setAll(pane);
    }

    public void setCalculator(Variables variables) {
        this.variables = variables;
        currency = Currency.getInstance(variables.getCurrentCurrency());
        String budgetText = String.format("%.2f", variables.getExpense());
        this.expense.setText(budgetText + " " + currency.getSymbol());
        selectTopic.getItems().addAll(variables.getTopics());
    }

    @FXML
    protected void onExpenseAddClick() {
        if(selectTopic.getSelectionModel().getSelectedItem() != null) {
            variables.calculate("subtractWithExpenses", Double.parseDouble(addExpense.getText()));
            String budgetText = String.format("%.2f", variables.getExpense());
            expense.setText(budgetText + " " + currency.getSymbol());
            variables.setCategories(selectTopic.getSelectionModel().getSelectedItem().toString(), Double.parseDouble(addExpense.getText()));
            LocalDate expenseDate = LocalDate.now();
            if(selectedDate.getValue() != null){
                expenseDate = selectedDate.getValue();
            }
            String expensePrice = String.format("%.2f", Double.parseDouble(addExpense.getText()));
            Expense addedExpense = new Expense(String.format("%.2f", Double.parseDouble(addExpense.getText())),
                    selectTopic.getSelectionModel().getSelectedItem().toString(), expenseDate, currency.getSymbol());
            expenseHistory.getItems().add(addedExpense);
        }
    }

    @FXML
    protected void BtnEnable() {
        if (selectTopic.getSelectionModel().getSelectedItem() != null && addExpense.getText() != null) {
            addBtn.setDisable(false);
        }
    }
}
