package fi.metropolia.expensetracker.module.Dao;

import fi.metropolia.expensetracker.datasource.MariaDBConnector;
import fi.metropolia.expensetracker.module.PsswdAuth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterLoginDao {

    private static final String INSERT_QUERY = "INSERT INTO UserInfo (username, password) VALUES (?, ?)";
    private static final String SELECT_QUERY = "SELECT * FROM UserInfo WHERE username = ?";
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
