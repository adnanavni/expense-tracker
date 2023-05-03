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

public class BudgetExpenseDao {

    private final Connection conn = MariaDBConnector.getInstance();

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

    public ConstantExpense getConstantExpenseByName(String name, Integer id) {

        String str = name;
        String firstLetter = str.substring(0, 1).toUpperCase();
        String result = firstLetter + str.substring(1);

        if (result.equals("Waterbill")) result = "Water bill";
        if (result.equals("Carpayment"))  result = "Car payment";
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
