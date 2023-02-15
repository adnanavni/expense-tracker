package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.Budget;
import fi.metropolia.expensetracker.module.Expense;
import fi.metropolia.expensetracker.module.Variables;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.util.Currency;

public class MainController {

    @FXML
    private Label budget;
    @FXML
    private Button expenseBtn;
    @FXML
    private ComboBox selectCurrency;

    @FXML
    private AnchorPane content;

    private Variables variables = Variables.getInstance();
    private Currency currency = Currency.getInstance(variables.getCurrentCurrency());

    @FXML
    public void initialize() {
        selectCurrency.getItems().addAll(variables.getCurrencyCodes());

        if(variables.getBudgets().size()<1){
            budget.setText("No budgets yet");
            expenseBtn.setDisable(true);
            selectCurrency.setDisable(true);
        }
        else {
            expenseBtn.setDisable(false);
            selectCurrency.setDisable(false);
            Double totalBudget = 0.00;
            for(Integer i=0; i< variables.getBudgets().size(); i++){
                totalBudget += variables.getBudgets().get(i).getAmount();
            }
            String budgetText = String.format("%.2f", totalBudget);
            budget.setText(budgetText + " " + currency.getSymbol());
        }


    }

    public void changeWindowToBudget(ActionEvent event) throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("budget-view.fxml"));
        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);

        BudgetController budgetController = fxmloader.getController();
        budgetController.setCalculator(variables);

    }

    public void changeWindowToIncome(ActionEvent event) throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("income-view.fxml"));
        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);

        IncomeController incomeController = fxmloader.getController();
        incomeController.setCalculator(variables);
    }

    public void changeWindowToExpense(ActionEvent event) throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("expense-view.fxml"));
        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);

        ExpenseController expenseController = fxmloader.getController();
        expenseController.setCalculator(variables);
    }

    @FXML
    protected void onChooseCurrencyBtnClick() {

        variables.setCurrentCourseMultiplier(selectCurrency.getSelectionModel().getSelectedItem().toString());
        currency = Currency.getInstance(variables.getCurrentCurrency());

        Double totalBudget = 0.00;
        for(Integer i=0; i< variables.getBudgets().size(); i++){
            totalBudget += variables.getBudgets().get(i).getAmount();
        }
        String budgetText = String.format("%.2f", totalBudget);
        budget.setText(budgetText + " " + currency.getSymbol());



    }

/*
    @FXML
    protected void onExpenseAddClick() {
        if(calculator.getBudget() > 0 && addExpense.getText() != "") {
            calculator.calulate("subtractWithExpenses", Double.parseDouble(addExpense.getText()));
            expense.setText(calculator.getExpense().toString() + " " + currency.getSymbol());
            budget.setText(calculator.getBudget().toString() + " " + currency.getSymbol());
        }
    }

    @FXML
    protected void onBudgetAddClick() {
        if(addBudget.getText() != "") {
            calculator.calulate("addToBudget", Double.parseDouble(addBudget.getText()));
            budget.setText(calculator.getBudget().toString() + " " + currency.getSymbol());
        }
    }

    @FXML
    protected void onBudgetRemoveClick() {
        if(addBudget.getText() != "") {
            calculator.calulate("subtractFromBudget", Double.parseDouble(addBudget.getText()));
            budget.setText(calculator.getBudget().toString() + " " + currency.getSymbol());
        }
    }

    @FXML
    protected void onIncomeAddClick() {
        if(addBudget.getText() != "") {
            calculator.calulate("addToIncome", Double.parseDouble(addIncome.getText()));
            income.setText(calculator.getIncome().toString() + " " + currency.getSymbol());
        }
    }

    @FXML
    protected void none() {
        System.out.println("Somethinghere");
    }
*/
}