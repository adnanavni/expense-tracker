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
import java.util.*;

public class MonthSalaryController {
        @FXML
        private AnchorPane content;
        @FXML
        private AnchorPane daySalaryPane;

        @FXML
        private TextField addMonthSalary;
        @FXML
        private TextField addTaxRate;
        @FXML
        private ListView salaryHistory;

        @FXML
        private Label salaryComing;
        private SalarySingle salarySingle;
        @FXML ComboBox monthsCombo;
        @FXML
        private DatePicker selectedDate;
        @FXML
        private Button addBtn;
        @FXML
        private Variables variables;
        private Currency currency;
        private Salary salary;

        public void initialize() {
            ThemeManager themeManager = ThemeManager.getInstance();
            content.setStyle(themeManager.getStyle());

        }

        public void backToIncome(ActionEvent event) throws IOException {
            AnchorPane pane = FXMLLoader.load(MainApplication.class.getResource("income-view.fxml"));
            content.getChildren().setAll(pane);
        }

        public void setVariables(SalarySingle salary, Variables variables) {
            this.salarySingle = salary;
            this.variables = variables;
            currency = Currency.getInstance(variables.getCurrentCurrency());

            salaryHistory.getItems().addAll(salarySingle.getMonthSalaries());
            monthsCombo.getItems().addAll(salarySingle.getMonths());

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
            salarySingle.calculateSalaryWithTaxRate(Double.parseDouble(addTaxRate.getText()), Double.parseDouble(addMonthSalary.getText()), "MONTH");
            salarySingle.setMonthSalaryMinusTaxes(salarySingle.getDaySalaryMinusTaxes());
            LocalDate salaryDate = LocalDate.now();

            if (selectedDate.getValue() != null) {
                salaryDate = selectedDate.getValue();
            }
            //Salary addedMonthSalary = new Salary(salarySingle.getMonthSalary(), salaryDate, currency.toString(), "MONTH", Double.parseDouble(addTaxRate.getText()));
            this.salary = new Salary(salarySingle.geTotalSalaryOfMonth(), salaryDate, currency.toString(), "MONTH", Double.parseDouble(addTaxRate.getText()));
            Date date = java.sql.Date.valueOf(salaryDate);
            this.salary.setDate(date);

            //salarySingle.createNewMonthSalary(addedMonthSalary);
            salarySingle.createNewMonthSalary(salary);
            salaryHistory.getItems().clear();
            salaryHistory.getItems().addAll(salarySingle.getMonthSalaries());
        }
        @FXML
        protected void calculateMonths() throws ParseException {
            int selectedIndex = monthsCombo.getSelectionModel().getSelectedIndex();
            String month = (String) monthsCombo.getItems().get(selectedIndex);
            salaryComing.setText("Salary amount of " + month + " is " + SalarySingle.getInstance().geTotalSalaryOfMonth(month, "MONTH"));
        }

}


