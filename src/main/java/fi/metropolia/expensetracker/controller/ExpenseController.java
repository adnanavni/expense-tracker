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
import java.util.ArrayList;
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

        Double budgetExpenses = 0.00;
        if (variables.getActiveBudget().getExpenses().size() > 0) {
            for (Expense expense : variables.getActiveBudget().getExpenses()) {
                budgetExpenses += expense.getPrice();
            }
        }
        String budgetText = String.format("%.2f", (variables.getActiveBudget().getAmount()) - budgetExpenses);
        this.expense.setText(budgetText + " " + currency.getSymbol());
        selectTopic.getItems().addAll(variables.getTopics());

        for (ConstantExpense constantExpense : variables.getConstantExpenseArray()) {
            selectCategory.getItems().add(constantExpense);
        }
        selectCategory.getItems().add(0, "New");
        expenseHistory.getItems().addAll(activeBudget.getExpenses());
        expenseHistory.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                int selectedIndex = expenseHistory.getSelectionModel().getSelectedIndex();
                Dao loginSignupDao = new Dao();
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
                        if (variables.getActiveBudget().getExpenses().size() > 0) {
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
            Dao loginSignupDao = new Dao();
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
        if (variables.getActiveBudget().getExpenses().size() > 0) {
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
        System.out.println(selectCategory.getValue());
        if (selectCategory.getValue().equals("New")) {
            if(constExpense.getText() == null){
                if(variables.constantExpenseNameExists(constExpenseName.getText())){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Name already exists!");
                    alert.setHeaderText("You already have a Constant expense with that name!");
                    alert.setContentText("Give a name that doesn't already exist.");
                    alert.showAndWait();
                    selectCategory.setValue(null);
                    constExpense.setText(null);
                    constExpenseName.setText(null);
                }
                else {
                    Dao loginSignupDao = new Dao();
                    loginSignupDao.saveConstantExpense(Variables.getInstance().getLoggedUserId(), constExpenseName.getText(), 0.00);
                    ConstantExpense newConstantExpense = loginSignupDao.getConstantExpenseByName(constExpenseName.getText(), variables.getLoggedUserId());
                    variables.addConstantExpense(newConstantExpense);
                    selectCategory.setValue(null);
                    constExpense.setText(null);
                    constExpenseName.setText(null);
                    selectCategory.getItems().clear();
                    selectCategory.getItems().add(0, "New");
                    for (ConstantExpense constantExpense : variables.getConstantExpenseArray()) {
                        selectCategory.getItems().add(constantExpense);
                    }
                }

            }
            else {

                if(variables.constantExpenseNameExists(constExpenseName.getText())){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Name already exists!");
                    alert.setHeaderText("You already have a Constant expense with that name!");
                    alert.setContentText("Give a name that doesn't already exist.");
                    alert.showAndWait();
                    selectCategory.setValue(null);
                    constExpense.setText(null);
                    constExpenseName.setText(null);
                }
                else {
                    Dao loginSignupDao = new Dao();
                    loginSignupDao.saveConstantExpense(Variables.getInstance().getLoggedUserId(), constExpenseName.getText(),
                            Double.parseDouble(constExpense.getText()));
                    ConstantExpense newConstantExpense = loginSignupDao.getConstantExpenseByName(constExpenseName.getText(), variables.getLoggedUserId());
                    variables.addConstantExpense(newConstantExpense);
                    selectCategory.setValue(null);
                    constExpense.setText(null);
                    constExpenseName.setText(null);
                    selectCategory.getItems().clear();
                    selectCategory.getItems().add(0, "New");
                    for (ConstantExpense constantExpense : variables.getConstantExpenseArray()) {
                        selectCategory.getItems().add(constantExpense);
                    }
                }
            }

        }
        else {
            System.out.println("NOT NEW, MODIFYING EXISTING...");
            ConstantExpense selectedConstExpense = (ConstantExpense) selectCategory.getSelectionModel().getSelectedItem();
            variables.removeConstantExpense(selectedConstExpense);
            Dao loginSignupDao = new Dao();
            loginSignupDao.changeConstantExpenseValue(selectedConstExpense.getId(), Double.parseDouble(constExpense.getText()));
            ConstantExpense modifiedConstExp = loginSignupDao.getConstantExpenseByName(selectedConstExpense.getType(), variables.getLoggedUserId());
            variables.addConstantExpense(modifiedConstExp);
            variables.setConstExpenses(selectedConstExpense.getType(), Double.parseDouble(constExpense.getText()));
            selectCategory.setValue(null);
            constExpense.setText(null);
            constExpenseName.setText(null);
            selectCategory.getItems().clear();
            selectCategory.getItems().add(0, "New");
            for (ConstantExpense constantExpense : variables.getConstantExpenseArray()) {
                selectCategory.getItems().add(constantExpense);
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
