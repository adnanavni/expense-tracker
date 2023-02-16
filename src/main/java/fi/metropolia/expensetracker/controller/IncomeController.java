package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.MainApplication;
import fi.metropolia.expensetracker.module.Salary;
import fi.metropolia.expensetracker.module.SalarySingle;
import fi.metropolia.expensetracker.module.Variables;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Optional;

public class IncomeController {
    @FXML
    private AnchorPane content;
    @FXML
    private TextField addHourSalary;
    @FXML
    private TextField btnAddHours;
    @FXML
    private ListView salaryHistory;

    @FXML
    private Label salaryComing;
    private SalarySingle salarySingle;

    @FXML
    private DatePicker selectedDate;
    @FXML
    private Button addBtn;
    private Variables variables;
    private Currency currency;
    private Salary salary;
    private double salaryTogether;


    public void backToMain(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(MainApplication.class.getResource("main-view.fxml"));
        content.getChildren().setAll(pane);
    }

    public void setCalculator(SalarySingle salary, Variables variables) {
        //this.salary = salary;
        this.salarySingle = salary;
        this.variables = variables;
        currency = Currency.getInstance(variables.getCurrentCurrency());

        salaryHistory.getItems().addAll(salarySingle.getDaySalaries());

        salaryHistory.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                int selectedIndex = salaryHistory.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0) {
                    Salary selected = (Salary) salaryHistory.getItems().get(selectedIndex);

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Salary calculation");
                    alert.setHeaderText("Add salary to calculation");
                    alert.setContentText(selected.toString());

                    Optional<ButtonType> option = alert.showAndWait();

                    if (option.get() == ButtonType.OK) {
                        salaryTogether += selected.getDaySalary();
                    }
                    salaryComing.setText("Salary coming: " + salaryTogether);
                }
            }

        });
    }

    @FXML
    protected void onSalaryAddClick() {
       // salary.calculateDaySalary(Double.parseDouble(btnAddHours.getText()), Double.parseDouble(addHourSalary.getText()));
        salarySingle.CalculateDaySalary(Double.parseDouble(btnAddHours.getText()), Double.parseDouble(addHourSalary.getText()));

        LocalDate salaryDate = LocalDate.now();
        if(selectedDate.getValue() != null) {
            salaryDate = selectedDate.getValue();
        }

        //Salary addedDaySalary = new Salary(salary.getDaySalary(), salaryDate, currency.toString());
        Salary addedDaySalary = new Salary(salarySingle.getDaySalary(), salaryDate, currency.toString());


        //salary.addDaySalaryToList(addedDaySalary);
        salarySingle.createNewSalary(addedDaySalary);
        salaryHistory.getItems().clear();
        salaryHistory.getItems().addAll(salarySingle.getDaySalaries());
        //        salaryHistory.getItems().addAll(salary.getDaySalaries());
    }
}
