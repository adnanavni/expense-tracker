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

    public class DaySalaryController {
        private Variables variables;
        private Currency currency;
        private Salary salary;
        private double salaryTogether;
        @FXML
        private AnchorPane content;
        @FXML
        private TextField addHourSalary;
        @FXML
        private TextField addHours;
        @FXML
        private TextField taxRate;
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
            monthsComb.getItems().addAll(salarySingle.getMonths());

            salaryHistory.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    int selectedIndex = salaryHistory.getSelectionModel().getSelectedIndex();

                    Salary selected = (Salary) salaryHistory.getItems().get(selectedIndex);

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Salary calculation");
                    alert.setHeaderText("Add salary to calculation");
                    alert.setContentText(selected.toString());

                    Optional<ButtonType> option = alert.showAndWait();

                    if (option.get() == ButtonType.OK) {
                        salarySingle.deleteDaySalary(selected);
                        salaryHistory.getItems().clear();
                        salaryHistory.getItems().addAll(salarySingle.getDaySalaries());

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
        protected void onSalaryAddClick() {
            salarySingle.CalculateDaySalary(Double.parseDouble(addHours.getText()), Double.parseDouble(addHourSalary.getText()));
            salarySingle.calculateSalaryWithTaxRate(Double.parseDouble(taxRate.getText()), salarySingle.getDaySalary(), "DAY");
            salarySingle.setMonthSalaryMinusTaxes(salarySingle.getDaySalaryMinusTaxes());

            LocalDate salaryDate = LocalDate.now();

            if (selectedDate.getValue() != null) {
                salaryDate = selectedDate.getValue();
            }

            this.salary = new Salary(salarySingle.getDaySalary(), salaryDate, currency.toString(), "DAY", Double.parseDouble(taxRate.getText()));
            Date date = java.sql.Date.valueOf(salaryDate);
            this.salary.setDate(date);

            salarySingle.createNewDaySalary(salary);
            salaryHistory.getItems().clear();
            salaryHistory.getItems().addAll(salarySingle.getDaySalaries());

            addHourSalary.setText(null);
            addHours.setText(null);
            taxRate.setText(null);
            selectedDate.setValue(null);
        }
        @FXML
        protected void calculateMonths() throws ParseException {
            int selectedIndex = monthsComb.getSelectionModel().getSelectedIndex();
            String month = (String) monthsComb.getItems().get(selectedIndex);
            salaryComing.setText("Salary amount of " + month + " is " + SalarySingle.getInstance().geTotalSalaryOfMonth(month, "DAY"));
        }
    }

