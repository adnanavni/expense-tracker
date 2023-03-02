package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
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
        ArrayList<String> constExpenseNames = variables.getConstExpenses();
        for(Integer i = 0; i < constExpenseNames.size(); i++){
            ConstantExpense expenseToAdd = new ConstantExpense(constExpenseNames.get(i), variables.getConstExpense(constExpenseNames.get(i))
                    , currency.getSymbol());
            expenseCombo.getItems().add(expenseToAdd);
        }

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
                    Login_Signup_Dao loginSignupDao = new Login_Signup_Dao();
                    loginSignupDao.saveBudget(variables.getLoggedUserId(), budgetName.getText(), Double.parseDouble(addBudget.getText()));
                    variables.resetBudgets();
                    Budget[] budgets = loginSignupDao.getBudgets(variables.getLoggedUserId());
                    for (Budget budget : budgets) {
                        variables.createNewBudget(budget);
                    }
                    variables.setActiveBudget(budgetName.getText());
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
        budgetName.setVisible(false);
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
                    variables.setActiveBudget(budget.getName());
                    String budgetText = String.format("%.2f", variables.getBudget());
                    this.budget.setText("Total: " + budgetText + " " + currency.getSymbol());
                    activeBudget.setText(variables.getActiveBudget().getName());
                    Double budgetExpenses = 0.00;
                    if (variables.getActiveBudget().getExpenses().size() > 0){
                        for (Expense expense : variables.getActiveBudget().getExpenses()) {
                            budgetExpenses += expense.getPrice();
                        }
                    }
                    specificBudget.setText(variables.getActiveBudget().getName() + " " + (variables.getActiveBudget().getAmount()-budgetExpenses) + " " + currency.getSymbol());
                }
            }
        }
    }

    @FXML
    protected void removeBtn() {
        ConstantExpense selectedConstExpense = (ConstantExpense) expenseCombo.getSelectionModel().getSelectedItem();
        if (variables.getConstExpense(selectedConstExpense.getType()) == 0.00) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(selectedConstExpense.getType() + " not found");
            alert.setHeaderText("Set the amount for " + selectedConstExpense.getType());
            alert.setContentText("This can be found in expenses menu");
            alert.showAndWait();
        } else {

            Login_Signup_Dao loginSignupDao = new Login_Signup_Dao();
            loginSignupDao.saveExpense(variables.getActiveBudget().getId(), selectedConstExpense.getType(), selectedConstExpense.getAmount(), new Date());
            variables.getActiveBudget().resetExpenses();
            Expense[] expenses = loginSignupDao.getExpenses(variables.getActiveBudget().getId());
            for (Expense expense : expenses) {
                variables.getActiveBudget().addExpenseToBudget(expense);
            }

            String budgetText = String.format("%.2f", variables.getBudget());
            this.budget.setText("Total: " + budgetText + " " + currency.getSymbol());
            Double budgetExpenses = 0.00;
            if (variables.getActiveBudget().getExpenses().size() > 0){
                for (Expense expense : variables.getActiveBudget().getExpenses()) {
                    budgetExpenses += expense.getPrice();
                }
            }

            specificBudget.setText(variables.getActiveBudget().getName() + " " + (variables.getActiveBudget().getAmount()-budgetExpenses) + " " + currency.getSymbol());

        }
    }
}