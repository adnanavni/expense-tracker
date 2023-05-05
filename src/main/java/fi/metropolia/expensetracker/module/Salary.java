package fi.metropolia.expensetracker.module;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Date;

/**
 * Salary-object, which includes info about users singular salaries
 * */
public class Salary {
    private double salary;
    private double taxRate;
    private Date date2;
    private String type;
    private Integer id;
    private LocalDate date;
    private String usedCurrency;

    /**
     * Reference to LocalizationManager - singleton, which is used to localize this Salary -object as well.
     * */
    LocalizationManager localizationManager = LocalizationManager.getInstance();

    private double salaryMinusTaxes;

    public Salary(int id, double salary, double salaryMinusTaxes, LocalDate date, String usedCurrency, String type, double taxRate) throws SQLException {
        this.salary = salary;
        this.date = date;
        this.usedCurrency = usedCurrency;
        this.taxRate = taxRate;
        this.type = type;
        this.id = id;
        this.salaryMinusTaxes = salaryMinusTaxes;
    }

    public Salary() {
    }

    public double getSalary() {
        return this.salary;
    }

    public double getSalaryMinusTaxes() {
        return salaryMinusTaxes;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public double getSalaryMinusTaxes(String type) {
        return SalarySingle.getInstance().calculateSalaryWithTaxRate(taxRate, salary, type);
    }

    public String getType() {
        return type;
    }

    public void setTaxRate(double taxrate) {
        this.taxRate = taxrate;
    }

    public Date getDate() {
        return this.date2 = (java.sql.Date.valueOf(date));
    }

    public void setUsedCurrency(String currency) {
        this.usedCurrency = currency;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    /**
     * @return salary-object in String-type with all salary info
     * */

    @Override
    public String toString() {
        Currency currency = Currency.getInstance(Variables.getInstance().getCurrentCurrency());
        return date + " " + localizationManager.getString("salaryText") + String.format("%.2f", taxRate) + "% " +
                localizationManager.getString("is") + " " + String.format("%.2f", salaryMinusTaxes) + " " + currency.getSymbol();
    }
}