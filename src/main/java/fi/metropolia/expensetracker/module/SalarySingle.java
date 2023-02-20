package fi.metropolia.expensetracker.module;

import java.util.ArrayList;

public class SalarySingle {


    private double daySalary;
    private ArrayList<Salary> salaries = new ArrayList<Salary>();

    private static SalarySingle INSTANCE = null;
    public static SalarySingle getInstance() {
        if (INSTANCE == null)
            INSTANCE = new SalarySingle();
        return INSTANCE;
    }

    private SalarySingle(){}

    public ArrayList<Salary> getDaySalaries(){

        return salaries;
    }
    public double getDaySalary(){
        return this.daySalary;
    }

    public void createNewSalary(Salary salary) {
        salaries.add(salary);
    }
    public void CalculateDaySalary(double hours, double hourSalary) {
        this.daySalary = (hours*hourSalary);

    }

}
