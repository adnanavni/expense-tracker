package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.Dao.RegisterLoginDao;
import fi.metropolia.expensetracker.module.LocalizationManager;
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

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

public class RegisterController {
    @FXML
    private AnchorPane content;
    @FXML
    private TextField userName;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField secondPasswordField;
    @FXML
    private Button submitButton;

    private String hashedPassword;
    private String passwd;
    private PsswdAuth auth;
    private RegisterLoginDao loginSignupDao;
    LocalizationManager localizationManager = LocalizationManager.getInstance();

    private static void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }

    @FXML
    public void register(ActionEvent event) throws SQLException, IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        auth = new PsswdAuth();
        Window owner = submitButton.getScene().getWindow();
        loginSignupDao = new RegisterLoginDao();

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
        if (secondPasswordField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, localizationManager.getString("formError"),
                    localizationManager.getString("emptySecondPassword"));
            return;
        }
        if(!passwordField.getText().equals(secondPasswordField.getText()) ) {
            showAlert(Alert.AlertType.ERROR, owner, localizationManager.getString("formError"),
                    localizationManager.getString("passwordNotEqual"));
            return;
        }
        if (loginSignupDao.userExists(userName.getText())) {
            showAlert(Alert.AlertType.ERROR, owner, localizationManager.getString("userExistsTitle"),
                    localizationManager.getString("userExistsMsg"));
        }
        if (!loginSignupDao.userExists(userName.getText())) {
            String name = userName.getText();
            passwd = passwordField.getText();
            hashedPassword = auth.hash(passwordField.getText().toCharArray());

            RegisterLoginDao loginSignupDao = new RegisterLoginDao();
            loginSignupDao.insertRecord(name, hashedPassword);
            Variables.getInstance().setLoggedUserId(loginSignupDao.loggedID(name));

            showAlert(Alert.AlertType.CONFIRMATION, owner, localizationManager.getString("registrationSuccessTitle"),
                    localizationManager.getString("welcome") + userName.getText());

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
}