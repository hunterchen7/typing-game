package com.cs2212group9.typinggame.db.tests;

import com.cs2212group9.typinggame.db.DBHelper;
import com.cs2212group9.typinggame.db.DBUser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the DBUser class
 */
@TestMethodOrder(org.junit.jupiter.api.MethodOrderer.OrderAnnotation.class)
public class TestDBUser {
    /**
     * Set up the database by dropping and creating the users table and inserting some test data
     */
    @BeforeAll
    static void setup() {
        Connection conn = DBHelper.getConnection();
        String drop = "DROP TABLE IF EXISTS users;";
        String create = """
            CREATE TABLE users (
                username TEXT PRIMARY KEY,
                password TEXT,
                date_created DATETIME,
                difficulty_modifier INTEGER,
                is_admin BOOLEAN,
                instant_death BOOLEAN
            );
        """;
        String addAdmin = """
            INSERT INTO users (username, password, date_created, difficulty_modifier, is_admin, instant_death)
            VALUES ('admin', 'admin', datetime('now'), 0, 1, 0);
        """;
        String addNotAdmin = """
            INSERT INTO users (username, password, date_created, difficulty_modifier, is_admin, instant_death)
            VALUES ('notAdmin', 'notAdmin', datetime('now'), 0, 0, 0);
        """;
        try (var stmt = conn.createStatement()) {
            stmt.executeUpdate(drop);
            stmt.executeUpdate(create);
            stmt.executeUpdate(addAdmin);
            stmt.executeUpdate(addNotAdmin);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    @Order(1)
    void testUserExists() {
        assertTrue(DBUser.userExists("notAdmin"));
    }

    @Test
    @Order(2)
    void testUserNotExist() {
        assertFalse(DBUser.userExists("notAdmin2"));
    }

    @Test
    @Order(3)
    void testFetchPassword() {
        // it's NOT hashed here, but we're testing that input = output
        DBUser.addUser("test", "test");
        assertEquals("test", DBUser.getUserPasswordHashed("test"));
    }

    @Test
    @Order(4)
    void testIfAdmin() {
        assertTrue(DBUser.isAdmin("admin"));
    }

    @Test
    @Order(5)
    void testIfNotAdmin() {
        assertFalse(DBUser.isAdmin("notAdmin"));
    }

    @Test
    @Order(6)
    void testGetNumberOfUsers() {
        assertEquals(3, DBUser.getNumberOfUsers());
    }

    @Test
    @Order(7)
    void testGetNumberOfUsersAdd() {
        DBUser.addUser("probablyUniqueUsername", "test2");
        assertEquals(4, DBUser.getNumberOfUsers());
    }
}
