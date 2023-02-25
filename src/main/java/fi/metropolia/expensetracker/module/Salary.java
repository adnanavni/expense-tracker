package fi.metropolia.expensetracker.module;

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

    public Salary(double salary, LocalDate date, String usedCurrency, String type, double taxRate ) {
        this.salary = salary;
        this.date = date;
        this.usedCurrency = usedCurrency;
        this.taxRate = taxRate;
        this.type = type;

        id = currentId;
        currentId++;
    }

    public double getDaySalary() {
        return SalarySingle.getInstance().getDaySalary();
    }

    public double getMonthSalary() {
        //return SalarySingle.getInstance().getMonthSalary();
        return salary;
    }

    public double getSalaryMinusTaxes (String type) {
       return SalarySingle.getInstance().calculateSalaryWithTaxRate(taxRate, salary, type);
    }
    public void setDate(Date date) {
        this.date2 = date;
    }

    public Date getDate(){
        return this.date2;
    }
    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Salary amount of the date " + date +
                " is " + salary + usedCurrency + " and minus " + taxRate+
                "% tax rate it is: " + SalarySingle.getInstance().calculateSalaryWithTaxRate(taxRate, salary, type);
    }
}
