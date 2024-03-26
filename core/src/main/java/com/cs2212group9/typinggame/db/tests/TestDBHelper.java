package com.cs2212group9.typinggame.db.tests;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static com.cs2212group9.typinggame.db.DBHelper.createNewTable;
import static com.cs2212group9.typinggame.db.DBHelper.getConnection;

public class TestDBHelper {
    @Test
    public void testCreateNewTable() {
        createNewTable();
    }

    @Test
    public void testGetConnection() {
        Connection conn = getConnection();
        assert conn != null;
    }
}
