package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Currency;
import java.util.Random;

public class MainController {
    @FXML
    private Label budget;
    @FXML
    private Button expenseBtn;
    @FXML
    private ImageView settingsIcon;
    @FXML
    private AnchorPane content;

    @FXML
    private Label quote;

    @FXML
    private Label total;

    @FXML
    private Button budgetBtn;

    @FXML
    private Button incomeBtn;

    @FXML
    private Button logOut;

    private int currentIndex = 0;

    private Variables variables = Variables.getInstance();
    private SalarySingle salarySingle = SalarySingle.getInstance();
    private Currency currency = Currency.getInstance(variables.getCurrentCurrency());

    private LocalizationManager localizationManager = LocalizationManager.getInstance();

    @FXML
    public void initialize() {
        ThemeManager themeManager = ThemeManager.getInstance();
        content.setStyle(themeManager.getStyle());

        if (variables.getBudgets().size() < 1) {
            // Change to correct language
            budget.setText(localizationManager.getString("emptyBudget"));

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

        currentIndex = new Random().nextInt(10);

        variables.refreshTips();
        quote.setText(variables.getQuote(currentIndex));

        Timeline textUpdateTimeline = new Timeline(new KeyFrame(Duration.seconds(30), event -> {
            currentIndex = (currentIndex + 1) % 10;
            quote.setText(variables.getQuote(currentIndex));
        }));
        textUpdateTimeline.setCycleCount(Timeline.INDEFINITE);
        textUpdateTimeline.play();

        total.setText(localizationManager.getString("totalB"));
        budgetBtn.setText(localizationManager.getString("budget"));
        expenseBtn.setText(localizationManager.getString("expense"));
        incomeBtn.setText(localizationManager.getString("income"));
        logOut.setText(localizationManager.getString("logOut"));
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

    public void changeWindowToLogin() throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("login_form-view.fxml"));
        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);

        variables.resetAll();
        salarySingle.resetAll();
    }
}