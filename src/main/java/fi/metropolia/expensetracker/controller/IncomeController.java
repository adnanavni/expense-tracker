package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.Dao.IncomeDao;
import fi.metropolia.expensetracker.module.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;


/**

 This class is responsible for controlling the Month Salary functionality. It implements the Controller interface.
 */
public class IncomeController implements Controller {
    @FXML
    ComboBox monthsCombo;
    private Currency currency;
    private Variables variables;
    @FXML
    private AnchorPane content;
    @FXML
    private AnchorPane historyPane;
    @FXML
    private TextField addMonthSalary;
    @FXML
    private TextField addTaxRate;
    @FXML
    private ListView salaryHistory;
    @FXML
    private Label salaryComing;
    private SalarySingle salarySingle = SalarySingle.getInstance();
    @FXML
    private DatePicker selectedDate;
    @FXML
    private CheckBox mandatoryTaxes;
    @FXML
    private Label income;
    @FXML
    private Label addMonth;
    @FXML
    private Label addTax;
    @FXML
    private Label payday;
    @FXML
    private Button addBtn;
    @FXML
    private Button hourSalary;
    @FXML
    private Label history;
    @FXML
    private Label check;
    @FXML
    private Button back;

    private Salary salary;
    private LocalizationManager lan = LocalizationManager.getInstance();

    /**
     Initializes the controller and sets the style and language of the elements.
     */
    @Override
    public void initialize() {
        ThemeManager themeManager = ThemeManager.getInstance();
        salarySingle.refreshMonthsCombLanguage();
        content.setStyle(themeManager.getStyle());


        if (Locale.getDefault().getLanguage().matches("fi")) {
            mandatoryTaxes.setVisible(true);
        }

        back.setText(lan.getString("back"));
        income.setText(lan.getString("income"));
        addMonth.setText(lan.getString("addMonth"));
        addTax.setText(lan.getString("addTax"));
        payday.setText(lan.getString("payday"));
        addBtn.setText(lan.getString("add"));
        hourSalary.setText(lan.getString("hourly"));
        history.setText(lan.getString("salaryHistory"));
        check.setText(lan.getString("check"));
    }

    public void backToMain(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(MainApplication.class.getResource("main-view.fxml"));
        content.getChildren().setAll(pane);
    }
    /**
     * Loads the daySalary-view.fxml file into the content AnchorPane, sets the AnchorPane as the root node, and passes
     * the salarySingle and variables objects to the controller.
     *
     * @param event The ActionEvent triggered when the "Month" button is clicked.
     * @throws IOException If the income-view.fxml file cannot be loaded.
     */
    public void toDaySalaryView(ActionEvent event) throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("daySalary-view.fxml"));

        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);

        DaySalaryController daySalaryController = fxmloader.getController();
        daySalaryController.setVariables(salarySingle, variables);
    }

    /**

     Sets the SalarySingle and Variables objects. Sets info to the views.

     @param salary The SalarySingle object.

     @param variables The Variables object.
     */
    @Override
    public void setVariables(SalarySingle salary, Variables variables) {
        ThemeManager styler = ThemeManager.getInstance();
        this.salarySingle = salary;
        this.variables = variables;
        currency = Currency.getInstance(variables.getCurrentCurrency());

        addMonthSalary.setPromptText(currency.getSymbol());

        salaryHistory.getItems().addAll(salarySingle.getMonthSalaries());
        styler.styleListView(salaryHistory);
        monthsCombo.setPromptText(lan.getString("month"));


        monthsCombo.getItems().addAll(salarySingle.getMonths());
        mandatoryTaxes.setTooltip(new Tooltip("Pakolliset verot kuten työeläkemaksu ja työttömyysvakuusmaksu."));
        setIncomeHistoryClick();

    }
    /**
     * Sets the click event handler for the salaryHistory ListView. When an item is clicked, a confirmation alert is shown
     * to the user to confirm the deletion of the selected salary item. If the user confirms the deletion, the salary item
     * is deleted from the database, the salarySingle object, and the ListView. The ListView is then repopulated with the
     * remaining salary items.
     */
    private void setIncomeHistoryClick() {

        salaryHistory.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                IncomeDao incomeDao = new IncomeDao();
                int selectedIndex = salaryHistory.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0) {
                    Salary selected = (Salary) salaryHistory.getItems().get(selectedIndex);

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle(lan.getString("income"));
                    alert.setHeaderText(lan.getString("areYouSure"));
                    alert.setContentText(selected.toString());

                    Optional<ButtonType> option = alert.showAndWait();

                    if (option.get() == ButtonType.OK) {
                        incomeDao.deleteSalary(selected.getId(), "MONTH");
                        salarySingle.deleteMonthSalary(selected);
                        salaryHistory.getItems().clear();
                        salaryHistory.getItems().addAll(salarySingle.getMonthSalaries());

                    }
                } else {
                    // Nothing selected.
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle(lan.getString("income"));
                    alert.setHeaderText(lan.getString("selectedIncome"));
                    alert.setContentText(lan.getString("clickIncome"));
                    alert.showAndWait();
                }
            }
        });
    }

    /**
     * Event handler for the "Add" button. When the button is clicked, a new salary item is created and added to the
     * database, to the salarySingle object, and to the ListView. The ListView is then repopulated with the updated salary items.
     * If the input values are invalid, a warning alert is shown to the user.
     *
     * @throws SQLException If an error occurs while communicating with the database.
     */
    @FXML
    protected void onSalaryAddClick() throws SQLException {
        if ((addMonth != null && addTaxRate != null) && (addMonth.getText().matches("^[0-9]+$") || addTaxRate.getText().matches("^[0-9]+$"))) {
            IncomeDao incomeDao = new IncomeDao();
            double taxRate;
            int age;
            double insurance = 0;
            if (mandatoryTaxes.isSelected()) {
                age = salarySingle.getAge();
                if (age < 53) {
                    insurance = 7.15;
                } else if (age >= 53 && age < 63) {
                    insurance = 8.65;
                } else if (age >= 63) {
                    insurance = 7.15;
                }
                taxRate = (Double.parseDouble(addTaxRate.getText()) + insurance);
                salarySingle.calculateSalaryWithTaxRate(taxRate, Double.parseDouble(addMonthSalary.getText()), "MONTH");
            } else {
                taxRate = Double.parseDouble(addTaxRate.getText());
                salarySingle.calculateSalaryWithTaxRate(taxRate, Double.parseDouble(addMonthSalary.getText()), "MONTH");
            }
            LocalDate salaryDate = LocalDate.now();

            if (selectedDate.getValue() != null) {
                salaryDate = selectedDate.getValue();
            }
            Date date = java.sql.Date.valueOf(salaryDate);

            this.salary = new Salary(variables.getLoggedUserId(), Double.parseDouble(addMonthSalary.getText()), salarySingle.getMonthSalaryMinusTaxes(), salaryDate, currency.toString(), "MONTH", taxRate);
            incomeDao.saveSalary(variables.getLoggedUserId(), "MONTH", salarySingle.getMonthSalary(), salarySingle.getMonthSalaryMinusTaxes(), date, taxRate, currency.toString());
            salarySingle.createNewMonthSalary(salary);

            salaryHistory.getItems().clear();
            salaryHistory.getItems().addAll(salarySingle.getMonthSalaries());

            addMonthSalary.setText(null);
            addTaxRate.setText(null);
            selectedDate.setValue(null);
            mandatoryTaxes.setSelected(false);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(lan.getString("income"));
            alert.setHeaderText(lan.getString("addIncome"));
            alert.setContentText(lan.getString("formCorrect"));
            alert.showAndWait();
        }
    }
    /**
     * Calculates the total salary for the selected month and updates the salaryComing label with the result.
     *
     * @throws ParseException If an error occurs while parsing the selected month from the monthsComb ComboBox.
     */
    @FXML
    protected void calculateMonths() throws ParseException {
        int selectedIndex = monthsCombo.getSelectionModel().getSelectedIndex();
        String month = (String) monthsCombo.getItems().get(selectedIndex);
        String salaryAmount = String.format("%.2f", SalarySingle.getInstance().geTotalSalaryOfMonth(selectedIndex, "MONTH"));
        Locale finnish = new Locale("fi", "FI");
        if (lan.getLocale().equals(finnish)) {
            salaryComing.setText(lan.getString("salarycomingText") + " " + month + lan.getString("bendingWord") + " " + lan.getString("is") + " " + salaryAmount + " " + currency.getSymbol());
        } else
            salaryComing.setText(lan.getString("salarycomingText") + " " + month + " " + lan.getString("is") + " " + salaryAmount + " " + currency.getSymbol());

    }
}
