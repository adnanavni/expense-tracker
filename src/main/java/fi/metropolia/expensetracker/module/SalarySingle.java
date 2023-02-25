package fi.metropolia.expensetracker.module;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SalarySingle {


    private double daySalary;
    private double monthSalary;
    private double taxRate;
    private double monthSalaryMinusTaxes;
    private double daySalaryMinusTaxes;
    private ArrayList<Salary> daySalaries = new ArrayList<Salary>();
    private ArrayList<Salary> monthSalaries = new ArrayList<>();
    private Map<String, Integer> months = new HashMap<>() {{
        put("January", 0);
        put("February", 1);
        put("March", 2);
        put("April", 3);
        put("May", 4);
        put("June", 5);
        put("July", 6);
        put("August", 7);
        put("September", 8);
        put("October", 9);
        put("November", 10);
        put("December", 11);
    }};
    private static SalarySingle INSTANCE = null;

    public static SalarySingle getInstance() {
        if (INSTANCE == null)
            INSTANCE = new SalarySingle();
        return INSTANCE;
    }

    private SalarySingle() {
    }

    public double calculateSalaryWithTaxRate(double taxRate, double salary, String type) {
        this.taxRate = taxRate;
        double procent = 1 - (taxRate/100);
        double wantedSalaryminusTaxes = 0;
        if (type == "DAY") {
            this.daySalaryMinusTaxes = (salary*procent);
            setDaySalaryMinusTaxes(this.daySalaryMinusTaxes);
            setDaySalary(salary);
            wantedSalaryminusTaxes = this.daySalaryMinusTaxes;
        }
        if (type == "MONTH") {
            this.monthSalaryMinusTaxes = (salary*procent);
            setMonthSalaryMinusTaxes(this.monthSalaryMinusTaxes);
            setMonthSalary(salary);
            wantedSalaryminusTaxes = this.monthSalaryMinusTaxes;
        }
        return wantedSalaryminusTaxes;
    }
    public void CalculateDaySalary(double hours, double hourSalary) {
        this.daySalary = (hours * hourSalary);
    }

    public ArrayList<Salary> getDaySalaries() {
        return daySalaries;
    }
    public ArrayList<Salary> getMonthSalaries(){
        return monthSalaries;
    }

    public double getTaxRate() {
        return this.taxRate;
    }

    public void setDaySalary(double daySalary) {
        this.daySalary = daySalary;
    }
    public double getDaySalary() {
        return this.daySalary;
    }
    public double getDaySalaryMinusTaxes() {
        return this.monthSalaryMinusTaxes;
    }
    public void setDaySalaryMinusTaxes(double daySalaryMinusTaxes) {
        this.daySalaryMinusTaxes = daySalaryMinusTaxes;
    }
    public void setMonthSalaryMinusTaxes(double monthSalaryMinusTaxes) {
        this.monthSalaryMinusTaxes = monthSalaryMinusTaxes;
    }

    public void setMonthSalary(double monthSalary) {
        this.monthSalary = monthSalary;
    }
    public double geTotalSalaryOfMonth() {
        return this.monthSalary;
    }


    public void createNewMonthSalary(Salary salary) {
        monthSalaries.add(salary);
    }

    public void createNewDaySalary(Salary salary) {
        daySalaries.add(salary);
    }

    public void deleteMonthSalary(Salary salary) {
        monthSalaries.remove(salary);
    }

    public void deleteDaySalary(Salary salary) {
        daySalaries.remove(salary);
    }

    public ArrayList<String> getMonths() {
        return new ArrayList<>(months.keySet());
    }

    //Change format so no  need to hardcode years
    public double geTotalSalaryOfMonth(String m, String type) throws ParseException {
        String s = null;
        double totalSalary = 0;
        System.out.println(m);
        switch (m) {
            case ("January") :
                totalSalary = getSalariesBetweenStartAndFinish("31/12/2022", "01/02/2023", type);
                break;
            case ("February"):
                totalSalary = getSalariesBetweenStartAndFinish("31/01/2023", "01/03/2023", type);
                break;
            case ("March"):
                totalSalary = getSalariesBetweenStartAndFinish("28/02/2023", "01/04/2023", type);
                break;
            case ("April"):
                totalSalary = getSalariesBetweenStartAndFinish("31/03/2023", "01/05/2023", type);
                break;
            case ("May"):
                totalSalary = getSalariesBetweenStartAndFinish("30/04/2023", "01/06/2023", type);
                break;
            case ("June"):
                totalSalary = getSalariesBetweenStartAndFinish("31/05/2023", "01/07/2023", type);
                break;
            case ("July"):
                totalSalary = getSalariesBetweenStartAndFinish("30/06/2023", "01/08/2023", type);
                break;
            case ("August"):
                totalSalary = getSalariesBetweenStartAndFinish("31/07/2023", "01/09/2023", type);
                break;
            case ("September"):
                totalSalary = getSalariesBetweenStartAndFinish("31/08/2023", "01/10/2023", type);
                break;
            case ("October"):
                totalSalary = getSalariesBetweenStartAndFinish("30/9/2023", "01/11/2023", type);
                break;
            case ("November") :
                totalSalary =  getSalariesBetweenStartAndFinish("31/10/2023", "01/12/2023", type);
                break;
            case ("December"):
                totalSalary = getSalariesBetweenStartAndFinish("30/11/2023", "01/01/2024", type);
                break;
            default :
                s = "Ei löytynyt kuukautta";
                System.out.println(s);
                return 0;
        }
        return totalSalary;
    }


    public double getSalariesBetweenStartAndFinish(String start, String end, String type) throws ParseException {

        ArrayList<Double> salaries = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date parsedStart = sdf.parse(start);
        Date parsedEnd = sdf.parse(end);
        double salariesTogether = 0;

        if (type == "MONTH") {
            for (Salary eachDate : SalarySingle.getInstance().getMonthSalaries()) {
                if (eachDate.getDate().after(parsedStart) && eachDate.getDate().before(parsedEnd)) {
                    salaries.add(eachDate.getSalaryMinusTaxes("MONTH"));
                }
            }
        } if (type == "DAY") {
            for (Salary eachDate : SalarySingle.getInstance().getDaySalaries()) {
                if (eachDate.getDate().after(parsedStart) && eachDate.getDate().before(parsedEnd)) {
                    salaries.add(eachDate.getSalaryMinusTaxes("DAY"));
                }
            }
        }
        for (double eachSalary : salaries) {
            salariesTogether += eachSalary;
        }
        return salariesTogether;
    }

      /*
    public static DateFormat getShortDateInstanceWithoutYears(Locale locale) {
        SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale);
        sdf.applyPattern(sdf.toPattern().replaceAll("[^\\p{Alpha}]*y+[^\\p{Alpha}]*", ""));
        return sdf;
    }*/

}
