package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.*;
import fi.metropolia.expensetracker.module.Dao.IncomeDao;
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
import java.util.Optional;

public class DaySalaryController {
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

    private SalarySingle salarySingle;

    @FXML
    private DatePicker selectedDate;
    @FXML
    private Button addBtn;
    @FXML
    private CheckBox mandatoryTaxes;

    private IncomeDao incomeDao = new IncomeDao();
    private Salary salary;

    public void initialize() {
        ThemeManager themeManager = ThemeManager.getInstance();
        content.setStyle(themeManager.getStyle());

    }

    public void backToMain(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(MainApplication.class.getResource("main-view.fxml"));
        content.getChildren().setAll(pane);
    }

    public void setVariables(SalarySingle salary, Variables variables) {
        this.salarySingle = salary;
        this.variables = variables;
        currency = Currency.getInstance(variables.getCurrentCurrency());

        salaryHistory.getItems().addAll(salarySingle.getDaySalaries());
        //salaryHistory.getItems().addAll(incomeDao.getSalariesWithType(variables.getLoggedUserId(), "DAY"));
        monthsComb.getItems().addAll(salarySingle.getMonths());
        mandatoryTaxes.setTooltip(new Tooltip("Add mandatory taxes, such as pension contribution and unemployment insurance"));

        salaryHistory.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                int incomeID = 0;
                int selectedIndex = salaryHistory.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0) {
                    Salary selected = (Salary) salaryHistory.getItems().get(selectedIndex);

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Salary calculation");
                    alert.setHeaderText("Add salary to calculation");
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
                    alert.setTitle("No selection!");
                    alert.setHeaderText("No selected income!");
                    alert.setContentText("Click an existing income.");
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

        if(addHourSalary.getText().matches("^[0-9]+$") && addHours.getText().matches("^[0-9]+$") && addTaxRate.getText().matches("^[0-9]+$")) {
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
       // salaryHistory.getItems().addAll(incomeDao.getSalariesWithType(variables.getLoggedUserId(), "DAY"));

            addHourSalary.setText(null);
            addHours.setText(null);
            addTaxRate.setText(null);
            selectedDate.setValue(null);
            mandatoryTaxes.setSelected(false);
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Add an income");
            alert.setHeaderText("You cant add an income");
            alert.setContentText("Fill the form correctly");
            alert.showAndWait();
        }
    }

    @FXML
    protected void calculateMonths() throws ParseException {
        int selectedIndex = monthsComb.getSelectionModel().getSelectedIndex();
        String month = (String) monthsComb.getItems().get(selectedIndex);
        String salaryAmount = String.format("%.2f", SalarySingle.getInstance().geTotalSalaryOfMonth(month, "DAY"));
        salaryComing.setText("Salary amount of " + month + " is " + salaryAmount + " " + currency.getSymbol());
    }
}

