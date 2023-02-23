package fi.metropolia.expensetracker.module;

import java.time.LocalDate;


public class Salary {

    private static Integer currentId = 1;
    private double daySalary;
    private double hours;
    private double hourSalary;
    private Integer id;
    private LocalDate date;
    private String usedCurrency;

    public Salary (Double daySalary, LocalDate date, String usedCurrency) {
        this.daySalary = daySalary;
        this.date = date;
        this.usedCurrency = usedCurrency;

        id = currentId;
        currentId++;
    }
    public double getDaySalary(){return this.daySalary;}

    public void setUsedCurrency(String currency){usedCurrency = currency;}

    public Double getHourSalary(){return hourSalary;}
    public void setHourSalaryAmount(Double hourAmount) {this.hourSalary = hourAmount;}

    public double getHours(){return this.hours;}
    public void setHours(double hours){this.hours = hours;}

    public Integer getId(){return id;}

    @Override
    public String toString() {
        return "Salary amount of the date " + date +
                " is " + daySalary + usedCurrency;
    }
}
