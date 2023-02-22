package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.Budget;
import fi.metropolia.expensetracker.module.Expense;
import fi.metropolia.expensetracker.module.ThemeManager;
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
    @FXML
    private DatePicker selectedDate;
    @FXML
    private Label activeBudgetTxt;
    @FXML
    private ListView expenseHistory;
    @FXML
    private ComboBox selectCategory;
    @FXML
    private TextField constExpenseName;
    @FXML
    private TextField constExpense;


    private Variables variables;
    private Currency currency;


    public void initialize() {
        ThemeManager themeManager = ThemeManager.getInstance();
        content.setStyle(themeManager.getStyle());
    }

    public void backToMain(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(MainApplication.class.getResource("main-view.fxml"));
        content.getChildren().setAll(pane);
    }

    public void setVariables(Variables variables) {
        this.variables = variables;
        currency = Currency.getInstance(variables.getCurrentCurrency());
        Budget activeBudget = variables.getActiveBudget();
        activeBudgetTxt.setText(activeBudget.getName());
        String budgetText = String.format("%.2f", variables.getActiveBudget().getAmount());
        this.expense.setText(budgetText + " " + currency.getSymbol());
        selectTopic.getItems().addAll(variables.getTopics());
        selectCategory.getItems().addAll(variables.getConstExpenses());
        selectCategory.getItems().add(0, "New");
        expenseHistory.getItems().addAll(activeBudget.getExpenses());
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
                        variables.getActiveBudget().removeExpenseFromBudget(selected);
                        expenseHistory.getItems().clear();
                        expenseHistory.getItems().addAll(variables.getActiveBudget().getExpenses());
                        variables.calculate("subtractExpense", selected.getPrice());
                        variables.setCategories(selected.getType(), selected.getPrice(), false);
                        String budgetText = String.format("%.2f", variables.getActiveBudget().getAmount());
                        expense.setText(budgetText + " " + currency.getSymbol());
                    }
                } else {
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
        if (selectTopic.getSelectionModel().getSelectedItem() != null) {
            variables.calculate("subtractWithExpenses", Double.parseDouble(addExpense.getText()));
            String budgetText = String.format("%.2f", variables.getActiveBudget().getAmount());
            expense.setText(budgetText + " " + currency.getSymbol());
            variables.setCategories(selectTopic.getSelectionModel().getSelectedItem().toString(), Double.parseDouble(addExpense.getText()), true);
            LocalDate expenseDate = LocalDate.now();
            if (selectedDate.getValue() != null) {
                expenseDate = selectedDate.getValue();
            }

            Expense addedExpense = new Expense(Double.parseDouble(addExpense.getText()),
                    selectTopic.getSelectionModel().getSelectedItem().toString(), expenseDate, currency.getSymbol());
            variables.getActiveBudget().addExpenseToBudget(addedExpense);
            expenseHistory.getItems().clear();
            expenseHistory.getItems().addAll(variables.getActiveBudget().getExpenses());
        }

        addExpense.setText(null);
        selectTopic.setValue(null);
        selectedDate.setValue(null);
    }

    @FXML
    protected void selectConst() {
        if (selectCategory.getValue() == "New") {
            constExpenseName.setVisible(true);
        } else if (selectCategory.getValue() != null) {
            constExpenseName.setVisible(false);
        }
    }

    @FXML
    protected void setConstExpense() {
        variables.setConstExpenses(selectCategory.getValue().toString(), Double.parseDouble(constExpense.getText()));

        selectCategory.setValue(null);
        constExpense.setText(null);
        constExpenseName.setText(null);
    }

    @FXML
    protected void btnEnable() {
        if (selectTopic.getSelectionModel().getSelectedItem() != null && addExpense.getText() != null) {
            addBtn.setDisable(false);
        }
    }
}
