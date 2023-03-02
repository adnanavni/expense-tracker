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

    private String hashedPassword;
    private String passwd;

    private PsswdAuth auth;
    private Login_Signup_Dao loginSignupDao;

    @FXML
    public void register(ActionEvent event) throws SQLException, IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        auth = new PsswdAuth();
        Window owner = submitButton.getScene().getWindow();
        loginSignupDao = new Login_Signup_Dao();


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
        if (loginSignupDao.userExists(userName.getText())){
            showAlert(Alert.AlertType.ERROR, owner, "User already exists",
                    "Please enter a new username");
        }
        if (!loginSignupDao.userExists(userName.getText())) {
            String name = userName.getText();
            // String password = passwordField.getText();
            passwd = passwordField.getText();
            hashedPassword = auth.hash(passwordField.getText().toCharArray());
            //   token = auth.hash(passwordField.getText());
            System.out.println("REGISTERCONTROLLER token" + hashedPassword);

            Login_Signup_Dao loginSignupDao = new Login_Signup_Dao();
            loginSignupDao.insertRecord(name, hashedPassword);
            Variables.getInstance().setLoggedUserId(loginSignupDao.loggedID(name));

            showAlert(Alert.AlertType.CONFIRMATION, owner, "Registration Successful!",
                    "Welcome " + userName.getText());

            changeWindowToHome();
        }




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