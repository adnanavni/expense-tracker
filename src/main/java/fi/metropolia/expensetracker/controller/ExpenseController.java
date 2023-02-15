package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.Expense;
import fi.metropolia.expensetracker.module.Variables;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Date;
import java.util.Optional;

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
        this.expense.setText("Expenses: " + budgetText + " " + currency.getSymbol());
        selectTopic.getItems().addAll(variables.getTopics());

        expenseHistory.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                int selectedIndex = expenseHistory.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0) {
                    Expense selected = (Expense) expenseHistory.getItems().get(selectedIndex);
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Expense deletion");
                    alert.setHeaderText("Delete selected expense?");
                    alert.setContentText(selected.toString());

                    Optional<ButtonType> option = alert.showAndWait();

                    if (option.get() == ButtonType.OK) {
                        expenseHistory.getItems().remove(selected);
                        variables.calculate("subtractExpense", selected.getPrice());
                        variables.setCategories(selected.getType(), selected.getPrice(), false);
                        String expenseText = String.format("%.2f", variables.getExpense());
                        expense.setText(expenseText + " " + currency.getSymbol());
                    }
                }
                else {
                    // Nothing selected.
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("No selection!");
                    alert.setHeaderText("No selected expense!");
                    alert.setContentText("Click an existing expense.");
                    alert.showAndWait();
                }
            }
        });
    }

    @FXML
    protected void onExpenseAddClick() {
        if(selectTopic.getSelectionModel().getSelectedItem() != null) {
            variables.calculate("subtractWithExpenses", Double.parseDouble(addExpense.getText()));
            String budgetText = String.format("%.2f", variables.getExpense());
            expense.setText(budgetText + " " + currency.getSymbol());
            variables.setCategories(selectTopic.getSelectionModel().getSelectedItem().toString(), Double.parseDouble(addExpense.getText()), true);
            LocalDate expenseDate = LocalDate.now();
            if(selectedDate.getValue() != null){
                expenseDate = selectedDate.getValue();
            }

            Expense addedExpense = new Expense(Double.parseDouble(addExpense.getText()),
                    selectTopic.getSelectionModel().getSelectedItem().toString(), expenseDate, currency.getSymbol());
            expenseHistory.getItems().add(addedExpense);
        }
    }

    @FXML
    protected void btnEnable() {
        if (selectTopic.getSelectionModel().getSelectedItem() != null && addExpense.getText() != null) {
            addBtn.setDisable(false);
        }
    }
}
