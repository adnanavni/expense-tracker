package fi.metropolia.expensetracker.module.Dao;

import fi.metropolia.expensetracker.datasource.MariaDBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class represents the data access object for the Settings feature.
 * It provides methods to change and retrieve user settings such as age, language, currency, theme color.
 * It also provides a method to delete all user data.
 */
public class SettingsDao {
    /**
     * The connection instance used to interact with the database.
     */
    private final Connection conn = MariaDBConnector.getInstance();

    /**
     * Prints the stack trace and error information for the given SQLException and its causes.
     *
     * @param ex the SQLException to print information for
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
     * Changes the user's currency in the database.
     *
     * @param id       The user's id.
     * @param currency The new currency.
     * @return True if the currency was successfully updated, false otherwise.
     */
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

    /**
     * Retrieves the user's age from the database.
     *
     * @param id The user's id.
     * @return The user's age if it exists in the database, null otherwise.
     * @throws SQLException If an error occurs while executing the SQL statement.
     */
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

    /**
     * Changes the user's language in the database.
     *
     * @param id       The user's id.
     * @param language The new language.
     * @return True if the language was successfully updated, false otherwise.
     */
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

    /**
     * Retrieves the user's language from the database.
     *
     * @param id The user's id.
     * @return The user's language if it exists in the database, null otherwise.
     */
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

    /**
     * Updates the age of a user in the database.
     *
     * @param id  the id of the user whose age will be updated
     * @param age the new age to be set for the user
     * @return true if the age was updated successfully, false otherwise
     */
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

    /**
     * A method to retrieve the current theme color for a given user.
     *
     * @param id the unique ID of the user to retrieve the theme color for.
     * @return a String representing the current theme color of the user, or null if the user does not exist.
     */
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

    /**
     * A method to change the theme color for a given user.
     *
     * @param id    the unique ID of the user to change the theme color for.
     * @param color the new color to set as the user's theme color.
     * @return a boolean indicating whether the theme color was successfully changed.
     */
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

    /**
     * A method to delete all user data associated with a given user ID.
     *
     * @param userId the unique ID of the user to delete all data for.
     */
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
