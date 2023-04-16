package fi.metropolia.expensetracker.module.Dao;

import fi.metropolia.expensetracker.datasource.MariaDBConnector;
import fi.metropolia.expensetracker.module.Budget;
import fi.metropolia.expensetracker.module.ConstantExpense;
import fi.metropolia.expensetracker.module.Expense;
import fi.metropolia.expensetracker.module.PsswdAuth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class Dao {

    private static final String INSERT_QUERY = "INSERT INTO Registration (username, password) VALUES (?, ?)";
    private static final String SELECT_QUERY = "SELECT * FROM Registration WHERE username = ?";
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

        PreparedStatement prepPsswordState = conn.prepareStatement("SELECT password FROM Registration WHERE username=?");
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
            String sql = "SELECT id FROM Registration WHERE username = ?";
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
        PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM Registration WHERE username = ?");
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

    public boolean deleteUser(String username) throws SQLException {

        if (userExists(username)) {
            try {
                String sql = "DELETE FROM Registration WHERE username = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, username);
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



}
