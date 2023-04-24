package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.*;
import fi.metropolia.expensetracker.module.Dao.BudgetExpenseDao;
import fi.metropolia.expensetracker.module.Dao.RegisterLoginDao;
import fi.metropolia.expensetracker.module.Dao.SettingsDao;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
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
    private Label activeBudget;
    @FXML
    private TextField addBudget;
    @FXML
    private TextField budgetName;
    @FXML
    private ComboBox selectTopic;
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
    private Button ConstExpenseBtn;
    @FXML
    private AnchorPane modifyBudget;
    @FXML
    private Button modify;
    @FXML
    private Button delete;
    @FXML
    private Button shared;

    @FXML
    private BarChart<String, Double> barStats;
    @FXML
    private PieChart pieStats;
    @FXML
    private ComboBox selectedTimeFrame;
    @FXML
    private Label show;


    private Boolean barChartShown = true;
    private Variables variables;
    private Currency currency;
    private LocalizationManager language = LocalizationManager.getInstance();
    private BudgetExpenseDao budgetExpenseDao = new BudgetExpenseDao();
    private SettingsDao settingsDao = new SettingsDao();
    private ObservableList<PieChart.Data> currentPieValues;

    public void initialize() {
        ThemeManager themeManager = ThemeManager.getInstance();
        content.setStyle(themeManager.getStyle());

        back.setText(language.getString("back"));
        total.setText(language.getString("total"));
        active.setText(language.getString("active"));
        selectTopic.setPromptText(language.getString("budget"));
        activeBudget.setText(language.getString("noActive"));

        budgetName.setPromptText(language.getString("name"));
        addBudget.setPromptText(language.getString("amount"));
        addBtn.setText(language.getString("add"));

        modifyName.setPromptText(language.getString("name"));
        modifyAmount.setPromptText(language.getString("amount"));
        modifyBtn.setText(language.getString("modify"));
        deleteBtn.setText(language.getString("delete"));

        expenseCombo.setPromptText(language.getString("constantExpense"));
        ConstExpenseBtn.setText(language.getString("remove"));

        modify.setText(language.getString("modify"));
        delete.setText(language.getString("delete"));

        shared.setText(language.getString("shared"));


    }

    public void setVariables(Variables variables) {

        this.variables = variables;
        currency = Currency.getInstance(variables.getCurrentCurrency());

        total.setText(language.getString("total"));
        selectTopic.getItems().addAll(variables.getBudgetNames());

        if (variables.getActiveBudget() != null) {
            selectTopic.setValue(variables.getActiveBudget().getName());
            modifyBudget.setVisible(true);
            budgetPane.setVisible(true);
        }

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
                            alert.setTitle(language.getString("budget"));
                            alert.setHeaderText(language.getString("budgetName"));
                            alert.setContentText(language.getString("anotherName"));
                            alert.showAndWait();
                        }
                    }
                    if (willAdd) {
                        RegisterLoginDao loginSignupDao = new RegisterLoginDao();
                        budgetExpenseDao.saveBudget(variables.getLoggedUserId(), budgetName.getText(), Double.parseDouble(addBudget.getText()));
                        variables.resetBudgets();
                        Budget[] budgets = budgetExpenseDao.getBudgets(variables.getLoggedUserId());
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

            selectTopic.setValue(variables.getActiveBudget().getName());
            addBudget.setText(null);
            budgetName.setText(null);
            newBudget.setVisible(false);
            editBudget.setVisible(false);
            budgetPane.setVisible(true);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(language.getString("budget"));
            alert.setHeaderText(language.getString("addBudget"));
            alert.setContentText(language.getString("formCorrect"));
            alert.showAndWait();
        }
    }

    @FXML
    protected void onSelectTopic() {
        if (selectTopic.getValue() == "New") {
            modifyBudget.setVisible(false);
            editBudget.setVisible(false);
            newBudget.setVisible(true);
        } else if (selectTopic.getValue() != null) {
            newBudget.setVisible(false);
            modifyBudget.setVisible(true);
            editBudget.setVisible(false);
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
    protected void modifyBudget() {
        modifyBudget.setVisible(false);
        editBudget.setVisible(true);
    }

    @FXML
    protected void removeBtn() {
        ConstantExpense selectedConstExpense = (ConstantExpense) expenseCombo.getSelectionModel().getSelectedItem();
        RegisterLoginDao loginSignupDao = new RegisterLoginDao();
        budgetExpenseDao.saveExpense(variables.getActiveBudget().getId(), selectedConstExpense.getType(), selectedConstExpense.getAmount(), new Date());
        variables.getActiveBudget().resetExpenses();
        Expense[] expenses = budgetExpenseDao.getExpenses(variables.getActiveBudget().getId());
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
    }

    @FXML
    protected void modifyBtnClick() {
        String text = modifyName.getText();
        String number = modifyAmount.getText();

        if (text.matches("[a-zA-Z]+") && number.matches("^[0-9]+$")) {
            RegisterLoginDao loginSignupDao = new RegisterLoginDao();
            if (modifyName.getText() != null && modifyAmount.getText() != null) {
                budgetExpenseDao.ModifyBudget(variables.getActiveBudget().getName(), Double.parseDouble(modifyAmount.getText()), modifyName.getText());

                variables.modifyBudget(modifyName.getText(), Double.parseDouble(modifyAmount.getText()));
                variables.setActiveBudget(modifyName.getText());

                update();
                selectTopic.getItems().setAll(variables.getBudgetNames());

                editBudget.setVisible(false);
            } else if (modifyName.getText() != null && modifyAmount.getText() == null) {

                budgetExpenseDao.ModifyBudget(variables.getActiveBudget().getName(), variables.getActiveBudget().getAmount(), modifyName.getText());

                variables.modifyBudget(modifyName.getText(), variables.getActiveBudget().getAmount());
                variables.setActiveBudget(modifyName.getText());

                update();
                selectTopic.getItems().setAll(variables.getBudgetNames());

                editBudget.setVisible(false);
            } else if (modifyName.getText() == null && modifyAmount.getText() != null) {
                budgetExpenseDao.ModifyBudget(variables.getActiveBudget().getName(), Double.parseDouble(modifyAmount.getText()), variables.getActiveBudget().getName());

                variables.modifyBudget(variables.getActiveBudget().getName(), Double.parseDouble(modifyAmount.getText()));
                variables.setActiveBudget(variables.getActiveBudget().getName());

                update();
                selectTopic.getItems().setAll(variables.getBudgetNames());

                editBudget.setVisible(false);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(language.getString("budget"));
            alert.setHeaderText(language.getString("modifyBudget"));
            alert.setContentText(language.getString("formCorrect"));
            alert.showAndWait();
        }


        modifyAmount.setText(null);
        modifyName.setText(null);
    }

    @FXML
    protected void deleteBtnClick() {

        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDelete.setTitle(language.getString("budget"));
        confirmDelete.setHeaderText(language.getString("areYouSure"));

        Optional<ButtonType> result = confirmDelete.showAndWait();

        if (result.get() == ButtonType.OK) {
            RegisterLoginDao loginSignupDao = new RegisterLoginDao();

            budgetExpenseDao.deleteBudget(variables.getActiveBudget().getId());
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
    }

    @FXML
    protected void onChangeChartClick() {
        if (barChartShown) {
            pieStats.setVisible(true);
            barStats.setVisible(false);
            barChartShown = false;
        } else {
            pieStats.setVisible(false);
            barStats.setVisible(true);
            barChartShown = true;
        }
    }

    @FXML
    protected void btnEnbale() {
        ConstExpenseBtn.setDisable(false);
    }
}