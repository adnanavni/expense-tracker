package fi.metropolia.expensetracker.module.Dao;

import fi.metropolia.expensetracker.datasource.MariaDBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsDao {

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

    public boolean changeUserCurrency(Integer id, String currency) {

        try {
            String sql = "UPDATE UserInfo SET currency = ? WHERE id= ?";
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

    public Integer getAge(int id) throws SQLException {

        PreparedStatement preparedStatement = conn.prepareStatement("SELECT Age FROM UserInfo WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        resultSet.close();
        preparedStatement.close();

        return null;
    }


    public boolean setLanguage(int id, String language) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(" UPDATE UserInfo SET Language = ? WHERE id = ? ");
            preparedStatement.setString(1, language);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            printSQLException(e);
        }
        return false;
    }

    public String getLanguage(int id) {
        try {

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT Language from UserInfo WHERE id=?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return resultSet.getString(1);
        } catch (SQLException e) {
            printSQLException(e);
        }
        return null;

    }

    public boolean setAge(int id, int age) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE UserInfo SET Age = ? WHERE id = ?");
            preparedStatement.setInt(1, age);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            printSQLException(e);
        }
        return false;
    }

    public String loggedThemeColor(Integer id) {

        try {
            String sql = "SELECT ThemeColor FROM UserInfo WHERE id = ?";
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

    public boolean changeUserThemeColor(Integer id, String color) {
        try {
            String sql = "UPDATE UserInfo SET ThemeColor = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, color);
            ps.setInt(2, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            printSQLException(e);
        }
        return false;
    }

    public void deleteUserData(Integer userId) {
        String deleteIncomes = "DELETE FROM Incomes WHERE UserID = ?";
        String deleteConstantExpenses = "DELETE FROM Constantexpenses WHERE registration_id = ?";
        String deleteBudgets = "DELETE FROM Budgets WHERE registration_id = ?";
        String deleteExpenses = "DELETE FROM Expenses WHERE BudgetId IN (SELECT BudgetId FROM Budgets WHERE registration_id = ?)";
        String updateUserInfoSql = "UPDATE UserInfo SET ThemeColor = '#85bb65', currency = 'EUR' WHERE id = ?";

        try {
            try (PreparedStatement stmt = conn.prepareStatement(deleteIncomes)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(deleteConstantExpenses)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(deleteBudgets)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(deleteExpenses)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(updateUserInfoSql)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
