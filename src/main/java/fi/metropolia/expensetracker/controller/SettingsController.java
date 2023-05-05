package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.Dao.SettingsDao;
import fi.metropolia.expensetracker.module.LocalizationManager;
import fi.metropolia.expensetracker.module.SalarySingle;
import fi.metropolia.expensetracker.module.ThemeManager;
import fi.metropolia.expensetracker.module.Variables;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * The SettingsController class is responsible for controlling the view for the user settings,
 * where the user can change various settings including theme color, currency, language and age.
 */
public class SettingsController implements Controller {
    @FXML
    private AnchorPane content;
    @FXML
    private ChoiceBox<String> colorChoiceBox = new ChoiceBox<>();
    @FXML
    private ComboBox selectCurrency;
    @FXML
    private TextField ageField;

    @FXML
    private ComboBox selectLanguage;

    @FXML
    private Label settings;

    @FXML
    private Label bgColor;

    @FXML
    private Label currencyText;

    @FXML
    private Label language;

    @FXML
    private Label setAge;

    @FXML
    private Label onlyFin;

    @FXML
    private Button saveAge;

    @FXML
    private Button deleteData;

    @FXML
    private Button back;

    private Variables variables = Variables.getInstance();

    private Currency currency;

    private LocalizationManager lan = LocalizationManager.getInstance();
    private SettingsDao settingsDao = new SettingsDao();

    /**
     * This method initializes the controller and sets the default values for the ChoiceBox and ComboBox.
     * It also sets the initial style for the content AnchorPane, and maps the color codes to their respective names.
     * Finally, it calls the refresh() method to deal with localization.
     */
    @Override
    public void initialize() {
        ThemeManager themeManager = ThemeManager.getInstance();
        content.setStyle(themeManager.getStyle());
        this.currency = Currency.getInstance(variables.getCurrentCurrency());


        Map<String, String> colorMap = new HashMap<>();
        colorMap.put("#85bb65", "Original");
        colorMap.put("#FBF4E2", "Light");
        colorMap.put("#333333", "Dark");
        colorMap.put("#E35B5B", "Light red");
        colorMap.put("#44ED4A", "Green");
        colorMap.put("#3287E9", "Blue");
        colorMap.put("#edc558", "Yellow");
        colorMap.put("#9b63a4", "Purple");
        colorMap.put("#8B0000", "Gambina");

        colorChoiceBox.getItems().addAll(colorMap.keySet());

        colorChoiceBox.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String hexCode) {
                String exampleName = colorMap.get(hexCode);
                if (exampleName == null) {
                    exampleName = "";
                }
                return exampleName;
            }

            @Override
            public String fromString(String exampleName) {
                return null;
            }

        });

        colorChoiceBox.setOnAction(event -> {
            String selectedHexCode = colorChoiceBox.getValue();
            themeManager.setCurrentColor(selectedHexCode);
            settingsDao.changeUserThemeColor(variables.getLoggedUserId(), selectedHexCode);
            content.setStyle(themeManager.getStyle());

        });

        selectCurrency.getItems().addAll(variables.getCurrencyCodes());

        ArrayList<String> languages = new ArrayList<>();
        languages.add("English");
        languages.add("Finnish");
        languages.add("Icelandic");

        selectLanguage.getItems().addAll(languages);

        refresh();
    }

    /**
     * Adds the age entered by the user to the database and updates the UI.
     *
     * @throws SQLException if there is an error while interacting with the database.
     */
    public void addAgeClick() throws SQLException {
        if (ageField.getText().matches("^[0-9]+$") && ageField != null) {
            SalarySingle.getInstance().setAge(Integer.parseInt(ageField.getText()));
            settingsDao.setAge(variables.getLoggedUserId(), Integer.parseInt(ageField.getText()));
            ageField.setText(null);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(lan.getString("Settings"));
            alert.setHeaderText(lan.getString("formCorrect"));
            alert.showAndWait();
        }
    }

    /**
     * Sets the variables for the controller.
     *
     * @param salary    The SalarySingle instance for the application.
     * @param variables The Variables instance for the application.
     */
    @Override
    public void setVariables(SalarySingle salary, Variables variables) {
        this.variables = variables;
    }

    /**
     * Returns the user to the main view of the application.
     *
     * @throws IOException If an error occurs while loading the main view.
     */
    public void backToMain() throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);
    }

    /**
     * Handles the user clicking the "Choose Currency" button thingy.
     * Sets the current currency multiplier, gets the currency instance, and changes the user's currency in the database.
     */
    @FXML
    protected void onChooseCurrencyBtnClick() {
        variables.setCurrentCourseMultiplier(selectCurrency.getSelectionModel().getSelectedItem().toString());
        currency = Currency.getInstance(variables.getCurrentCurrency());

        settingsDao.changeUserCurrency(variables.getLoggedUserId(), selectCurrency.getSelectionModel().getSelectedItem().toString());
    }

    /**
     * Handles the user selecting a language from the dropdown menu.
     * Changes the language to the selected locale and updates the user's language in the database.
     *
     * @throws NullPointerException If the selected item is null.
     */
    @FXML
    protected void onSelectLanguage() {
        if (selectLanguage.getSelectionModel().getSelectedItem().toString().equals("English")) {
            Locale English = new Locale("en", "GB");
            lan.setLocale(English);
            settingsDao.setLanguage(Variables.getInstance().getLoggedUserId(), "en_GB");
            refresh();
        } else if (selectLanguage.getSelectionModel().getSelectedItem().toString().equals("Finnish")) {
            Locale Finnish = new Locale("fi", "FI");
            lan.setLocale(Finnish);
            settingsDao.setLanguage(Variables.getInstance().getLoggedUserId(), "fi_FI");
            refresh();
        } else if (selectLanguage.getSelectionModel().getSelectedItem().toString().equals("Icelandic")) {
            Locale Icelandic = new Locale("is", "IS");
            lan.setLocale(Icelandic);
            settingsDao.setLanguage(Variables.getInstance().getLoggedUserId(), "is_IS");
            refresh();
        }
    }

    /**
     * Deletes all user data from the database and resets variables to default values.
     * Displays an alert to confirm the user's intention to delete their data,
     * and another notification once the deletion is complete.
     */
    @FXML
    public void deleteData() {
        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDelete.setTitle(lan.getString("settings"));
        confirmDelete.setHeaderText(lan.getString("areYouSure"));

        Optional<ButtonType> result = confirmDelete.showAndWait();
        if (result.get() == ButtonType.OK) {
            settingsDao.deleteUserData(variables.getLoggedUserId());
            variables.resetAndSetDefaults();
            SalarySingle.getInstance().resetAll();

            content.setStyle(ThemeManager.getInstance().getStyle());
            Alert dataDeleted = new Alert(Alert.AlertType.INFORMATION);
            dataDeleted.setTitle(lan.getString("settings"));
            dataDeleted.setHeaderText(lan.getString("dataSuccess"));
            dataDeleted.showAndWait();
        }
    }

    /**
     * Refreshes the language settings of the UI elements on the settings view.
     * Uses the current language settings to retrieve the correct text for each UI element
     * and updates the text displayed on the element accordingly.
     */
    private void refresh() {
        settings.setText(lan.getString("settings"));
        bgColor.setText(lan.getString("bgColor"));
        currencyText.setText(lan.getString("currencyText"));
        language.setText(lan.getString("language"));
        setAge.setText(lan.getString("setAge"));
        onlyFin.setText(lan.getString("onlyFin"));
        saveAge.setText(lan.getString("setBtn"));
        deleteData.setText(lan.getString("deleteAll"));
        back.setText(lan.getString("back"));
    }
}