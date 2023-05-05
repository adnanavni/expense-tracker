package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.Dao.RegisterLoginDao;
import fi.metropolia.expensetracker.module.Dao.SettingsDao;
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
import java.util.Locale;

/**
 * This class represents the controller for the registration view of the application.
 * It handles user input and interacts with the DAO to create a new user account.
 * It also provides methods to switch to the login view or the main application view.
 */
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
    /**
     * The hashed version of the user's password.
     */
    private String hashedPassword;
    /**
     * The user's password in plain text.
     */
    private String passwd;
    /**
     * The password authentication object used to hash the user's password.
     */
    private PsswdAuth auth;
    /**
     * The data access object used to interact with the database.
     */
    private RegisterLoginDao loginSignupDao;

    /**
     * Displays an alert dialog box with the specified type, owner, title, and message.
     *
     * @param alertType the type of alert to display (error, warning, information, confirmation)
     * @param owner the window that the alert should be displayed in front of
     * @param title the title of the alert
     * @param message the message to display in the alert
     */
    private static void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }

    /**
     Handles the registration of a new user when the submit button is clicked.
     Checks the validity of the input fields, creates a new user account in the database,
     and navigates to the home view upon successful registration.
     @param event the event triggered by clicking the submit button
     @throws SQLException if there is an error executing the SQL statement
     @throws IOException if there is an error reading the FXML file for the home view
     */
    @FXML
    public void register(ActionEvent event) throws SQLException, IOException{
        auth = new PsswdAuth();
        Window owner = submitButton.getScene().getWindow();
        loginSignupDao = new RegisterLoginDao();

        if (userName.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, "formError",
                    "Please enter your username");
            return;
        }
        if (passwordField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, "formError",
                    "Please enter a password");
            return;
        }
        if (secondPasswordField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, "formError",
                    "Please enter same password again");
            return;
        }
        if (!passwordField.getText().equals(secondPasswordField.getText())) {
            showAlert(Alert.AlertType.ERROR, owner, "formError",
                    "Passwords are not the same");
            return;
        }
        if (loginSignupDao.userExists(userName.getText())) {
            showAlert(Alert.AlertType.ERROR, owner, "Username already in use!",
                    "Please enter a new username");
        }
        if (!loginSignupDao.userExists(userName.getText())) {
            String name = userName.getText();
            passwd = passwordField.getText();
            hashedPassword = auth.hash(passwordField.getText().toCharArray());

            createUser(name);

            showAlert(Alert.AlertType.CONFIRMATION, owner, "Registration successful!",
                    "Welcome" + " " + userName.getText());

            changeWindowToHome();
        }
    }

    /**
     * Creates a new user account with the specified username and password.
     * Inserts the new user's data into the database and sets the logged-in user ID.
     * Also sets the application's default settings and locale.
     *
     * @param name the username of the new user
     */
    private void createUser(String name) {
        RegisterLoginDao loginSignupDao = new RegisterLoginDao();
        loginSignupDao.insertRecord(name, hashedPassword);
        Variables.getInstance().setLoggedUserId(loginSignupDao.loggedID(name));

        Variables.getInstance().resetAndSetDefaults();
        SettingsDao settingsDao = new SettingsDao();
        settingsDao.setLanguage(loginSignupDao.loggedID(name), "en_GB");

        LocalizationManager.getInstance().setLocale(new Locale("en_GB"));

    }

    /**
     * Changes window to main-view / "home-view"
     * */
    private void changeWindowToHome() throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);
    }

    /**
     * Change window to login-view if the user has clicked that she/he has already the user.
     * */
    public void changeWindowToLogin() throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("login_form-view.fxml"));
        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);
    }
}