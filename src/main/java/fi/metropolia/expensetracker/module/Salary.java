package fi.metropolia.expensetracker.module;

import fi.metropolia.expensetracker.module.Dao.IncomeDao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Date;

public class Salary {
    private double salary;
    private double taxRate;
    private Date date2;
    private String type;
    private Integer id;
    private LocalDate date;
    private String usedCurrency;
    private IncomeDao incomeDao = new IncomeDao();

    public Salary(int id, double salary, LocalDate date, String usedCurrency, String type, double taxRate) throws SQLException {
        this.salary = salary;
        this.date = date;
        this.usedCurrency = usedCurrency;
        this.taxRate = taxRate;
        this.type = type;
        this.id = id;
    }
    public Salary() {}

    public double getSalary() {
        return this.salary;
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

    @Override
    public String toString() {
        Currency currency = Currency.getInstance(Variables.getInstance().getCurrentCurrency());
        return "Salary amount of the date " + date +
                " is " + String.format("%.2f", salary) + " " + currency.getSymbol() + " and minus " + String.format("%.2f", incomeDao.getTaxrate(id)) +
                "% tax rate it is: " + String.format("%.2f", incomeDao.getSalaryWithTaxrate(id)) + " " + currency.getSymbol();
    }

}