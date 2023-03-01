package fi.metropolia.expensetracker.module;

import fi.metropolia.expensetracker.datasource.MariaDBConnector;

import java.sql.*;

public class Login_Signup_Dao {

    private static final String INSERT_QUERY = "INSERT INTO Registration (username, password) VALUES (?, ?)";
    //private static final String SELECT_QUERY = "SELECT * FROM Registration WHERE username = ? and password = ?";
    private static final String SELECT_QUERY = "SELECT * FROM Registration WHERE username = ?";

    private PsswdAuth psswdAuth = new PsswdAuth();
    private final Connection conn = MariaDBConnector.getInstance();

    public void insertRecord(String username, String password) throws SQLException {
        try {

             PreparedStatement preparedStatement = conn.prepareStatement(INSERT_QUERY);
             preparedStatement.setString(1, username);
             preparedStatement.setString(2, password);

            System.out.println("Insert record prepare " + preparedStatement);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    public String getPassword (String username) throws SQLException {

        PreparedStatement prepPsswordState = conn.prepareStatement("SELECT password FROM Registration WHERE username=?");
        prepPsswordState.setString(1, username);
        ResultSet resultSet = prepPsswordState.executeQuery();
       // String password = "";
        System.out.println(prepPsswordState);
        if (resultSet.next()) {
            System.out.println(resultSet.getString(1));
            return resultSet.getString(1);
        }


        resultSet.close();
        prepPsswordState.close();

        return null;
    }
    public boolean validate(String username, String password) throws SQLException {
        try {
             PreparedStatement preparedStatement = conn.prepareStatement(SELECT_QUERY);
            preparedStatement.setString(1, username);
            System.out.println(preparedStatement);
            PsswdAuth auth = new PsswdAuth();


            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next() && auth.authenticate(password.toCharArray(), getPassword(username)) ) {
                return true;
            }
            resultSet.close();
            preparedStatement.close();

        }  catch (SQLException e) {
            printSQLException(e);
        }
        return false;
    }

    public Integer loggedID(String name){

        try  {
            String sql = "SELECT id FROM Registration WHERE username = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setString(1, name);


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

    public String loggedCurrency(Integer id){
        try  {
            System.out.println("Logged currency id " + id);
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
