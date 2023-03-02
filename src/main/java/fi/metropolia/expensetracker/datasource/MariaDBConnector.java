package fi.metropolia.expensetracker.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MariaDBConnector {
    private static Connection conn = null;

    private MariaDBConnector() {
    }

    public static Connection getInstance() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection("jdbc:mysql://10.114.32.29/expensetracker?user=tracker&password=tracker");
            } catch (SQLException var1) {
                var1.printStackTrace();
                System.exit(-1);
            }
            return conn;
        } else {
            return conn;
        }
    }
}
