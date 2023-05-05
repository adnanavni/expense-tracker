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

/**

 The IncomeDao class represents a Data Access Object (DAO) that provides methods to interact with the 'Incomes' table in the database.
 It handles the retrieval, creation, modification and deletion of income records for a given user.
 */
public class IncomeDao {
    private final Connection conn = MariaDBConnector.getInstance();

    /**
     * Retrieves a Salary object based on the given IncomeID and Type
     *
     * @param id   The ID of the income record to be retrieved
     * @param type The type of the income record to be retrieved
     * @return Salary object if the record exists, null otherwise
     */
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

    /**
     * Deletes an income record based on the given IncomeID and Type
     *
     * @param id   The ID of the income record to be deleted
     * @param type The type of the income record to be deleted
     * @return true if the record was deleted successfully, false otherwise
     */
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
    /**
     * Creates a new income record for a given user
     *
     * @param userID          The ID of the user
     * @param type            The type of the income
     * @param salary          The amount of the income before taxes
     * @param salaryMinusTaxes The amount of the income after taxes
     * @param date            The date of the income
     * @param taxrate         The tax rate applied to the income
     * @param currency        The currency of the income
     */

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
    /**
     * Retrieves all salaries for a given user ID and salary type.
     * @param userID the ID of the user whose salaries are being retrieved
     * @param type the type of salaries being retrieved "month" or  "day"
     * @return an ArrayList of Salary objects representing the salaries matching the given criteria
    */


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

    /**
     * Retrieves all salaries for a given user ID.
     *
     * @param userID the ID of the user whose salaries are being retrieved
     * @return an ArrayList of Salary objects representing all salaries for the given user
     */

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

    /**
     * Updates the amount, amount minus taxes, and currency of a given income.
     *
     * @param id the ID of the income to be updated
     * @param amount the new amount of the income
     * @param amount_tax the new amount of the income minus taxes
     * @param currency the new currency of the income
     * @return true if the update was successful, false otherwise
     */
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
