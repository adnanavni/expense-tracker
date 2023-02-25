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
    SalarySingle salarySingle = SalarySingle.getInstance();
    private LocalDate date;
  //  private Salary salary = new Salary(0.0, date.now(), Currency.getInstance(variables.getCurrentCurrency()).toString());
    private Currency currency = Currency.getInstance(variables.getCurrentCurrency());

    @FXML
    public void initialize() {
        ThemeManager themeManager = ThemeManager.getInstance();
        content.setStyle(themeManager.getStyle());

        selectCurrency.getItems().addAll(variables.getCurrencyCodes());

        if (variables.getBudgets().size() < 1) {
            budget.setText("No budgets yet");
            expenseBtn.setDisable(true);
            selectCurrency.setDisable(true);
        } else {
            expenseBtn.setDisable(false);
            selectCurrency.setDisable(false);
            Double totalBudget = 0.00;
            for (Integer i = 0; i < variables.getBudgets().size(); i++) {
                totalBudget += variables.getBudgets().get(i).getAmount();
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

      //  IncomeController incomeController = fxmloader.getController();
        //incomeController.setVariables(salarySingle, variables);
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
    }

    @FXML
    protected void onChooseCurrencyBtnClick() {

        variables.setCurrentCourseMultiplier(selectCurrency.getSelectionModel().getSelectedItem().toString());
        currency = Currency.getInstance(variables.getCurrentCurrency());
        variables.convertConstExpense();

        Double totalBudget = 0.00;
        for (Integer i = 0; i < variables.getBudgets().size(); i++) {
            totalBudget += variables.getBudgets().get(i).getAmount();
        }
        String budgetText = String.format("%.2f", totalBudget);
        budget.setText(budgetText + " " + currency.getSymbol());
    }
}