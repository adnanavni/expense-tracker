package fi.metropolia.expensetracker.module.Dao;

import fi.metropolia.expensetracker.datasource.MariaDBConnector;
import fi.metropolia.expensetracker.module.Salary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import static fi.metropolia.expensetracker.module.Dao.RegisterLoginDao.printSQLException;

public class IncomeDao {
    private final Connection conn = MariaDBConnector.getInstance();

    public Salary getSalary(Integer id, String type) {
        try {
            String sql = "SELECT * FROM Incomes WHERE IncomeID = ? AND Type = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, type);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {

                return new Salary(resultSet.getInt(1), resultSet.getDouble(3), resultSet.getDouble(4), resultSet.getDate(6).toLocalDate(), resultSet.getString(5),
                        resultSet.getString(2), resultSet.getDouble(7));
            }
            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            printSQLException(e);
        }
        return null;
    }


    public boolean deleteSalary(Integer id, String type) {
        Salary salary = getSalary(id, type);

        if (salary != null) {
            try {
                String sql = "DELETE FROM Incomes WHERE IncomeID =? AND Type =?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setInt(1, id);
                preparedStatement.setString(2, type);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                return true;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return false;
            }
        } else {
            System.out.println("False delete");
            return false;
        }
    }


    public void saveSalary(Integer userID, String type, Double salary, Double salaryMinusTaxes, Date date, Double taxrate, String currency) {
        try {

            String sql = "INSERT INTO Incomes VALUES (NULL, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, type);
            preparedStatement.setDouble(2, salary);
            preparedStatement.setDouble(3, salaryMinusTaxes);
            preparedStatement.setString(4, currency);

            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            preparedStatement.setDate(5, sqlDate);

            preparedStatement.setDouble(6, taxrate);
            preparedStatement.setInt(7, userID);

            preparedStatement.executeUpdate();

            preparedStatement.close();
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    public ArrayList<Salary> getSalariesWithType(Integer userID, String type) {

        String sql = "SELECT * FROM Incomes WHERE userID = ? AND Type = ?";

        ArrayList<Salary> salaries = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            ps.setString(2, type);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Salary salary =
                        new Salary(resultSet.getInt(1), resultSet.getDouble(3), resultSet.getDouble(4), resultSet.getDate(6).toLocalDate(), resultSet.getString(5),
                                resultSet.getString(2), resultSet.getDouble(7));
                salaries.add(salary);
            }
            resultSet.close();
            ps.close();

        } catch (SQLException e) {
            printSQLException(e);
        }
        return salaries;
    }

    public ArrayList<Salary> getAllSalaries(Integer userID) {

        String sql = "SELECT * FROM Incomes WHERE userID = ?";

        ArrayList<Salary> salaries = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Salary salary =
                        new Salary(resultSet.getInt(1), resultSet.getDouble(3), resultSet.getDouble(4), resultSet.getDate(6).toLocalDate(), resultSet.getString(5),
                                resultSet.getString(2), resultSet.getDouble(7));
                salaries.add(salary);
            }
            resultSet.close();
            ps.close();

        } catch (SQLException e) {
            printSQLException(e);
        }
        return salaries;
    }

    public boolean changeIncomeValues(Integer id, Double amount, Double amount_tax, String currency) {
        try {
            String sql = "UPDATE Incomes SET Amount = ?, Amount_Minus_Taxes = ?, Currency = ? WHERE IncomeID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, amount);
            ps.setDouble(2, amount_tax);
            ps.setString(3, currency);
            ps.setDouble(4, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            printSQLException(e);
        }
        return false;
    }
}
