package fi.metropolia.expensetracker.datasource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class MariaDBConnectorTest {

    @Test
    @DisplayName("Database connection test")
    void getInstance() throws SQLException {
        assertNotNull(MariaDBConnector.getInstance(), "Connection not succeed");
    }
}