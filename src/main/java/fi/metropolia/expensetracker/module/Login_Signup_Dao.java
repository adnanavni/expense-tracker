package fi.metropolia.expensetracker.module;

import fi.metropolia.expensetracker.datasource.MariaDBConnector;

import java.sql.*;

public class Login_Signup_Dao {

    private static final String INSERT_QUERY = "INSERT INTO registration (full_name, password) VALUES (?, ?)";
    private static final String SELECT_QUERY = "SELECT * FROM registration WHERE full_name = ? and password = ?";

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

            String sql = "SELECT id FROM registration WHERE full_name = ? and password = ?";
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

            String sql = "SELECT currency FROM registration WHERE id=?";
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

    public boolean changeUserCurrency(Integer id, String currency){
        try {
            String sql = "UPDATE registration SET currency = ? WHERE id= ?";
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
