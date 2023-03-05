package fi.metropolia.expensetracker.module;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class SalaryTest {
    private Salary salary = new Salary();

    @Test
    @DisplayName("Testaa prosenttilasku palkasta")
    void getSalaryMinusTaxes() {
        salary.setTaxRate(2);
        salary.setSalary(10.0);

        assertEquals(9.8, salary.getSalaryMinusTaxes("MONTH"), "Prosenttilasku 2% from 10euro wrong");

    }
}