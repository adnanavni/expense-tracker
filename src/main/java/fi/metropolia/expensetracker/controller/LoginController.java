package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.Budget;
import fi.metropolia.expensetracker.module.Dao;
import fi.metropolia.expensetracker.module.Expense;
import fi.metropolia.expensetracker.module.Variables;
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

public class LoginController {

    @FXML
    private AnchorPane content;
    @FXML
    private TextField userName;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button submitButton;

    @FXML
    public void login(ActionEvent event) throws SQLException, IOException {

        Window owner = submitButton.getScene().getWindow();

        System.out.println(userName.getText());
        System.out.println(passwordField.getText());

        if (userName.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!",
                    "Please enter your username");
            return;
        }
        if (passwordField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!",
                    "Please enter a password");
            return;
        }

        String name = userName.getText();
        String password = passwordField.getText();

        Dao loginSignupDao = new Dao();
        boolean flag = loginSignupDao.validate(name, password);

        if (!flag) {
            infoBox("Please enter correct username and password or create a new account!", null, "Failed");
        } else {
            infoBox("Login Successful!", null, "Successful");
            changeWindowToHome();

            Variables.getInstance().setLoggedUserId(loginSignupDao.loggedID(name));
            Variables.getInstance().setCurrentCourseMultiplier(loginSignupDao.loggedCurrency(Variables.getInstance().getLoggedUserId()));
            Budget[] budgets = loginSignupDao.getBudgets(Variables.getInstance().getLoggedUserId());
            if (budgets.length > 0) {
                for (Budget budget : budgets) {
                    Variables.getInstance().createNewBudget(budget);
                }

                for (Budget budget : Variables.getInstance().getBudgets()) {
                    Expense[] budgetExpenses = loginSignupDao.getExpenses(budget.getId());
                    for (Expense expense : budgetExpenses) {
                        budget.addExpenseToBudget(expense);
                    }

                }
                Variables.getInstance().setActiveBudget(Variables.getInstance().getBudgets().get(0).getName());
            }

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
}