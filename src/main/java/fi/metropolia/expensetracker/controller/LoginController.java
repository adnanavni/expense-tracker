package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.*;
import fi.metropolia.expensetracker.module.Dao.BudgetExpenseDao;
import fi.metropolia.expensetracker.module.Dao.RegisterLoginDao;
import fi.metropolia.expensetracker.module.Dao.IncomeDao;
import fi.metropolia.expensetracker.module.Dao.SettingsDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class LoginController {
    @FXML
    private AnchorPane content;
    @FXML
    private TextField userName;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button submitButton;

    private RegisterLoginDao dao = new RegisterLoginDao();
    private SettingsDao settingsDao = new SettingsDao();
    private BudgetExpenseDao budgetExpenseDao = new BudgetExpenseDao();
    LocalizationManager localizationManager = LocalizationManager.getInstance();

    public static void infoBox(String infoMessage, String headerText, String title) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText(infoMessage);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }

    private static void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }

    @FXML
    public void login(ActionEvent event) throws SQLException, IOException {

        Window owner = submitButton.getScene().getWindow();

        if (userName.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, localizationManager.getString("formError"),
                    localizationManager.getString("emptyUsername"));
            return;
        }
        if (passwordField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, localizationManager.getString("formError"),
                    localizationManager.getString("emptyPassword"));
            return;
        }

        String name = userName.getText();
        String password = passwordField.getText();

        RegisterLoginDao loginSignupDao = new RegisterLoginDao();
        IncomeDao incomeDao = new IncomeDao();
        boolean flag = loginSignupDao.validate(name, password);

        if (!flag) {
            infoBox(localizationManager.getString("wrongInfo"), null, "Failed");
        } else {
            infoBox(localizationManager.getString("loginSuccesMsg"), null, localizationManager.getString("loginSuccesTitle"));
            Variables.getInstance().setLoggedUserId(loginSignupDao.loggedID(name));
            Variables.getInstance().setLoggedCurrency(budgetExpenseDao.loggedCurrency(Variables.getInstance().getLoggedUserId()));
            ThemeManager.getInstance().setCurrentColor(settingsDao.loggedThemeColor(Variables.getInstance().getLoggedUserId()));

            Budget[] budgets = budgetExpenseDao.getBudgets(Variables.getInstance().getLoggedUserId());
            if (budgets.length > 0) {
                for (Budget budget : budgets) {
                    Variables.getInstance().createNewBudget(budget);
                }

                for (Budget budget : Variables.getInstance().getBudgets()) {
                    Expense[] budgetExpenses = budgetExpenseDao.getExpenses(budget.getId());
                    for (Expense expense : budgetExpenses) {
                        budget.addExpenseToBudget(expense);
                    }

                }
                Variables.getInstance().setActiveBudget(Variables.getInstance().getBudgets().get(0).getName());
            }

            ConstantExpense[] constantExpenses = budgetExpenseDao.getConstantExpenses(Variables.getInstance().getLoggedUserId());
            if (constantExpenses.length == 0) {
                ArrayList<String> defaultConstExpenseNames = Variables.getInstance().getConstExpenses();
                for (String defaultConstExpenseName : defaultConstExpenseNames) {
                    budgetExpenseDao.saveConstantExpense(Variables.getInstance().getLoggedUserId(), defaultConstExpenseName, 0.00);
                }
                ConstantExpense[] defaultConstExpenses = budgetExpenseDao.getConstantExpenses(Variables.getInstance().getLoggedUserId());
                for (ConstantExpense constantExpense : defaultConstExpenses) {
                    Variables.getInstance().addConstantExpense(constantExpense);
                }
            } else {
                for (ConstantExpense constantExpense : constantExpenses) {
                    Variables.getInstance().addConstantExpense(constantExpense);
                }
            }
            ArrayList<Salary> monthSalaries = incomeDao.getSalariesWithType(Variables.getInstance().getLoggedUserId(), "MONTH");
            for (Salary salary : monthSalaries) {
                SalarySingle.getInstance().createNewMonthSalary(salary);
            }
            ArrayList<Salary> daySalaries = incomeDao.getSalariesWithType(Variables.getInstance().getLoggedUserId(), "DAY");
            for (Salary salary : daySalaries) {
                SalarySingle.getInstance().createNewDaySalary(salary);
            }
            SalarySingle.getInstance().setAge(settingsDao.getAge(Variables.getInstance().getLoggedUserId()));
            changeWindowToHome();
        }
    }

    public void changeWindowToHome() throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);
    }

    public void changeWindowToRegistration() throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("registration_form-view.fxml"));
        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);
    }
}