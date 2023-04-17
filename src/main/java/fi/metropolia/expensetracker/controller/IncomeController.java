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


public class IncomeController {
    @FXML
    ComboBox monthsCombo;
    private Currency currency;
    private Variables variables;
    @FXML
    private AnchorPane content;
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

    public void toDaySalaryView(ActionEvent event) throws IOException {
        FXMLLoader fxmloader = new FXMLLoader(MainApplication.class.getResource("daySalary-view.fxml"));

        AnchorPane pane = fxmloader.load();
        content.getChildren().setAll(pane);

        DaySalaryController daySalaryController = fxmloader.getController();
        daySalaryController.setVariables(salarySingle, variables);
    }

    public void setVariables(SalarySingle salary, Variables variables) {
        this.salarySingle = salary;
        this.variables = variables;
        currency = Currency.getInstance(variables.getCurrentCurrency());

        addMonthSalary.setPromptText(currency.getSymbol());

        salaryHistory.getItems().addAll(salarySingle.getMonthSalaries());
        monthsCombo.getItems().addAll(salarySingle.getMonths());
        mandatoryTaxes.setTooltip(new Tooltip("Pakolliset verot kuten työeläkemaksu ja työttömyysvakuusmaksu."));

        salaryHistory.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                IncomeDao incomeDao = new IncomeDao();
                int selectedIndex = salaryHistory.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0) {
                    Salary selected = (Salary) salaryHistory.getItems().get(selectedIndex);

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Salary deletion");
                    alert.setHeaderText("Do you want to delete salary of the history");
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
                    alert.setTitle("No selection!");
                    alert.setHeaderText("No selected income!");
                    alert.setContentText("Click an existing income.");
                    alert.showAndWait();
                }
            }
        });
    }

    @FXML
    protected void onSalaryAddClick() throws SQLException {
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
            //double pension = 1.40;
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
    }

    @FXML
    protected void calculateMonths() throws ParseException {
        int selectedIndex = monthsCombo.getSelectionModel().getSelectedIndex();
        String month = (String) monthsCombo.getItems().get(selectedIndex);
        String salaryAmount = String.format("%.2f", SalarySingle.getInstance().geTotalSalaryOfMonth(month, "MONTH"));
        salaryComing.setText("Salary amount of " + month + " is " + salaryAmount + " " + currency.getSymbol());
    }
}
