package fi.metropolia.expensetracker.module;

import fi.metropolia.expensetracker.datasource.MariaDBConnector;

import java.sql.Timestamp;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class Login_Signup_Dao {

    private static final String INSERT_QUERY = "INSERT INTO Registration (username, password) VALUES (?, ?)";
    private static final String SELECT_QUERY = "SELECT * FROM Registration WHERE username = ? and password = ?";

    private final Connection conn = MariaDBConnector.getInstance();

    public void insertRecord(String fullName, String password) throws SQLException {

        try {

             PreparedStatement preparedStatement = conn.prepareStatement(INSERT_QUERY);
             preparedStatement.setString(1, fullName);
             preparedStatement.setString(2, password);

            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    public boolean validate(String fullName, String password) throws SQLException {

        try {

             PreparedStatement preparedStatement = conn.prepareStatement(SELECT_QUERY);
            preparedStatement.setString(1, fullName);
            preparedStatement.setString(2, password);

            System.out.println(preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }


        }  catch (SQLException e) {
            printSQLException(e);
        }
        return false;
    }

    public Integer loggedID(String name, String password){
        try  {

            String sql = "SELECT id FROM Registration WHERE username = ? and password = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);

            System.out.println(preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);

            }
            resultSet.close();
            preparedStatement.close();


        }  catch (SQLException e) {
            printSQLException(e);
        }

        return null;
    }

    public String loggedCurrency(Integer id){
        try  {

            String sql = "SELECT currency FROM Registration WHERE id=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setInt(1, id);

            System.out.println(preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);

            }
            resultSet.close();
            preparedStatement.close();


        }  catch (SQLException e) {
            printSQLException(e);
        }

        return null;
    }

    public Budget getBudget(Integer id){
        try  {

            String sql = "SELECT * FROM Budgets WHERE BudgetId = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setInt(1, id);

            System.out.println(preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Budget(resultSet.getInt(1), resultSet.getDouble(3), resultSet.getString(2));
            }
            resultSet.close();
            preparedStatement.close();


        }  catch (SQLException e) {
            printSQLException(e);
        }

        return null;
    }
    public Expense getExpense(Integer id){
        try  {

            String sql = "SELECT * FROM Expenses WHERE ExpenseId = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setInt(1, id);

            System.out.println(preparedStatement);


            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {

                return new Expense(resultSet.getInt(1), resultSet.getDouble(3), resultSet.getString(2),
                       resultSet.getDate(5));
            }
            resultSet.close();
            preparedStatement.close();


        }  catch (SQLException e) {
            printSQLException(e);
        }

        return null;
    }

    public boolean deleteExpense(Integer id){
        Expense expense = getExpense(id);

        if(expense != null){
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

        }
        else {
            return false;
        }
    }

    public void saveBudget(Integer id, String name, Double money)  {

        try {

            String sql = "INSERT INTO Budgets VALUES (NULL, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, money);
            preparedStatement.setInt(3, id);

            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    public void saveExpense(Integer id, String type, Double money, Date date)  {

        try {

            String sql = "INSERT INTO Expenses VALUES (NULL, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, type);
            preparedStatement.setDouble(2, money);
            preparedStatement.setInt(3, id);


            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            preparedStatement.setDate(4, sqlDate);

            System.out.println(preparedStatement);
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

    public Expense[] getExpenses(Integer id){
        String sql = "SELECT * FROM Expenses WHERE BudgetId = ?";
        Expense[] result;
        ArrayList<Expense> expenses = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Expense expense = new Expense(rs.getInt(1), rs.getDouble(3), rs.getString(2),
                        rs.getDate(5));
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



    public boolean changeUserCurrency(Integer id, String currency){
        try {
            String sql = "UPDATE Registration SET currency = ? WHERE id= ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, currency);
            ps.setInt(2, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            printSQLException(e);
        }
        return false;
    }

    public boolean changeExpenseMoney(Integer id, Double money){
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

    public boolean changeBudgetMoney(Integer id, Double money){
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

    public static void printSQLException(SQLException ex) {
        for (Throwable e: ex) {
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
}
