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

public class SettingsController {
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

    private LocalizationManager localizationManager = LocalizationManager.getInstance();
    private SettingsDao settingsDao = new SettingsDao();

    public void initialize() {
        ThemeManager themeManager = ThemeManager.getInstance();
        content.setStyle(themeManager.getStyle());

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

    public void addAgeClick() throws SQLException {
        SalarySingle.getInstance().setAge(Integer.parseInt(ageField.getText()));
        settingsDao.setAge(variables.getLoggedUserId(), Integer.parseInt(ageField.getText()));
        ageField.setText(null);
    }

    public void setVariables(Variables variables, Currency currency) {
        this.variables = variables;
        this.currency = currency;
    }

    public void backToMain() throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);
    }

    @FXML
    protected void onChooseCurrencyBtnClick() {
        variables.setCurrentCourseMultiplier(selectCurrency.getSelectionModel().getSelectedItem().toString());
        currency = Currency.getInstance(variables.getCurrentCurrency());

        settingsDao.changeUserCurrency(variables.getLoggedUserId(), selectCurrency.getSelectionModel().getSelectedItem().toString());
    }

    @FXML
    protected void onSelectLanguage() {
        if (selectLanguage.getSelectionModel().getSelectedItem().toString().equals("English")) {
            Locale English = new Locale("en", "GB");
            Locale.setDefault(English);
            localizationManager.setLocale(English);
            settingsDao.setLanguage(Variables.getInstance().getLoggedUserId(), "English");
            refresh();
        } else if (selectLanguage.getSelectionModel().getSelectedItem().toString().equals("Finnish")) {
            Locale Finnish = new Locale("fi", "FI");
            Locale.setDefault(Finnish);
            localizationManager.setLocale(Finnish);
            settingsDao.setLanguage(Variables.getInstance().getLoggedUserId(), "Finnish");

            refresh();
        } else if (selectLanguage.getSelectionModel().getSelectedItem().toString().equals("Icelandic")) {
            Locale Icelandic = new Locale("is", "IS");
            Locale.setDefault(Icelandic);
            localizationManager.setLocale(Icelandic);
            settingsDao.setLanguage(Variables.getInstance().getLoggedUserId(), "Icelandic");
            refresh();
        }
    }

    @FXML
    public void deleteData() {
        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDelete.setTitle("Confirm Delete");
        confirmDelete.setHeaderText("This action resets all data from the user. Are you sure you want to delete all data?");

        Optional<ButtonType> result = confirmDelete.showAndWait();
        if (result.get() == ButtonType.OK) {
            settingsDao.deleteUserData(variables.getLoggedUserId());
            variables.resetAndSetDefaults();
            content.setStyle(ThemeManager.getInstance().getStyle());

            Alert dataDeleted = new Alert(Alert.AlertType.INFORMATION);
            dataDeleted.setTitle("Data Deleted");
            dataDeleted.setHeaderText(null);
            dataDeleted.setContentText("All data has been deleted successfully.");
            dataDeleted.showAndWait();
        }
    }

    private void refresh() {
        settings.setText(localizationManager.getString("settings"));
        bgColor.setText(localizationManager.getString("bgColor"));
        currencyText.setText(localizationManager.getString("currencyText"));
        language.setText(localizationManager.getString("language"));
        setAge.setText(localizationManager.getString("setAge"));
        onlyFin.setText(localizationManager.getString("onlyFin"));
        saveAge.setText(localizationManager.getString("setBtn"));
        deleteData.setText(localizationManager.getString("deleteAll"));
        back.setText(localizationManager.getString("back"));
    }
}