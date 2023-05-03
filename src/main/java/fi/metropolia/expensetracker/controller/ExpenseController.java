package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.*;
import fi.metropolia.expensetracker.module.Dao.BudgetExpenseDao;
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

public class ExpenseController implements Controller {
    @FXML
    AnchorPane budgetPane;

    @FXML
    private AnchorPane content;
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
    private Label expenseTxt;
    @FXML
    private Label active;
    @FXML
    private Label singleExpense;

    @FXML
    private Label history;

    @FXML
    private Button ConstExpenseBtn;
    @FXML
    private ComboBox expenseCombo;
    @FXML
    private Button back;

    private LocalizationManager lan = LocalizationManager.getInstance();
    private Variables variables;
    private Currency currency;
    private BudgetExpenseDao budgetExpenseDao = new BudgetExpenseDao();
    private Budget activeBudget;


    public void initialize() {
        ThemeManager themeManager = ThemeManager.getInstance();
        content.setStyle(themeManager.getStyle());
        expenseCombo.setPromptText(lan.getString("constantExpense"));

        back.setText(lan.getString("back"));
        expenseTxt.setText(lan.getString("expense"));
        active.setText(lan.getString("activeonExpense"));
        singleExpense.setText(lan.getString("single"));
        selectTopic.setPromptText(lan.getString("category"));
        addExpense.setPromptText(lan.getString("amount"));
        addBtn.setText(lan.getString("add"));
        history.setText(lan.getString("history"));

        ConstExpenseBtn.setText(lan.getString("remove"));
    }


    public void backToMain(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(MainApplication.class.getResource("main-view.fxml"));
        content.getChildren().setAll(pane);
    }

    @Override
    public void setVariables(SalarySingle salary, Variables variables) {
        this.variables = variables;
        ThemeManager styler = ThemeManager.getInstance();

        currency = Currency.getInstance(variables.getCurrentCurrency());
        this.activeBudget = variables.getActiveBudget();
        activeBudgetTxt.setText(activeBudget.getName() + ": " + activeBudget.getAmount());
        budgetPane.setVisible(true);
        Double budgetExpenses = 0.00;
        if (variables.getActiveBudget().getExpenses().size() > 0) {
            for (Expense expense : variables.getActiveBudget().getExpenses()) {
                budgetExpenses += expense.getPrice();
            }
        }
        for (ConstantExpense constantExpense : variables.getConstantExpenseArray()) {
            expenseCombo.getItems().add(constantExpense);
        }
        String budgetText = String.format("%.2f", (variables.getActiveBudget().getAmount()) - budgetExpenses);
        this.activeBudgetTxt.setText(activeBudget.getName() + ": " + budgetText + " " + currency.getSymbol());
        selectTopic.getItems().addAll(variables.getCategories());


        expenseHistory.getItems().addAll(activeBudget.getExpenses());
        styler.styleListView(expenseHistory);
        expenseHistory.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                int selectedIndex = expenseHistory.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0) {
                    Expense selected = (Expense) expenseHistory.getItems().get(selectedIndex);
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle(lan.getString("expense"));
                    alert.setHeaderText(lan.getString("areYouSure"));
                    Optional<ButtonType> option = alert.showAndWait();

                    if (option.get() == ButtonType.OK) {
                        expenseHistory.getItems().remove(selected);
                        variables.getActiveBudget().removeExpenseFromBudget(selected);
                        budgetExpenseDao.deleteExpense(selected.getId());
                        expenseHistory.getItems().clear();
                        expenseHistory.getItems().addAll(variables.getActiveBudget().getExpenses());

                        Double budgetExpenses = 0.00;
                        if (variables.getActiveBudget().getExpenses().size() > 0) {
                            for (Expense expense : variables.getActiveBudget().getExpenses()) {
                                budgetExpenses += expense.getPrice();
                            }
                        }
                        String budgetText = String.format("%.2f", (variables.getActiveBudget().getAmount() - budgetExpenses));
                        activeBudgetTxt.setText(activeBudget.getName() + ": " + budgetText + " " + currency.getSymbol());
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle(lan.getString("expense"));
                    alert.setHeaderText(lan.getString("selectedExpense"));
                    alert.setContentText(lan.getString("clickExpense"));
                    alert.showAndWait();
                }
            }
        });
    }

    @FXML
    protected void onExpenseAddClick() {
        String number = addExpense.getText();
        if (number.matches("\\d+") && addExpense != null) {
            if (selectTopic.getSelectionModel().getSelectedItem() != null) {
                LocalDate expenseDate = LocalDate.now();
                if (selectedDate.getValue() != null) {
                    expenseDate = selectedDate.getValue();
                }
                ZoneId defaultZoneId = ZoneId.systemDefault();
                Date finalDate = Date.from(expenseDate.atStartOfDay(defaultZoneId).toInstant());

                ArrayList<String> categories = new ArrayList<>() {{
                    add("Groceries");
                    add("Restaurants");
                    add("Hobbies");
                    add("Clothes");
                    add("Well-being");
                    add("Medicine");
                    add("Transport");
                    add("Other");
                }};

                budgetExpenseDao.saveExpense(variables.getActiveBudget().getId(), categories.get(selectTopic.getSelectionModel().getSelectedIndex()), Double.parseDouble(addExpense.getText()), finalDate);
                variables.getActiveBudget().resetExpenses();
                Expense[] expenses = budgetExpenseDao.getExpenses(variables.getActiveBudget().getId());
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
            activeBudgetTxt.setText(activeBudget.getName() + ": " + budgetText + " " + currency.getSymbol());
            addExpense.setText(null);
            selectTopic.setValue(null);
            selectedDate.setValue(null);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(lan.getString("expense"));
            alert.setHeaderText(lan.getString("addExpense"));
            alert.setContentText(lan.getString("formCorrect"));
            alert.showAndWait();
        }
    }


    @FXML
    protected void btnEnable() {
        if (selectTopic.getSelectionModel().getSelectedItem() != null && addExpense.getText() != null) {
            addBtn.setDisable(false);
        }
    }


    @FXML
    protected void removeBtn() {

        ConstantExpense selectedConstExpense = (ConstantExpense) expenseCombo.getSelectionModel().getSelectedItem();
        budgetExpenseDao.saveExpense(variables.getActiveBudget().getId(), selectedConstExpense.getType(), selectedConstExpense.getAmount(), new Date());
        variables.getActiveBudget().resetExpenses();
        Expense[] expenses = budgetExpenseDao.getExpenses(variables.getActiveBudget().getId());
        for (Expense expense : expenses) {
            variables.getActiveBudget().addExpenseToBudget(expense);
        }
        expenseHistory.getItems().clear();
        expenseHistory.getItems().addAll(variables.getActiveBudget().getExpenses());
        activeBudgetTxt.setText(variables.getActiveBudget().getName() + ": " + variables.getActiveBudget().getAmount() + " " + currency.getSymbol());
        Double budgetExpenses = 0.00;
        if (variables.getActiveBudget().getExpenses().size() > 0) {
            for (Expense expense : variables.getActiveBudget().getExpenses()) {
                budgetExpenses += expense.getPrice();
            }
        }
        String budgetText = String.format("%.2f", (variables.getActiveBudget().getAmount()) - budgetExpenses);
        activeBudgetTxt.setText(activeBudget.getName() + ": " + budgetText + " " + currency.getSymbol());
    }

    @FXML
    protected void btnEnbale() {
        ConstExpenseBtn.setDisable(false);
    }
}