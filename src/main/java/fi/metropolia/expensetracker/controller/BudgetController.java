package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.Budget;
import fi.metropolia.expensetracker.module.ThemeManager;
import fi.metropolia.expensetracker.module.Variables;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Currency;
import java.util.Objects;

public class BudgetController {

    @FXML
    private AnchorPane content;

    @FXML
    private Label budget;

    @FXML
    private Label activeBudget;
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

    @FXML
    private AnchorPane budgetPane;

    @FXML
    private ComboBox expenseCombo;

    private Variables variables;
    private Currency currency;

    public void initialize() {
        ThemeManager themeManager = ThemeManager.getInstance();
        content.setStyle(themeManager.getStyle());
    }

    public void setVariables(Variables variables) {

        this.variables = variables;
        currency = Currency.getInstance(variables.getCurrentCurrency());

        budget.setText("Budget");
        selectTopic.getItems().addAll(variables.getBudgetNames());
        expenseCombo.getItems().addAll(variables.getConstExpenses());

        if (variables.getActiveBudget() != null) {
            String budgetText = String.format("%.2f", variables.getBudget());
            activeBudget.setText(variables.getActiveBudget().getName());
            budget.setText("Total: " + budgetText + " " + currency.getSymbol());
        }
    }

    @FXML
    protected void backToMain(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(MainApplication.class.getResource("main-view.fxml"));
        content.getChildren().setAll(pane);
    }

    @FXML
    protected void addToBudget() {
        if (selectTopic.getSelectionModel().getSelectedItem() != null && addBudget.getText() != "") {
            if (selectTopic.getValue() == "New") {
                boolean willAdd = true;
                for (Budget budget : variables.getBudgets()) {
                    if (Objects.equals(budget.getName(), budgetName.getText())) {
                        willAdd = false;
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Name Exists");
                        alert.setHeaderText("Budget with given name already exists!");
                        alert.setContentText("Choose another name.");
                        alert.showAndWait();
                    }
                }
                if (willAdd) {
                    Budget newBudget = new Budget(Double.parseDouble(addBudget.getText()), budgetName.getText(), variables.getCurrentCurrency());
                    variables.createNewBudget(newBudget);
                    variables.setActiveBudget(newBudget);
                    activeBudget.setText(variables.getActiveBudget().getName());
                    String budgetText = String.format("%.2f", variables.getBudget());
                    budget.setText("Total: " + budgetText + " " + currency.getSymbol());
                }
            }
        }
        selectTopic.getItems().setAll(variables.getBudgetNames());
        specificBudget.setText(variables.getActiveBudget().getName() + " " + variables.getActiveBudget().getAmount() + " " + currency.getSymbol());

        selectTopic.setValue(null);
        budgetName.setText(null);
        addBudget.setText(null);

        budgetPane.setVisible(true);
    }

    @FXML
    protected void onSelectTopic() {
        if (selectTopic.getValue() == "New") {
            budgetName.setVisible(true);
        } else if (selectTopic.getValue() != null) {
            budgetName.setVisible(false);
            budgetPane.setVisible(true);
            for (Budget budget : variables.getBudgets()) {
                if (budget.getName() == selectTopic.getValue()) {
                    variables.setActiveBudget(budget);
                    String budgetText = String.format("%.2f", variables.getBudget());
                    this.budget.setText("Total: " + budgetText + " " + currency.getSymbol());
                    activeBudget.setText(variables.getActiveBudget().getName());
                    specificBudget.setText(variables.getActiveBudget().getName() + " " + variables.getActiveBudget().getAmount() + " " + currency.getSymbol());
                }
            }
        }
    }

    @FXML
    protected void removeBtn() {
        if (variables.getConstExpense(expenseCombo.getValue().toString()) == 0.00) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(expenseCombo.getValue().toString() + " not found");
            alert.setHeaderText("Set the amount for " + expenseCombo.getValue().toString());
            alert.setContentText("This can be found in expenses menu");
            alert.showAndWait();
        } else {
            variables.calculate("subtractFromBudget", variables.getConstExpense(expenseCombo.getValue().toString()));
            String budgetText = String.format("%.2f", variables.getBudget());
            this.budget.setText("Total: " + budgetText + " " + currency.getSymbol());
            specificBudget.setText(variables.getActiveBudget().getName() + " " + variables.getActiveBudget().getAmount() + " " + currency.getSymbol());

        }
    }
}