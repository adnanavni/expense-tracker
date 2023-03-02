package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

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

        Double budgetExpenses = 0.00;
        if(variables.getActiveBudget().getExpenses().size() > 0){
            for (Expense expense : variables.getActiveBudget().getExpenses()) {
                budgetExpenses += expense.getPrice();
            }
        }
        String budgetText = String.format("%.2f", (variables.getActiveBudget().getAmount()) - budgetExpenses);
        this.expense.setText(budgetText + " " + currency.getSymbol());
        selectTopic.getItems().addAll(variables.getTopics());

        ArrayList<String> constExpenseNames = variables.getConstExpenses();
        for(Integer i = 0; i < constExpenseNames.size(); i++){
            ConstantExpense expenseToAdd = new ConstantExpense(constExpenseNames.get(i), variables.getConstExpense(constExpenseNames.get(i))
            , currency.getSymbol());
            selectCategory.getItems().add(expenseToAdd);
        }
        selectCategory.getItems().add(0, "New");
        expenseHistory.getItems().addAll(activeBudget.getExpenses());
        expenseHistory.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                int selectedIndex = expenseHistory.getSelectionModel().getSelectedIndex();
                Login_Signup_Dao loginSignupDao = new Login_Signup_Dao();
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
                        loginSignupDao.deleteExpense(selected.getId());
                        expenseHistory.getItems().clear();
                        expenseHistory.getItems().addAll(variables.getActiveBudget().getExpenses());

                        Double budgetExpenses = 0.00;
                        if(variables.getActiveBudget().getExpenses().size() > 0){
                            for (Expense expense : variables.getActiveBudget().getExpenses()) {
                                budgetExpenses += expense.getPrice();
                            }
                        }
                        String budgetText = String.format("%.2f", (variables.getActiveBudget().getAmount() - budgetExpenses));
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


            LocalDate expenseDate = LocalDate.now();
            if (selectedDate.getValue() != null) {
                expenseDate = selectedDate.getValue();
            }
            ZoneId defaultZoneId = ZoneId.systemDefault();
            Date finalDate = Date.from(expenseDate.atStartOfDay(defaultZoneId).toInstant());
            Login_Signup_Dao loginSignupDao = new Login_Signup_Dao();
            loginSignupDao.saveExpense(variables.getActiveBudget().getId(), selectTopic.getSelectionModel().getSelectedItem().toString()
                    , Double.parseDouble(addExpense.getText()), finalDate);
            variables.getActiveBudget().resetExpenses();
            Expense[] expenses = loginSignupDao.getExpenses(variables.getActiveBudget().getId());
            for (Expense expense : expenses) {
                variables.getActiveBudget().addExpenseToBudget(expense);
            }
            expenseHistory.getItems().clear();
            expenseHistory.getItems().addAll(variables.getActiveBudget().getExpenses());
        }
        Double budgetExpenses = 0.00;
        if(variables.getActiveBudget().getExpenses().size() > 0){
            for (Expense expense : variables.getActiveBudget().getExpenses()) {
                budgetExpenses += expense.getPrice();
            }
        }
        String budgetText = String.format("%.2f", (variables.getActiveBudget().getAmount()) - budgetExpenses);
        expense.setText(budgetText + " " + currency.getSymbol());
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
        if (selectCategory.getValue() == "New") {
            if(constExpense.getText() == null){
                variables.setConstExpenses(constExpenseName.getText(), 0.00);
                selectCategory.setValue(null);
                constExpense.setText(null);
                constExpenseName.setText(null);
                selectCategory.getItems().clear();
                selectCategory.getItems().add(0, "New");
                ArrayList<String> constExpenseNames = variables.getConstExpenses();
                for(Integer i = 0; i < constExpenseNames.size(); i++){
                    ConstantExpense expenseToAdd = new ConstantExpense(constExpenseNames.get(i), variables.getConstExpense(constExpenseNames.get(i))
                            , currency.getSymbol());
                    selectCategory.getItems().add(expenseToAdd);
                }
            }
            else {
                variables.setConstExpenses(constExpenseName.getText(), Double.parseDouble(constExpense.getText()));
                selectCategory.setValue(null);
                constExpense.setText(null);
                constExpenseName.setText(null);
                selectCategory.getItems().clear();
                selectCategory.getItems().add(0, "New");
                ArrayList<String> constExpenseNames = variables.getConstExpenses();
                for (String expenseName : constExpenseNames) {
                    ConstantExpense expenseToAdd = new ConstantExpense(expenseName, variables.getConstExpense(expenseName)
                            , currency.getSymbol());
                    selectCategory.getItems().add(expenseToAdd);
                }
            }

        }
        else {
            ConstantExpense selectedConstExpense = (ConstantExpense) selectCategory.getSelectionModel().getSelectedItem();
            variables.setConstExpenses(selectedConstExpense.getType(), Double.parseDouble(constExpense.getText()));
            selectCategory.setValue(null);
            constExpense.setText(null);
            constExpenseName.setText(null);
            selectCategory.getItems().clear();
            selectCategory.getItems().add(0, "New");
            ArrayList<String> constExpenseNames = variables.getConstExpenses();
            for (String expenseName : constExpenseNames) {
                ConstantExpense expenseToAdd = new ConstantExpense(expenseName, variables.getConstExpense(expenseName)
                        , currency.getSymbol());
                selectCategory.getItems().add(expenseToAdd);
            }
        }

    }

    public void toExpenseStatistics(ActionEvent event) throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("expenseStatistics-view.fxml"));

        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);

        ExpenseStatisticsController expenseStatisticsController = fxmloader.getController();
        expenseStatisticsController.setVariables(variables);
    }

    @FXML
    protected void btnEnable() {
        if (selectTopic.getSelectionModel().getSelectedItem() != null && addExpense.getText() != null) {
            addBtn.setDisable(false);
        }
    }
}
