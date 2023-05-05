package fi.metropolia.expensetracker.module.Dao;

import fi.metropolia.expensetracker.datasource.MariaDBConnector;
import fi.metropolia.expensetracker.module.Budget;
import fi.metropolia.expensetracker.module.ConstantExpense;
import fi.metropolia.expensetracker.module.Expense;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * BudgetExpenseDao is a class that provides methods for accessing
 * and manipulating data related to budgets and expenses in the database.
 */
public class BudgetExpenseDao {

    /**
     * A private final field representing a connection to a MariaDB database,
     * obtained through a singleton instance of the MariaDBConnector class.
     */
    private final Connection conn = MariaDBConnector.getInstance();

    /**
     * This method is used to print the details of any SQLException that occurred while executing a SQL statement.
     * It prints the SQLState, Error Code, and Message of the SQLException, and also prints the cause of the exception if any.
     *
     * @param ex The SQLException to be printed
     */
    public static void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }

    /**
     * Returns the currency of the user with the specified ID from the database.
     *
     * @param id the ID of the user whose currency to retrieve
     * @return the currency of the user with the specified ID, or null if the user cannot be found or an error occurs
     */
    public String loggedCurrency(Integer id) {
        try {
            String sql = "SELECT currency FROM UserInfo WHERE id=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            printSQLException(e);
        }

        return null;
    }

    /**
     * Returns the budget with the specified ID from the database.
     *
     * @param id the ID of the budget to retrieve
     * @return the budget with the specified ID, or null if the budget cannot be found or an error occurs
     */
    public Budget getBudget(Integer id) {
        try {

            String sql = "SELECT * FROM Budgets WHERE BudgetId = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Budget(resultSet.getInt(1), resultSet.getDouble(3), resultSet.getString(2));
            }
            resultSet.close();
            preparedStatement.close();


        } catch (SQLException e) {
            printSQLException(e);
        }

        return null;
    }

    /**
     * Returns the constant expense with the specified name and user ID from the database.
     *
     * @param name the name of the constant expense to retrieve
     * @param id   the ID of the user whose constant expense to retrieve
     * @return the constant expense with the specified name and user ID, or null if the constant expense cannot be found or an error occurs
     */
    public ConstantExpense getConstantExpenseByName(String name, Integer id) {

        String str = name;
        String firstLetter = str.substring(0, 1).toUpperCase();
        String result = firstLetter + str.substring(1);

        if (result.equals("Waterbill")) result = "Water bill";
        if (result.equals("Carpayment")) result = "Car payment";
        if (result.equals("Cellphone")) result = "Cell phone";

        try {

            String sql = "SELECT * FROM Constantexpenses WHERE Title = ? AND registration_id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setString(1, result);
            preparedStatement.setInt(2, id);


            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {

                return new ConstantExpense(resultSet.getInt(1), resultSet.getString(2), resultSet.getDouble(3));
            }
            resultSet.close();
            preparedStatement.close();


        } catch (SQLException e) {
            printSQLException(e);
        }

        return null;
    }

    /**
     * Retrieves an Expense from the database based on its ID.
     *
     * @param id the ID of the Expense to retrieve.
     * @return the Expense object with the specified ID, or null if no such Expense exists.
     */
    public Expense getExpense(Integer id) {
        try {

            String sql = "SELECT * FROM Expenses WHERE ExpenseId = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {

                return new Expense(resultSet.getInt(1), resultSet.getDouble(3), resultSet.getString(2), resultSet.getDate(5));
            }
            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            printSQLException(e);
        }

        return null;
    }

    /**
     * Deletes an Expense from the database based on its ID.
     *
     * @param id the ID of the Expense to delete.
     * @return true if the Expense was successfully deleted, false otherwise.
     */
    public boolean deleteExpense(Integer id) {
        Expense expense = getExpense(id);

        if (expense != null) {
            try {
                String sql = "DELETE FROM Expenses WHERE ExpenseId = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
                return true;
            } catch (SQLException e) {
                printSQLException(e);
                return false;
            }

        } else {
            return false;
        }
    }

    /**
     * Saves a ConstantExpense to the database.
     *
     * @param id    the ID of the registration.
     * @param name  the name of the ConstantExpense.
     * @param money the amount of money spent on the ConstantExpense.
     */
    public void saveConstantExpense(Integer id, String name, Double money) {
        try {

            String sql = "INSERT INTO Constantexpenses VALUES (NULL, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, money);
            preparedStatement.setInt(3, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    /**
     * Saves a budget in the database.
     *
     * @param id    The user's registration id.
     * @param name  The name of the budget.
     * @param money The amount of money allocated for the budget.
     */
    public void saveBudget(Integer id, String name, Double money) {

        try {
            String sql = "INSERT INTO Budgets VALUES (NULL, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, money);
            preparedStatement.setInt(3, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    /**
     * @param id    The user's registration id.
     * @param type  The type of expense.
     * @param money The amount of money spent.
     * @param date  The date the expense was made.
     */
    public void saveExpense(Integer id, String type, Double money, Date date) {

        try {
            String sql = "INSERT INTO Expenses VALUES (NULL, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, type);
            preparedStatement.setDouble(2, money);
            preparedStatement.setInt(3, id);


            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            preparedStatement.setDate(4, sqlDate);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    /**
     * Retrieves all budgets for a given user.
     *
     * @param id The user's registration id.
     * @return An array of Budget objects representing the user's budgets.
     */
    public Budget[] getBudgets(Integer id) {

        String sql = "SELECT * FROM Budgets WHERE registration_id = ?";
        Budget[] result;
        ArrayList<Budget> budgets = new ArrayList<>();

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Budget budget = new Budget(rs.getInt(1), rs.getDouble(3), rs.getString(2));
                budgets.add(budget);
            }

        } catch (SQLException e) {
            printSQLException(e);
        }

        result = new Budget[budgets.size()];
        for (int i = 0; i < budgets.size(); i++) {
            result[i] = budgets.get(i);
        }
        return result;

    }

    /**
     * Retrieves all constant expenses for a given user.
     *
     * @param id The user's registration id.
     * @return An array of ConstantExpense objects representing the user's constant expenses.
     */
    public ConstantExpense[] getConstantExpenses(Integer id) {

        String sql = "SELECT * FROM Constantexpenses WHERE registration_id = ?";
        ConstantExpense[] result;
        ArrayList<ConstantExpense> constantExpenses = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ConstantExpense constantExpense = new ConstantExpense(rs.getInt(1), rs.getString(2), rs.getDouble(3));
                constantExpenses.add(constantExpense);
            }

        } catch (SQLException e) {
            printSQLException(e);
        }

        result = new ConstantExpense[constantExpenses.size()];
        for (int i = 0; i < constantExpenses.size(); i++) {
            result[i] = constantExpenses.get(i);
        }
        return result;

    }

    /**
     * Returns an array of Expense objects associated with the given budget id.
     *
     * @param id the id of the budget to retrieve expenses from.
     * @return an array of Expense objects associated with the given budget id.
     */
    public Expense[] getExpenses(Integer id) {
        String sql = "SELECT * FROM Expenses WHERE BudgetId = ?";
        Expense[] result;
        ArrayList<Expense> expenses = new ArrayList<>();

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Expense expense = new Expense(rs.getInt(1), rs.getDouble(3), rs.getString(2), rs.getDate(5));
                expenses.add(expense);
            }

        } catch (SQLException e) {
            printSQLException(e);
        }

        result = new Expense[expenses.size()];
        for (int i = 0; i < expenses.size(); i++) {
            result[i] = expenses.get(i);
        }
        return result;
    }

    /**
     * Changes the amount of a constant expense identified by the given id.
     *
     * @param id    the id of the constant expense to change.
     * @param money the new amount of the constant expense.
     * @return true if the constant expense was successfully updated, false otherwise.
     */
    public boolean changeConstantExpenseValue(Integer id, Double money) {
        try {
            String sql = "UPDATE Constantexpenses SET Amount = ? WHERE ConstantexpenseId = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, money);
            ps.setInt(2, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            printSQLException(e);
        }
        return false;
    }

    /**
     * Changes the amount of an expense identified by the given id.
     *
     * @param id    the id of the expense to change.
     * @param money the new amount of the expense.
     * @return true if the expense was successfully updated, false otherwise.
     */
    public boolean changeExpenseMoney(Integer id, Double money) {
        try {
            String sql = "UPDATE Expenses SET Money = ? WHERE ExpenseId = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, money);
            ps.setInt(2, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            printSQLException(e);
        }
        return false;
    }

    /**
     * Changes the amount of a budget identified by the given id.
     *
     * @param id    the id of the budget to change.
     * @param money the new amount of the budget.
     * @return true if the budget was successfully updated, false otherwise.
     */
    public boolean changeBudgetMoney(Integer id, Double money) {

        try {
            String sql = "UPDATE Budgets SET Money = ? WHERE BudgetId = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, money);
            ps.setInt(2, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            printSQLException(e);
        }
        return false;
    }

    /**
     * Modifies a budget identified by its original name, setting a new name and amount.
     *
     * @param ogName the original name of the budget to modify.
     * @param money  the new amount of the budget.
     * @param name   the new name of the budget.
     * @return true if the budget was successfully modified, false otherwise.
     */
    public boolean ModifyBudget(String ogName, Double money, String name) {

        try {
            String sql = "UPDATE Budgets SET Money = ?, BudgetName = ? WHERE BudgetName = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, money);
            ps.setString(2, name);
            ps.setString(3, ogName);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            printSQLException(e);
        }
        return false;
    }

    /**
     * Deletes a budget identified by the given id.
     *
     * @param id the id of the budget to delete.
     * @return true if the budget was successfully deleted, false otherwise.
     */
    public boolean deleteBudget(Integer id) {
        Budget budget = getBudget(id);

        if (budget != null) {
            try {
                String sql = "DELETE FROM Budgets WHERE BudgetId = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
                return true;
            } catch (SQLException e) {
                printSQLException(e);
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Returns a HashMap with the name and amount of all expenses associated with the budget id.
     *
     * @param id the id of the budget to retrieve expenses from.
     * @return a HashMap with the name and amount of all expenses associated with the budget id.
     */
    public HashMap<String, Double> getExpenseNameAndAmount(Integer id) {
        String sql = "SELECT * FROM Expenses WHERE BudgetId = ?";
        HashMap<String, Double> result = new HashMap<>();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Expense expense = new Expense(rs.getInt(1), rs.getDouble(3), rs.getString(2), rs.getDate(5));
                result.put(rs.getString(2), rs.getDouble(3));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return result;
    }
}
