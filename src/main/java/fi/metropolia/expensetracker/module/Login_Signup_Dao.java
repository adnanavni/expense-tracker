package fi.metropolia.expensetracker.module;

import fi.metropolia.expensetracker.datasource.MariaDBConnector;

import java.sql.*;

public class Login_Signup_Dao {

    private static final String INSERT_QUERY = "INSERT INTO registration (full_name, email_id, password) VALUES (?, ?, ?)";
    private static final String SELECT_QUERY = "SELECT * FROM registration WHERE email_id = ? and password = ?";

    private final Connection conn = MariaDBConnector.getInstance();
    public void insertRecord(String fullName, String emailId, String password) throws SQLException {

        try {

             PreparedStatement preparedStatement = conn.prepareStatement(INSERT_QUERY);
             preparedStatement.setString(1, fullName);
             preparedStatement.setString(2, emailId);
             preparedStatement.setString(3, password);

            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    public boolean validate(String emailId, String password) throws SQLException {

        try {

             PreparedStatement preparedStatement = conn.prepareStatement(SELECT_QUERY);
            preparedStatement.setString(1, emailId);
            preparedStatement.setString(2, password);

            System.out.println(preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
            resultSet.close();
            preparedStatement.close();

        }  catch (SQLException e) {
            printSQLException(e);
        }
        return false;
    }
    public Integer loggedID(String emailId, String password){
        try  {

            String sql = "SELECT id FROM registration WHERE email_id = ? and password = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setString(1, emailId);
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
