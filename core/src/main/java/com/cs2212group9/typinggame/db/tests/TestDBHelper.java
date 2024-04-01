package com.cs2212group9.typinggame.db.tests;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static com.cs2212group9.typinggame.db.DBHelper.createNewTable;
import static com.cs2212group9.typinggame.db.DBHelper.getConnection;

/**
 * Provides test cases for the DBHelper class to ensure that database operations such as creating tables
 * and establishing connections are performed correctly. These tests validate the functionality of the DBHelper
 * class, which is crucial for the operation of the Typing Game application by interacting with the database.
 */
public class TestDBHelper {
    /**
     * Tests the creation of new tables in the database. This test case verifies that the DBHelper class
     * can successfully execute SQL commands to create tables necessary for the application. It ensures
     * that the application's initial setup of database schema is correct and operational.
     */
    @Test
    public void testCreateNewTable() {
        createNewTable();
        // Since the method has no return value and its success is assumed by the absence of exceptions,
        // there's no explicit assertion here. Consider adding a way to verify table creation.
    }

    /**
     * Tests the database connection functionality provided by the DBHelper class. This test case ensures
     * that the application can establish a connection to the database correctly using the DBHelper class.
     * A successful test confirms that the database is accessible and ready for further operations.
     */
    @Test
    public void testGetConnection() {
        Connection conn = getConnection();
        assert conn != null;
    }
}
