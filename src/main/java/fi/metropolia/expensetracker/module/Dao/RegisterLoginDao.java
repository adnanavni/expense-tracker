package fi.metropolia.expensetracker.module.Dao;

import fi.metropolia.expensetracker.datasource.MariaDBConnector;
import fi.metropolia.expensetracker.module.PsswdAuth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**

 The RegisterLoginDao class provides methods to interact with the UserInfo table in the database,

 allowing for user registration and login.
 */
public class RegisterLoginDao {

    private static final String INSERT_QUERY = "INSERT INTO UserInfo (username, password) VALUES (?, ?)";
    private static final String SELECT_QUERY = "SELECT * FROM UserInfo WHERE username = ?";
    private final Connection conn = MariaDBConnector.getInstance();

    /**
     Prints detailed error messages in the console for SQLExceptions.
     @param ex the SQLException object containing the error information
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

     Inserts a new user record with the provided username and password.
     @param username the desired username for the new user
     @param password the password for the new user
     */
    public void insertRecord(String username, String password) {
        try {

            PreparedStatement preparedStatement = conn.prepareStatement(INSERT_QUERY);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    /**

     Retrieves the password of the user with the provided username.
     @param username the username of the desired user
     @return the password of the user as a String, or null if the user does not exist
     @throws SQLException if there is an error executing the SQL query
     */
    public String getPassword(String username) throws SQLException {

        PreparedStatement prepPsswordState = conn.prepareStatement("SELECT password FROM UserInfo WHERE username=?");
        prepPsswordState.setString(1, username);
        ResultSet resultSet = prepPsswordState.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString(1);
        }
        resultSet.close();
        prepPsswordState.close();

        return null;
    }

    /**

     Validates the provided username and password against the database records.
     @param username the provided username
     @param password the provided password
     @return true if the username and password are valid, false otherwise
     */
    public boolean validate(String username, String password) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(SELECT_QUERY);
            preparedStatement.setString(1, username);
            PsswdAuth auth = new PsswdAuth();

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next() && auth.authenticate(password.toCharArray(), getPassword(username))) {
                return true;
            }
            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            printSQLException(e);
        }
        return false;
    }
    /**

     Retrieves the ID of the user with the provided username.
     @param name the username of the desired user
     @return the ID of the user as an Integer, or null if the user does not exist
     */
    public Integer loggedID(String name) {

        try {
            String sql = "SELECT id FROM UserInfo WHERE username = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            printSQLException(e);
        }

        return null;
    }
    /**
     * Check if the username already exists at the database
     * @param username - name from which is checked matches from database
     * @return true if username exists,
     * @return false if username don't exists.
     * */

    public boolean userExists(String username) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM UserInfo WHERE username = ?");
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();

        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);
        }
        if (count > 0) {
            // The username already exists
            return true;
        } else {
            return false;
        }
    }

}
