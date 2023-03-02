package fi.metropolia.expensetracker.controller;

import java.io.IOException;
import java.sql.SQLException;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.Login_Signup_Dao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;

public class RegisterController {

    @FXML
    private AnchorPane content;
    @FXML
    private TextField userName;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button submitButton;

    @FXML
    public void register(ActionEvent event) throws SQLException, IOException {

        Window owner = submitButton.getScene().getWindow();


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

        Login_Signup_Dao loginSignupDao = new Login_Signup_Dao();
        loginSignupDao.insertRecord(name, password);

        showAlert(Alert.AlertType.CONFIRMATION, owner, "Registration Successful!",
                "Welcome " + userName.getText());
        changeWindowToHome();
    }

    private void changeWindowToHome() throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);
    }

    public void changeWindowToLogin() throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("login_form-view.fxml"));
        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);
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