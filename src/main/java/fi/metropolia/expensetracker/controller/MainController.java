package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Currency;
import java.time.LocalDate;
import java.util.Date;

public class MainController {

    @FXML
    private Label budget;
    @FXML
    private Button expenseBtn;
    @FXML
    private ComboBox selectCurrency;
    @FXML
    private ImageView settingsIcon;
    @FXML
    private AnchorPane content;

    private Variables variables = Variables.getInstance();
    private SalarySingle salarySingle = SalarySingle.getInstance();
    private LocalDate date;
    private Currency currency = Currency.getInstance(variables.getCurrentCurrency());

    @FXML
    public void initialize() {
        ThemeManager themeManager = ThemeManager.getInstance();
        content.setStyle(themeManager.getStyle());

        if (variables.getBudgets().size() < 1) {
            budget.setText("No budgets yet");
            expenseBtn.setDisable(true);
        } else {
            expenseBtn.setDisable(false);
            Double totalBudget = 0.00;

            for (Budget budget : variables.getBudgets()) {
                Double budgetExpenses = 0.00;
                for (Expense expense : budget.getExpenses()) {
                    budgetExpenses += expense.getPrice();
                }
                totalBudget += (budget.getAmount() - budgetExpenses);
            }

            String budgetText = String.format("%.2f", totalBudget);
            budget.setText(budgetText + " " + currency.getSymbol());
        }

        settingsIcon.setOnMouseClicked(e -> {
            try {
                changeWindowToSettings();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    public void changeWindowToBudget(ActionEvent event) throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("budget-view.fxml"));
        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);

        BudgetController budgetController = fxmloader.getController();
        budgetController.setVariables(variables);

    }

    public void changeWindowToIncome(ActionEvent event) throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("income-view.fxml"));
        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);

        IncomeController incomeController = fxmloader.getController();
        incomeController.setVariables(salarySingle, variables);
    }

    public void changeWindowToExpense(ActionEvent event) throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("expense-view.fxml"));
        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);

        ExpenseController expenseController = fxmloader.getController();
        expenseController.setVariables(variables);
    }

    public void changeWindowToSettings() throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("settings-view.fxml"));
        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);

        SettingsController settingsController = fxmloader.getController();
        settingsController.setVariables(variables, currency);
    }

}