package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.Dao.Dao;
import fi.metropolia.expensetracker.module.SalarySingle;
import fi.metropolia.expensetracker.module.ThemeManager;
import fi.metropolia.expensetracker.module.Variables;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SettingsController {
    @FXML
    private AnchorPane content;
    @FXML
    private ChoiceBox<String> colorChoiceBox = new ChoiceBox<>();
    @FXML
    private ComboBox selectCurrency;
    @FXML
    private TextField ageField;
    private Variables variables = Variables.getInstance();
    private Currency currency;


    public void initialize() {
        ThemeManager themeManager = ThemeManager.getInstance();
        content.setStyle(themeManager.getStyle());

        Map<String, String> colorMap = new HashMap<>();
        colorMap.put("#85bb65", "Default");
        colorMap.put("#FBF4E2", "Light Theme");
        colorMap.put("#333333", "Dark Theme");
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
                    exampleName = "Choose a color";
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
            Dao loginSignupDao = new Dao();
            loginSignupDao.changeUserThemeColor(variables.getLoggedUserId(), selectedHexCode);
            content.setStyle(themeManager.getStyle());

        });

        selectCurrency.getItems().addAll(variables.getCurrencyCodes());
    }

    public void addAgeClick() {
        SalarySingle.getInstance().setAge(Integer.parseInt(ageField.getText()));
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

        Dao loginSignupDao = new Dao();
        loginSignupDao.changeUserCurrency(variables.getLoggedUserId(), selectCurrency.getSelectionModel().getSelectedItem().toString());
    }

    @FXML
    public void deleteData() {
        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDelete.setTitle("Confirm Delete");
        confirmDelete.setHeaderText("This action resets all data from the user. Are you sure you want to delete all data?");

        Optional<ButtonType> result = confirmDelete.showAndWait();
        if (result.get() == ButtonType.OK) {
            Dao dao = new Dao();
            dao.deleteUserData(variables.getLoggedUserId());
            variables.resetAndSetDefaults();
            content.setStyle(ThemeManager.getInstance().getStyle());

            Alert dataDeleted = new Alert(Alert.AlertType.INFORMATION);
            dataDeleted.setTitle("Data Deleted");
            dataDeleted.setHeaderText(null);
            dataDeleted.setContentText("All data has been deleted successfully.");
            dataDeleted.showAndWait();
        }
    }
}