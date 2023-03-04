package fi.metropolia.expensetracker.module;

import fi.metropolia.expensetracker.datasource.MariaDBConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class Login_Signup_Dao {

    private static final String INSERT_QUERY = "INSERT INTO Registration (username, password) VALUES (?, ?)";
    private static final String SELECT_QUERY = "SELECT * FROM Registration WHERE username = ?";
    private final Connection conn = MariaDBConnector.getInstance();

    public void insertRecord(String username, String password) {
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
        System.out.println(prepPsswordState);
        if (resultSet.next()) {
            System.out.println(resultSet.getString(1));
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
