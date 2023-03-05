package fi.metropolia.expensetracker.module;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ConstantExpenseTest {
    private ConstantExpense expense = new ConstantExpense();

    @Test
    void getConstantExpense() {
        expense.setAmount(10.0);

        assertEquals(10.0, expense.getAmount(), "Constant get expense getter wrong");

    }
}