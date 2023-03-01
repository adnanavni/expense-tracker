package fi.metropolia.expensetracker.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.Login_Signup_Dao;
import fi.metropolia.expensetracker.module.PsswdAuth;
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

public class LoginController {

    @FXML
    private AnchorPane content;
    @FXML
    private TextField userName;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button submitButton;
    private String token;
    PsswdAuth auth;

    @FXML
    public void login(ActionEvent event) throws IOException, SQLException {
        auth = new PsswdAuth();
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

        Login_Signup_Dao loginSignupDao = new Login_Signup_Dao();
      //  token = auth.hash(passwordField.getText().toCharArray());

        boolean isUser = loginSignupDao.validate(name, password);
        if (!isUser) {
            infoBox("Please enter correct Username and Password or Signup", null, "Failed");
        } if(isUser){
            infoBox("Login Successful!", null, "Successful");
            Variables.getInstance().setLoggedUserId(loginSignupDao.loggedID(name));
            Variables.getInstance().setCurrentCourseMultiplier(loginSignupDao.loggedCurrency(Variables.getInstance().getLoggedUserId()));

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