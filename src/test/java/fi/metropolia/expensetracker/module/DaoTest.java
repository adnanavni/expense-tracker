package fi.metropolia.expensetracker.module;

import fi.metropolia.expensetracker.module.Dao.Dao;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DaoTest {
    Dao dao = new Dao();

    @Test
    void userExists() throws SQLException {
        dao.insertRecord("testExpense", "testExpense");
        assertTrue(dao.userExists("testExpense"), "User not found" );
        dao.deleteUser("testExpense");
    }

    @Test
    void removeUser() throws SQLException {
        dao.insertRecord("testExpense", "testExpense");
        assertTrue(dao.deleteUser("testExpense"), "Test removing user failed");
    }
}