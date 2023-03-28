package fi.metropolia.expensetracker.module;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class VariablesTest {
    private final Variables variables = Variables.getInstance();
    private final double DELTA = 0.001;
    @BeforeEach
    public void testResetAll(){
        variables.resetAll();
        assertNull(variables.getLoggedUserId(), "Logged user reset failed!");
        assertEquals(1.00, variables.getCurrentCourseMultiplier(), DELTA, "Course multiplier reset failed!");
        assertEquals("EUR", variables.getCurrentCurrency(), "Currency reset failed!");
        assertNull(variables.getActiveBudget(), "Active budget reset failed!");
        assertEquals(0, variables.getBudgets().size(), "Budgets reset failed!");
        assertEquals(0, variables.getBudget(), DELTA, "Total budget reset failed!");
        assertEquals(0, variables.getConstantExpenseArray().size(), "Constant expenses reset failed!");
    }


    @Test
    public void testBudgetMethods(){
        Budget newBudget = new Budget(1.0, "Budget");
        variables.createNewBudget(newBudget);
        assertEquals(1, variables.getBudgets().size(), "Budget adding or getBudgets failed!");
        assertNull(variables.getActiveBudget(), "Active budget should be NULL before setting! Or getActiveBudget failed!");
        variables.setActiveBudget("Budget");
        assertEquals("Budget", variables.getActiveBudget().getName(), "Active budget set or get failed!");
        variables.modifyBudget("newName", 2.0);
        assertEquals("newName", variables.getActiveBudget().getName(), "Active budget modifying failed!");
        ArrayList<Budget> budgetNames = variables.getBudgets();
        assertEquals("newName", budgetNames.get(0).getName(), "Get budget names failed!");
        Budget secondBudget = new Budget(1.0, "Budget2");
        variables.createNewBudget(secondBudget);
        assertEquals(3.0, variables.getBudget(), DELTA, "Get total budget failed!");
        variables.setActiveBudget("Budget2");
        variables.deleteBudget();
        assertEquals(1, variables.getBudgets().size(), "Budget deletion failed!");
        variables.resetBudgets();
        assertEquals(0, variables.getBudgets().size(), "Budget reset failed!");
    }

    @Test
    public void testCurrencyMethods(){
        double testExchangeGet = 0.0;
        try {
            testExchangeGet = variables.getCurrencyExchangeRateViaGETRequest("EUR");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(1, testExchangeGet, DELTA, "Currency exchange rate get failed!");
        variables.setLoggedCurrency("USD");
        assertEquals("USD", variables.getCurrentCurrency(), "Currency set failed!");
        try {
            assertEquals(variables.getCurrencyExchangeRateViaGETRequest("USD"), variables.getCurrentCourseMultiplier(), DELTA, "Course multiplier set failed!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testConstantExpenseMethods(){
        ConstantExpense newConstExp = new ConstantExpense(1, "NewExp", 1.0);
        variables.addConstantExpense(newConstExp);
        assertEquals(1, variables.getConstantExpenseArray().size(), "Constant expense adding failed!");
        assertEquals(true, variables.constantExpenseNameExists("NewExp"), "Constant expense name already exists check failed!");
        variables.removeConstantExpense(newConstExp);
        assertEquals(0, variables.getConstantExpenseArray().size(), "Constant expense removing failed!");
    }

}
