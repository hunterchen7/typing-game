package com.cs2212group9.typinggame.db.tests;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static com.cs2212group9.typinggame.db.DBHelper.createNewTable;
import static com.cs2212group9.typinggame.db.DBHelper.getConnection;

/**
 * Tests for the DBHelper class
 */
public class TestDBHelper {
    /**
     * Test initializing the database with tables
     */
    @Test
    public void testCreateNewTable() {
        createNewTable();
    }

    /**
     * Test getting a connection to the database
     */
    @Test
    public void testGetConnection() {
        Connection conn = getConnection();
        assert conn != null;
    }
}
