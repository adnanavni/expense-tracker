package fi.metropolia.expensetracker.module;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class SalaryTest {
    private Salary salary = new Salary();
    private final double DELTA = 0.01;

    @Test
    @DisplayName("Testaa prosenttilasku palkasta")
    void getSalaryMinusTaxes() {
        salary.setTaxRate(2.32);
        salary.setSalary(10.0);

        String salaryAmount = String.format("%.2f", salary.getSalaryMinusTaxes( "MONTH"));
        assertEquals(9.77, Double.parseDouble(String.format(salaryAmount.replace(",", "."))),  DELTA, "Percent calculation wrong");

    }
}