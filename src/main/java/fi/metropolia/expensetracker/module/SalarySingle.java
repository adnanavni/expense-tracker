package fi.metropolia.expensetracker.module;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SalarySingle {
    private static SalarySingle INSTANCE = null;
    private double daySalary;
    private double monthSalary;
    private double taxRate;
    private double monthSalaryMinusTaxes;
    private double daySalaryMinusTaxes;
    private double salaryMinusTaxes;
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

    private SalarySingle() {
    }

    public static SalarySingle getInstance() {
        if (INSTANCE == null) INSTANCE = new SalarySingle();
        return INSTANCE;
    }

    public double calculateSalaryWithTaxRate(double taxRate, double salary, String type) {

        this.taxRate = taxRate;
        setTaxrate(this.taxRate);
        double percent = 1 - (this.taxRate / 100);
        double wantedSalaryminusTaxes = 0;
        if (type.equals("DAY")) {
            this.daySalaryMinusTaxes = (salary * percent);
            setDaySalaryMinusTaxes(this.daySalaryMinusTaxes);
            setSalaryMinusTaxes((salary * percent));

            setDaySalary(salary);
            wantedSalaryminusTaxes = this.daySalaryMinusTaxes;
        }
        if (type.equals("MONTH")) {
            this.monthSalaryMinusTaxes = (salary * percent);
            setMonthSalaryMinusTaxes(this.monthSalaryMinusTaxes);
            setSalaryMinusTaxes((salary * percent));
            setMonthSalary(salary);
            wantedSalaryminusTaxes = this.monthSalaryMinusTaxes;
        }
        return wantedSalaryminusTaxes;
    }

    public void setSalaryMinusTaxes(double salary) {
        this.salaryMinusTaxes = salary;

    }

    public void CalculateDaySalary(double hours, double hourSalary) {
        this.daySalary = (hours * hourSalary);
    }

    public void setTaxrate(double taxrate) {
        this.taxRate = taxRate;
    }

    public double getDaySalary() {

        return this.daySalary;
    }

    public void setDaySalary(double daySalary) {
        this.daySalary = daySalary;
    }

    public double getDaySalaryMinusTaxes() {

        return this.daySalaryMinusTaxes;
    }

    public void setDaySalaryMinusTaxes(double daySalaryMinusTaxes) {
        this.daySalaryMinusTaxes = daySalaryMinusTaxes;
    }

    public double getMonthSalaryMinusTaxes() {
        return this.monthSalaryMinusTaxes;
    }

    public void setMonthSalaryMinusTaxes(double monthSalaryMinusTaxes) {
        this.monthSalaryMinusTaxes = monthSalaryMinusTaxes;
    }

    public double getMonthSalary() {
        return this.monthSalary;
    }

    public void setMonthSalary(double monthSalary) {
        this.monthSalary = monthSalary;
    }

    public ArrayList<String> getMonths() {
        return new ArrayList<>(months.keySet());
    }

    public double geTotalSalaryOfMonth(String m, String type) throws ParseException {
        int year = LocalDate.now().getYear();
        int beforeYear = (year - 1);
        int nextYear = (year + 1);
        double totalSalary = 0;
        switch (m) {
            case ("January"):
                totalSalary = getSalariesBetweenStartAndFinish("31/12/" + beforeYear, "01/02/" + year, type);
                break;
            case ("February"):
                totalSalary = getSalariesBetweenStartAndFinish("31/01/" + year, "01/03/" + year, type);
                break;
            case ("March"):
                totalSalary = getSalariesBetweenStartAndFinish("28/02/" + year, "01/04/" + year, type);
                break;
            case ("April"):
                totalSalary = getSalariesBetweenStartAndFinish("31/03/" + year, "01/05/" + year, type);
                break;
            case ("May"):
                totalSalary = getSalariesBetweenStartAndFinish("30/04/" + year, "01/06/" + year, type);
                break;
            case ("June"):
                totalSalary = getSalariesBetweenStartAndFinish("31/05/" + year, "01/07/" + year, type);
                break;
            case ("July"):
                totalSalary = getSalariesBetweenStartAndFinish("30/06/" + year, "01/08/" + year, type);
                break;
            case ("August"):
                totalSalary = getSalariesBetweenStartAndFinish("31/07/" + year, "01/09/" + year, type);
                break;
            case ("September"):
                totalSalary = getSalariesBetweenStartAndFinish("31/08/" + year, "01/10/" + year, type);
                break;
            case ("October"):
                totalSalary = getSalariesBetweenStartAndFinish("30/9/" + year, "01/11/" + year, type);
                break;
            case ("November"):
                totalSalary = getSalariesBetweenStartAndFinish("31/10/" + year, "01/12/" + year, type);
                break;
            case ("December"):
                totalSalary = getSalariesBetweenStartAndFinish("30/11/" + year, "01/01/" + nextYear, type);
                break;
            default:
                return 0;
        }
        return totalSalary;
    }

    public double getSalariesBetweenStartAndFinish(String start, String end, String type) throws ParseException {
        IncomeDao incomeDao = new IncomeDao();
        ArrayList<Double> salaries = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date parsedStart = sdf.parse(start);
        Date parsedEnd = sdf.parse(end);
        double salariesTogether = 0;

        for (Salary eachDate : incomeDao.getSalaries(Variables.getInstance().getLoggedUserId(), type)) {
            if (eachDate.getDate().after(parsedStart) && eachDate.getDate().before(parsedEnd)) {
                salaries.add(eachDate.getSalaryMinusTaxes(type));
            }
        }
        for (double eachSalary : salaries) {
            salariesTogether += eachSalary;
        }
        return salariesTogether;
    }
}
