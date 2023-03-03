package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.Login_Signup_Dao;
import fi.metropolia.expensetracker.module.ThemeManager;
import fi.metropolia.expensetracker.module.Variables;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

public class SettingsController {

    @FXML
    private AnchorPane content;

    private Variables variables = Variables.getInstance();
    private Currency currency;
    @FXML
    private ChoiceBox<String> colorChoiceBox = new ChoiceBox<>();

    @FXML
    private ComboBox selectCurrency;


    public void initialize() {
        ThemeManager themeManager = ThemeManager.getInstance();
        content.setStyle(themeManager.getStyle());

        Map<String, String> colorMap = new HashMap<>();
        colorMap.put("#85bb65", "Default");
        colorMap.put("#FBF4E2", "Light Theme");
        colorMap.put("#333333", "Dark Theme");
        colorMap.put("#E35B5B", "Light red");
        colorMap.put("#44ED4A", "Green");
        colorMap.put("#3287E9", "Light Blue");

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
            Login_Signup_Dao loginSignupDao = new Login_Signup_Dao();
            loginSignupDao.changeUserThemeColor(variables.getLoggedUserId(), selectedHexCode);
            content.setStyle(themeManager.getStyle());

        });

        selectCurrency.getItems().addAll(variables.getCurrencyCodes());
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

        Login_Signup_Dao loginSignupDao = new Login_Signup_Dao();
        loginSignupDao.changeUserCurrency(variables.getLoggedUserId(), selectCurrency.getSelectionModel().getSelectedItem().toString());
    }
}