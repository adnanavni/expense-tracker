package fi.metropolia.expensetracker.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MariaDBConnector {
    private static Connection conn = null;

    public MariaDBConnector() {
    }

    public static Connection getInstance() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection("jdbc:mysql://10.114.32.29/expensetracker?user=tracker&password=tracker");
                System.out.println("Connection succesfull.");
            } catch (SQLException var1) {
                System.out.println("Connection failed.");
                var1.printStackTrace();
                System.exit(-1);
            }

            return conn;
        } else {
            return conn;
        }
    }
}