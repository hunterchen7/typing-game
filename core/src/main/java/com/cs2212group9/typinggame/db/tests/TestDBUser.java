package com.cs2212group9.typinggame.db.tests;

import com.cs2212group9.typinggame.db.DBHelper;
import com.cs2212group9.typinggame.db.DBUser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class TestDBUser {
    @BeforeAll
    static void setup() {
        Connection conn = DBHelper.getConnection();
        String addAdmin = """
            INSERT INTO users (username, password, date_created, difficulty_modifier, is_admin, instant_death)
            VALUES ('admin', 'admin', datetime('now'), 0, 1, 0);
        """;
        DBUser.addUser("notAdmin", "notAdmin");
        try (var stmt = conn.createStatement()) {
            stmt.executeUpdate(addAdmin);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void testUserExists() {
        assertTrue(DBUser.userExists("notAdmin"));
    }

    @Test
    void testUserNotExist() {
        assertFalse(DBUser.userExists("notAdmin2"));
    }

    @Test
    void testFetchPassword() {
        // it's NOT hashed here, but we're testing that input = output
        assertEquals("test", DBUser.getUserPasswordHashed("test"));
    }

    @Test
    void testUserNextLevel() {
        DBUser.addUser("test", "test");
        assertEquals(1, DBUser.getNextLevel("test"));
    }

    @Test
    void testIfAdmin() {
        assertTrue(DBUser.isAdmin("admin"));
    }

    @Test
    void testIfNotAdmin() {
        assertFalse(DBUser.isAdmin("notAdmin"));
    }

}
