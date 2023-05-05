package fi.metropolia.expensetracker.datasource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * This class provides a way to connect to a MariaDB database using the Singleton design pattern.
 */
public class MariaDBConnector {
    /**
     * Private constructor to prevent instantiation from outside the class.
     */
    private static Connection conn = null;

    private MariaDBConnector() {

    }

    /**
     * Returns the single instance of the Connection object.
     *
     * @return the Connection object used to connect to the MariaDB database.
     */
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
                // if the database is hosted locally remove the next comment
                // conn = DriverManager.getConnection("jdbc:mysql://localhost/" + database + "?user=" + name + "&password=" + password);
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
