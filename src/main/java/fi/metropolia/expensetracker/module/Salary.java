package fi.metropolia.expensetracker.module;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

public class Salary {

    private static Integer currentId = 1;
    private static Salary INSTANCE = null;
    private double salary;
    private double taxRate;
    private Date date2;
    private String type;
    private Integer id;
    private LocalDate date;
    private String usedCurrency;
    private IncomeDao incomeDao = new IncomeDao();
    private int incomeID;


    public Salary(double salary, LocalDate date, String usedCurrency, String type, double taxRate ) throws SQLException {
        this.salary = salary;
        this.date = date;
        this.usedCurrency = usedCurrency;
        this.taxRate = taxRate;
        this.type = type;

        id = currentId;
        currentId++;

        incomeID = incomeDao.getIncomeId(Variables.getInstance().getLoggedUserId(), type, salary, java.sql.Date.valueOf(date), taxRate, usedCurrency);

    }

    public LocalDate getLocalDate() {
        return this.date;
    }

    public double getSalary() {
        return this.salary;
    }
    public double getSalaryMinusTaxes (String type) {
       return SalarySingle.getInstance().calculateSalaryWithTaxRate(taxRate, salary, type);
    }

    public Date getDate(){
        return this.date2 = (java.sql.Date.valueOf(date));
    }
    public double getTaxRate() {
        return taxRate;
    }
    public String getUsedCurrency() {
        return usedCurrency;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Salary amount of the date " + date +
                " is " + salary + usedCurrency + " and minus " + incomeDao.getTaxrate(incomeID) +
                "% tax rate it is: " + String.format("%.2f", incomeDao.getSalaryWithTaxrate(incomeID));
    }
}
