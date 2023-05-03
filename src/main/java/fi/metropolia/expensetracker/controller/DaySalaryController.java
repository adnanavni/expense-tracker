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

public class DaySalaryController implements Controller {
    private Variables variables;
    private Currency currency;
    @FXML
    private AnchorPane content;
    @FXML
    private TextField addHourSalary;
    @FXML
    private TextField addHours;
    @FXML
    private TextField addTaxRate;
    @FXML
    private ListView salaryHistory;
    @FXML
    private ComboBox monthsComb;
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
    private Label addHourly;

    @FXML
    private Label addHour;

    @FXML
    private Label addTax;

    @FXML
    private Label workingDay;

    @FXML
    private Button addBtn;

    @FXML
    private Button monthly;

    @FXML
    private Label history;

    @FXML
    private Label check;

    @FXML
    private Button back;

    private IncomeDao incomeDao = new IncomeDao();
    private Salary salary;

    private LocalizationManager lan = LocalizationManager.getInstance();

    @Override
    public void initialize() {
        ThemeManager themeManager = ThemeManager.getInstance();
        content.setStyle(themeManager.getStyle());
        salarySingle.refreshMonthsCombLanguage();

        if (Locale.getDefault().getLanguage().matches("fi")) {
            mandatoryTaxes.setVisible(true);
        }

        back.setText(lan.getString("back"));
        income.setText(lan.getString("income"));
        addHourly.setText(lan.getString("addHourly"));
        addHour.setText(lan.getString("hours"));
        addTax.setText(lan.getString("addTax"));
        workingDay.setText(lan.getString("workingDay"));
        addBtn.setText(lan.getString("add"));
        monthly.setText(lan.getString("monthly"));
        history.setText(lan.getString("salaryHistory"));
        check.setText(lan.getString("check"));
    }


    public void backToMain(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(MainApplication.class.getResource("main-view.fxml"));
        content.getChildren().setAll(pane);
    }

    @Override
    public void setVariables(SalarySingle salary, Variables variables) {
        this.salarySingle = salary;
        this.variables = variables;
        ThemeManager styler = ThemeManager.getInstance();
        currency = Currency.getInstance(variables.getCurrentCurrency());

        addHourSalary.setPromptText(currency.getSymbol());

        salaryHistory.getItems().addAll(salarySingle.getDaySalaries());
        styler.styleListView(salaryHistory);
        monthsComb.setPromptText(lan.getString("month"));

        monthsComb.getItems().addAll(salarySingle.getMonths());


        mandatoryTaxes.setTooltip(new Tooltip("Pakolliset verot kuten työeläkemaksu ja työttömyysvakuusmaksu."));

        salaryHistory.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                int incomeID = 0;
                int selectedIndex = salaryHistory.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0) {
                    Salary selected = (Salary) salaryHistory.getItems().get(selectedIndex);

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle(lan.getString("income"));
                    alert.setHeaderText(lan.getString("areYouSure"));
                    alert.setContentText(selected.toString());

                    incomeID = selected.getId();

                    Optional<ButtonType> option = alert.showAndWait();

                    if (option.get() == ButtonType.OK) {
                        incomeDao.deleteSalary(incomeID, "DAY");
                        salarySingle.deleteDaySalary(selected);
                        salaryHistory.getItems().clear();
                        salaryHistory.getItems().addAll(salarySingle.getDaySalaries());
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle(lan.getString("income"));
                    alert.setHeaderText(lan.getString("selectedIncome"));
                    alert.setContentText(lan.getString("clickIncome"));
                    alert.showAndWait();
                }
            }
        });
    }

    public void toMonthSalaryView(ActionEvent event) throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("income-view.fxml"));

        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);

        IncomeController monthSalaryController = fxmloader.getController();
        monthSalaryController.setVariables(salarySingle, variables);
    }

    @FXML
    protected void onSalaryAddClick() throws SQLException {

        if ((addHourSalary.getText().matches("^[0-9]+$") && addHours.getText().matches("^[0-9]+$")) && addTaxRate.getText().matches("^[0-9]+$") && (addHourSalary != null && addHours != null && addTaxRate != null)) {
            IncomeDao incomeDao = new IncomeDao();
            double taxRate;

            salarySingle.calculateDaySalary(Double.parseDouble(addHours.getText()), Double.parseDouble(addHourSalary.getText()));

            if (mandatoryTaxes.isSelected()) {
                double insurance = 0;
                double age = salarySingle.getAge();
                if (age < 53) {
                    insurance = 7.15;
                } else if (age >= 53 && age < 63) {
                    insurance = 8.65;
                } else if (age >= 63) {
                    insurance = 7.15;
                }
                // double pension = 1.40;
                taxRate = (Double.parseDouble(addTaxRate.getText()) + insurance);
                salarySingle.calculateSalaryWithTaxRate(taxRate, salarySingle.getDaySalary(), "DAY");
            } else {
                taxRate = Double.parseDouble(addTaxRate.getText());
                salarySingle.calculateSalaryWithTaxRate(taxRate, salarySingle.getDaySalary(), "DAY");
            }

            LocalDate salaryDate = LocalDate.now();

            if (selectedDate.getValue() != null) {
                salaryDate = selectedDate.getValue();
            }
            Date date = java.sql.Date.valueOf(salaryDate);

            incomeDao.saveSalary(variables.getLoggedUserId(), "DAY", salarySingle.getDaySalary(), salarySingle.getDaySalaryMinusTaxes(), date, taxRate, currency.toString());
            this.salary = new Salary(variables.getLoggedUserId(), salarySingle.getDaySalary(), salarySingle.getDaySalaryMinusTaxes(), salaryDate, currency.toString(), "DAY", taxRate);
            salarySingle.createNewDaySalary(salary);
            salaryHistory.getItems().clear();
            salaryHistory.getItems().addAll(salarySingle.getDaySalaries());

            addHourSalary.setText(null);
            addHours.setText(null);
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

    @FXML
    protected void calculateMonths() throws ParseException {
        int selectedIndex = monthsComb.getSelectionModel().getSelectedIndex();
        String month = (String) monthsComb.getItems().get(selectedIndex);
        String salaryAmount = String.format("%.2f", SalarySingle.getInstance().geTotalSalaryOfMonth(selectedIndex, "DAY"));
        Locale finnish = new Locale("fi", "FI");
        if (lan.getLocale().equals(finnish)) {
            salaryComing.setText(lan.getString("salarycomingText") + " " + month + lan.getString("bendingWord") + " " + lan.getString("is") + " " + salaryAmount + " " + currency.getSymbol());
        } else
            salaryComing.setText(lan.getString("salarycomingText") + " " + month + " " + lan.getString("is") + " " + salaryAmount + " " + currency.getSymbol());


    }

}

