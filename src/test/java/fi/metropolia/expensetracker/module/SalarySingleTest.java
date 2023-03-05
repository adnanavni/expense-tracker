package fi.metropolia.expensetracker.module;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SalarySingleTest {
    @Test
    @DisplayName("Day salary calculation")
    void calculateDaySalary() {
        SalarySingle.getInstance().calculateDaySalary(8,12);
        assertEquals(96, SalarySingle.getInstance().getDaySalary(), "Day salary calculated wrong");
    }
}