package fi.metropolia.expensetracker.datasource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MariaDBConnector {
    private static Connection conn = null;

    private MariaDBConnector() {
    }

    public static Connection getInstance() {
        Properties props = new Properties();
        FileInputStream input = null;
        try {
            input = new FileInputStream("src/main/java/fi/metropolia/expensetracker/datasource/.env");
            props.load(input);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String database = props.getProperty("DB_name");
        String name = props.getProperty("DB_username");
        String password = props.getProperty("DB_password");


        if (conn == null) {
            try {
                conn = DriverManager.getConnection("jdbc:mysql://10.114.32.29/" + database + "?user=" + name + "&password=" + password);
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
