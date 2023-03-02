package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.Salary;
import fi.metropolia.expensetracker.module.SalarySingle;
import fi.metropolia.expensetracker.module.ThemeManager;
import fi.metropolia.expensetracker.module.Variables;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Date;
import java.util.Optional;


public class IncomeController {
    private Currency currency;
    private Salary salary;

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
    ComboBox monthsCombo;
    @FXML
    private DatePicker selectedDate;
    @FXML
    private Button addBtn;
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
        this.salarySingle = salary;
        this.variables = variables;
        currency = Currency.getInstance(variables.getCurrentCurrency());

        salaryHistory.getItems().addAll(salarySingle.getMonthSalaries());
        monthsCombo.getItems().addAll(salarySingle.getMonths());
        mandatoryTaxes.setTooltip(new Tooltip("Add mandatory taxes, such as pension contribution and unemployment insurance"));

        salaryHistory.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                int selectedIndex = salaryHistory.getSelectionModel().getSelectedIndex();

                Salary selected = (Salary) salaryHistory.getItems().get(selectedIndex);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Salary deletion");
                alert.setHeaderText("Do you want to delete salary of the history");
                alert.setContentText(selected.toString());

                Optional<ButtonType> option = alert.showAndWait();

                if (option.get() == ButtonType.OK) {
                    salarySingle.deleteMonthSalary(selected);

                    salaryHistory.getItems().clear();
                    salaryHistory.getItems().addAll(salarySingle.getMonthSalaries());
                }
            }
        });
    }


    @FXML
    protected void onSalaryAddClick() {
        salarySingle.calculateSalaryWithTaxRate(Double.parseDouble(addTaxRate.getText()), Double.parseDouble(addMonthSalary.getText()), "MONTH", mandatoryTaxes.isSelected());
        salarySingle.setMonthSalaryMinusTaxes(salarySingle.getDaySalaryMinusTaxes());
        LocalDate salaryDate = LocalDate.now();

        if (selectedDate.getValue() != null) {
            salaryDate = selectedDate.getValue();
        }

        this.salary = new Salary(salarySingle.geTotalSalaryOfMonth(), salaryDate, currency.toString(), "MONTH", Double.parseDouble(addTaxRate.getText()));
        Date date = java.sql.Date.valueOf(salaryDate);
        this.salary.setDate(date);
        this.salary.setMandTax(mandatoryTaxes.isSelected());

        salarySingle.createNewMonthSalary(salary);
        salaryHistory.getItems().clear();
        salaryHistory.getItems().addAll(salarySingle.getMonthSalaries());

        addMonthSalary.setText(null);
        addTaxRate.setText(null);
        selectedDate.setValue(null);
    }

    @FXML
    protected void calculateMonths() throws ParseException {
        int selectedIndex = monthsCombo.getSelectionModel().getSelectedIndex();
        String month = (String) monthsCombo.getItems().get(selectedIndex);
        salaryComing.setText("Salary amount of " + month + " is " + SalarySingle.getInstance().geTotalSalaryOfMonth(month, "MONTH"));
    }


}
