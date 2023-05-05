package fi.metropolia.expensetracker.module;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * SalarySingle is a singleton-class which keeps the data of different salaries
 */
public class SalarySingle {
    private static SalarySingle INSTANCE = null;
    private double daySalary;
    private double monthSalary;
    private double taxRate;
    private double monthSalaryMinusTaxes;
    private double daySalaryMinusTaxes;
    private double salaryMinusTaxes;
    private int age;
    LocalizationManager localizationManager = LocalizationManager.getInstance();

    private ArrayList<Salary> daySalaries = new ArrayList<>();
    private ArrayList<Salary> monthSalaries = new ArrayList<>();
    public LinkedHashMap<String, Integer> months;


    public void refreshMonthsCombLanguage() {
        months = new LinkedHashMap<>() {{
            put(localizationManager.getString("january"), 0);
            put(localizationManager.getString("february"), 1);
            put(localizationManager.getString("march"), 2);
            put(localizationManager.getString("april"), 3);
            put(localizationManager.getString("may"), 4);
            put(localizationManager.getString("june"), 5);
            put(localizationManager.getString("july"), 6);
            put(localizationManager.getString("august"), 7);
            put(localizationManager.getString("september"), 8);
            put(localizationManager.getString("october"), 9);
            put(localizationManager.getString("november"), 10);
            put(localizationManager.getString("december"), 11);
        }};
    }

    private SalarySingle() {
    }

    /**
     * Creates singleton instance if it hasn't been done before, otherwise just returns created instance
     * */
    public static SalarySingle getInstance() {
        if (INSTANCE == null) INSTANCE = new SalarySingle();
        return INSTANCE;
    }
    /**
     * Calculates given salaryamount with given taxrate.
     * @param taxRate user's given taxrate
     * @param salary user salary without taken the taxes off
     * @param type defines is the salary day-salary or a month salary.
     * @return salaryamoount minus taxrate.
     * */
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
    /**
     * Calculates day salary based on worked hours and hour salary
     * @param hours worked hours
     * @param hourSalary rate paid per hour of work
    * */
    public void calculateDaySalary(double hours, double hourSalary) {
        this.daySalary = (hours * hourSalary);
    }

    public void setTaxrate(double taxRate) {
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

    public ArrayList<Salary> getDaySalaries() {
        return daySalaries;
    }

    public ArrayList<Salary> getMonthSalaries() {
        return monthSalaries;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Returns sum of all gotten salaries in a selected month. Uses getSalariesBetweenStartAndFinish-method to calculate the sum of salaries.
     * @param monthNumber tells the selected month of the year
     * @param type tells are the calculated salaries month-salaries or day-salaried.
     * @return sum of the salaries of all month
     * */
    public double geTotalSalaryOfMonth(int monthNumber, String type) throws ParseException {
        int year = LocalDate.now().getYear();
        int beforeYear = (year - 1);
        int nextYear = (year + 1);
        double totalSalary = 0;

        switch (monthNumber) {
            case (0):
                totalSalary = getSalariesBetweenStartAndFinish("31/12/" + beforeYear, "01/02/" + year, type);
                break;
            case (1):
                totalSalary = getSalariesBetweenStartAndFinish("31/01/" + year, "01/03/" + year, type);
                break;
            case (2):
                totalSalary = getSalariesBetweenStartAndFinish("28/02/" + year, "01/04/" + year, type);
                break;
            case (3):
                totalSalary = getSalariesBetweenStartAndFinish("31/03/" + year, "01/05/" + year, type);
                break;
            case (4):
                totalSalary = getSalariesBetweenStartAndFinish("30/04/" + year, "01/06/" + year, type);
                break;
            case (5):
                totalSalary = getSalariesBetweenStartAndFinish("31/05/" + year, "01/07/" + year, type);
                break;
            case (6):
                totalSalary = getSalariesBetweenStartAndFinish("30/06/" + year, "01/08/" + year, type);
                break;
            case (7):
                totalSalary = getSalariesBetweenStartAndFinish("31/07/" + year, "01/09/" + year, type);
                break;
            case (8):
                totalSalary = getSalariesBetweenStartAndFinish("31/08/" + year, "01/10/" + year, type);
                break;
            case (9):
                totalSalary = getSalariesBetweenStartAndFinish("30/9/" + year, "01/11/" + year, type);
                break;
            case (10):
                totalSalary = getSalariesBetweenStartAndFinish("31/10/" + year, "01/12/" + year, type);
                break;
            case (11):
                totalSalary = getSalariesBetweenStartAndFinish("30/11/" + year, "01/01/" + nextYear, type);
                break;
            default:
                return 0;
        }
        return totalSalary;
    }

    /**
     * Calculates together the salaries together between given dates
     * @param start day of a month
     * @param end day of a month
     * @return sum of salaries between given start and end date.
     * */
    public double getSalariesBetweenStartAndFinish(String start, String end, String type) throws ParseException {
        ArrayList<Double> salaries = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date parsedStart = sdf.parse(start);
        Date parsedEnd = sdf.parse(end);
        double salariesTogether = 0;

        if (type == "MONTH") {
            for (Salary eachDate : getMonthSalaries()) {
                if (eachDate.getDate().after(parsedStart) && eachDate.getDate().before(parsedEnd)) {
                    salaries.add(eachDate.getSalaryMinusTaxes());
                }
            }
        }
        if (type == "DAY") {
            for (Salary eachDate : getDaySalaries()) {
                if (eachDate.getDate().after(parsedStart) && eachDate.getDate().before(parsedEnd)) {
                    salaries.add(eachDate.getSalaryMinusTaxes());
                }
            }
        }
        for (double eachSalary : salaries) {
            salariesTogether += eachSalary;
        }
        return salariesTogether;
    }

    /**
     * Clear all values of this singleton when user is logging out or if the user has deleted all own values.
     * */
    public void resetAll() {
        monthSalaries.clear();
        daySalaries.clear();
    }

}
