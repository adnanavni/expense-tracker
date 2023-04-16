package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.*;
import fi.metropolia.expensetracker.module.Dao.Dao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Currency;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

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
    private Label specificBudget;

    @FXML
    private AnchorPane budgetPane;

    @FXML
    private ComboBox expenseCombo;
    @FXML
    private AnchorPane newBudget;
    @FXML
    private AnchorPane editBudget;
    @FXML
    private TextField modifyName;
    @FXML
    private TextField modifyAmount;

    @FXML
    private Label total;

    @FXML
    private Label active;

    @FXML
    private Button deleteBtn;

    @FXML
    private Button modifyBtn;

    @FXML
    private Button addBtn;

    @FXML
    private Button back;

    @FXML
    Button ConstExpenseBtn;

    private Variables variables;
    private Currency currency;

    private LocalizationManager language = LocalizationManager.getInstance();

    public void initialize() {
        ThemeManager themeManager = ThemeManager.getInstance();
        content.setStyle(themeManager.getStyle());

        back.setText(language.getString("back"));
        total.setText(language.getString("total"));
        active.setText(language.getString("active"));
        selectTopic.setPromptText(language.getString("budget"));

        budgetName.setPromptText(language.getString("name"));
        addBudget.setPromptText(language.getString("amount"));
        addBtn.setText(language.getString("add"));

        modifyName.setPromptText(language.getString("name"));
        modifyAmount.setPromptText(language.getString("amount"));
        modifyBtn.setText(language.getString("modify"));
        deleteBtn.setText(language.getString("delete"));

        expenseCombo.setPromptText(language.getString("constantExpense"));
        ConstExpenseBtn.setText(language.getString("remove"));

    }

    public void setVariables(Variables variables) {

        this.variables = variables;
        currency = Currency.getInstance(variables.getCurrentCurrency());

        total.setText("Budget");
        selectTopic.getItems().addAll(variables.getBudgetNames());

        for (ConstantExpense constantExpense : variables.getConstantExpenseArray()) {
            expenseCombo.getItems().add(constantExpense);
        }

        if (variables.getActiveBudget() != null) {
            String budgetText = String.format("%.2f", variables.getBudget());
            activeBudget.setText(variables.getActiveBudget().getName());
            total.setText(language.getString("total") + " " + budgetText + " " + currency.getSymbol());
        }
    }

    @FXML
    protected void backToMain(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(MainApplication.class.getResource("main-view.fxml"));
        content.getChildren().setAll(pane);
    }

    @FXML
    protected void addToBudget() {

        String text = budgetName.getText();
        String number = addBudget.getText();

        if (text.matches("[a-zA-Z]+") && number.matches("^[0-9]+$")) {
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
                        Dao loginSignupDao = new Dao();
                        loginSignupDao.saveBudget(variables.getLoggedUserId(), budgetName.getText(), Double.parseDouble(addBudget.getText()));
                        variables.resetBudgets();
                        Budget[] budgets = loginSignupDao.getBudgets(variables.getLoggedUserId());
                        for (Budget budget : budgets) {
                            variables.createNewBudget(budget);
                        }
                        variables.setActiveBudget(budgetName.getText());
                        activeBudget.setText(variables.getActiveBudget().getName());
                        String budgetText = String.format("%.2f", variables.getBudget());
                        total.setText(language.getString("total") + " " + budgetText + " " + currency.getSymbol());

                    }
                }
            }
            selectTopic.getItems().setAll(variables.getBudgetNames());
            specificBudget.setText(variables.getActiveBudget().getName() + " " + variables.getActiveBudget().getAmount() + " " + currency.getSymbol());

            selectTopic.setValue(null);
            addBudget.setText(null);
            budgetName.setText(null);
            newBudget.setVisible(false);
            editBudget.setVisible(false);
            budgetPane.setVisible(true);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Add a budget");
            alert.setHeaderText("You cant add a budget");
            alert.setContentText("Fill the form correctly");
            alert.showAndWait();
        }
    }

    @FXML
    protected void onSelectTopic() {
        if (selectTopic.getValue() == "New") {
            editBudget.setVisible(false);
            newBudget.setVisible(true);
        } else if (selectTopic.getValue() != null) {
            newBudget.setVisible(false);
            editBudget.setVisible(true);
            budgetPane.setVisible(true);

            for (Budget budget : variables.getBudgets()) {
                if (budget.getName() == selectTopic.getValue()) {
                    variables.setActiveBudget(budget.getName());
                    update();
                }
            }
        }
    }

    @FXML
    protected void removeBtn() {
        ConstantExpense selectedConstExpense = (ConstantExpense) expenseCombo.getSelectionModel().getSelectedItem();


        Dao loginSignupDao = new Dao();
        loginSignupDao.saveExpense(variables.getActiveBudget().getId(), selectedConstExpense.getType(), selectedConstExpense.getAmount(), new Date());
        variables.getActiveBudget().resetExpenses();
        Expense[] expenses = loginSignupDao.getExpenses(variables.getActiveBudget().getId());
        for (Expense expense : expenses) {
            variables.getActiveBudget().addExpenseToBudget(expense);
        }
        String budgetText = String.format("%.2f", variables.getBudget());
        total.setText(language.getString("total") + " " + budgetText + " " + currency.getSymbol());
        Double budgetExpenses = 0.00;
        if (variables.getActiveBudget().getExpenses().size() > 0) {
            for (Expense expense : variables.getActiveBudget().getExpenses()) {
                budgetExpenses += expense.getPrice();
            }
        }

        specificBudget.setText(variables.getActiveBudget().getName() + " " + (variables.getActiveBudget().getAmount() - budgetExpenses) + " " + currency.getSymbol());


    }

    @FXML
    protected void modifyBtnClick() {
        String text = modifyName.getText();
        String number = modifyAmount.getText();

        if (text.matches("[a-zA-Z]+") && number.matches("^[0-9]+$")) {
            Dao loginSignupDao = new Dao();
            if (modifyName.getText() != null && modifyAmount.getText() != null) {
                loginSignupDao.ModifyBudget(variables.getActiveBudget().getName(), Double.parseDouble(modifyAmount.getText()), modifyName.getText());

                variables.modifyBudget(modifyName.getText(), Double.parseDouble(modifyAmount.getText()));
                variables.setActiveBudget(modifyName.getText());

                update();
                selectTopic.getItems().setAll(variables.getBudgetNames());

                editBudget.setVisible(false);
            } else if (modifyName.getText() != null && modifyAmount.getText() == null) {

                loginSignupDao.ModifyBudget(variables.getActiveBudget().getName(), variables.getActiveBudget().getAmount(), modifyName.getText());

                variables.modifyBudget(modifyName.getText(), variables.getActiveBudget().getAmount());
                variables.setActiveBudget(modifyName.getText());

                update();
                selectTopic.getItems().setAll(variables.getBudgetNames());

                editBudget.setVisible(false);
            } else if (modifyName.getText() == null && modifyAmount.getText() != null) {
                loginSignupDao.ModifyBudget(variables.getActiveBudget().getName(), Double.parseDouble(modifyAmount.getText()), variables.getActiveBudget().getName());

                variables.modifyBudget(variables.getActiveBudget().getName(), Double.parseDouble(modifyAmount.getText()));
                variables.setActiveBudget(variables.getActiveBudget().getName());

                update();
                selectTopic.getItems().setAll(variables.getBudgetNames());

                editBudget.setVisible(false);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Modify your budget");
            alert.setHeaderText("Your budget is not being modified");
            alert.setContentText("Fill the form correctly");
            alert.showAndWait();
        }


        selectTopic.setValue(null);
        modifyAmount.setText(null);
        modifyName.setText(null);
    }

    @FXML
    protected void deleteBtnClick() {

        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDelete.setTitle("Confirm Delete");
        confirmDelete.setHeaderText("This action deletes your current budget. Are you sure you want to delete your current budget?");

        Optional<ButtonType> result = confirmDelete.showAndWait();

        if (result.get() == ButtonType.OK) {
            Dao loginSignupDao = new Dao();

            loginSignupDao.deleteBudget(variables.getActiveBudget().getId());
            variables.deleteBudget();

            update();
            activeBudget.setText("None");

            selectTopic.getItems().setAll(variables.getBudgetNames());

            selectTopic.setValue(null);
            modifyAmount.setText(null);
            modifyName.setText(null);
            editBudget.setVisible(false);
            budgetPane.setVisible(false);
        }
    }

    private void update() {
        String budgetText = String.format("%.2f", variables.getBudget());
        total.setText(language.getString("total") + " " + budgetText + " " + currency.getSymbol());
        activeBudget.setText(variables.getActiveBudget().getName());
        Double budgetExpenses = 0.00;
        if (variables.getActiveBudget().getExpenses().size() > 0) {
            for (Expense expense : variables.getActiveBudget().getExpenses()) {
                budgetExpenses += expense.getPrice();
            }
        }
        String activeBudgetText = String.format("%.2f", variables.getActiveBudget().getAmount() - budgetExpenses);
        specificBudget.setText(variables.getActiveBudget().getName() + " " + activeBudgetText + " " + currency.getSymbol());
    }
}