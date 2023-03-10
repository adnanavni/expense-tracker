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
    private SalarySingle salarySingle;
    @FXML
    private DatePicker selectedDate;
    @FXML
    private CheckBox mandatoryTaxes;

    public void initialize() {
        ThemeManager themeManager = ThemeManager.getInstance();
        content.setStyle(themeManager.getStyle());
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
        IncomeDao incomeDao = new IncomeDao();

        this.salarySingle = salary;
        this.variables = variables;
        currency = Currency.getInstance(variables.getCurrentCurrency());

        salaryHistory.getItems().addAll(incomeDao.getSalariesWithType(variables.getLoggedUserId(), "MONTH"));
        monthsCombo.getItems().addAll(salarySingle.getMonths());
        mandatoryTaxes.setTooltip(new Tooltip("Add mandatory taxes, such as pension contribution and unemployment insurance"));

        salaryHistory.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                IncomeDao incomeDao = new IncomeDao();
                int selectedIndex = salaryHistory.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0) {
                    int incomeID = 0;

                    Salary selected = (Salary) salaryHistory.getItems().get(selectedIndex);

                    incomeID = selected.getId();

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Salary deletion");
                    alert.setHeaderText("Do you want to delete salary of the history");
                    alert.setContentText(selected.toString());

                    Optional<ButtonType> option = alert.showAndWait();

                    if (option.get() == ButtonType.OK) {
                        incomeDao.deleteSalary(incomeID, "MONTH");
                        salaryHistory.getItems().clear();
                        salaryHistory.getItems().addAll(incomeDao.getSalariesWithType(variables.getLoggedUserId(), "MONTH"));
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

        if (mandatoryTaxes.isSelected()) {
            double insurance = 7.15;
            double pension = 1.40;
            taxRate = (Double.parseDouble(addTaxRate.getText()) + insurance + pension);

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

        incomeDao.saveSalary(variables.getLoggedUserId(), "MONTH", salarySingle.getMonthSalary(), salarySingle.getMonthSalaryMinusTaxes(), date, taxRate, currency.toString());


        salaryHistory.getItems().clear();
        salaryHistory.getItems().addAll(incomeDao.getSalariesWithType(variables.getLoggedUserId(), "MONTH"));

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
