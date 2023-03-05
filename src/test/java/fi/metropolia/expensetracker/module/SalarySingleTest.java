package fi.metropolia.expensetracker.module;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SalarySingleTest {

    @Test
    void geTotalSalaryOfMonth() {

    }

    @Test
    void calculateDaySalary() {
        SalarySingle.getInstance().calculateDaySalary(8,12);
    }
}