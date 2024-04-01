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
 * This class contains tests for the {@link DBUser} class.
 * It checks the functionalities related to user operations in the database,
 * such as checking if a user exists, verifying passwords, and checking user privileges.
 */
@TestMethodOrder(org.junit.jupiter.api.MethodOrderer.OrderAnnotation.class)
public class TestDBUser {
    /**
     * Prepares the testing environment by setting up the database.
     * This includes dropping the users table if it exists, creating a new users table,
     * and inserting test data for an admin and a regular user.
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

    /**
     * Tests if the method correctly identifies an existing user.
     */
    @Test
    @Order(1)

    void testUserExists() {
        assertTrue(DBUser.userExists("notAdmin"));
    }

    /**
     * Tests if the method correctly identifies a non-existing user.
     */
    @Test
    @Order(2)
    void testUserNotExist() {
        assertFalse(DBUser.userExists("notAdmin2"));
    }


    /**
     * Tests if fetching the password for a user returns the expected password hash.
     * Note: In this test environment, passwords are not hashed.
     */
    @Test
    @Order(3)
    void testFetchPassword() {
        // it's NOT hashed here, but we're testing that input = output
        DBUser.addUser("test", "test");
        assertEquals("test", DBUser.getUserPasswordHashed("test"));
    }

    /**
     * Tests if the method correctly identifies an admin user.
     */
    @Test
    @Order(4)
    void testIfAdmin() {
        assertTrue(DBUser.isAdmin("admin"));
    }

    /**
     * Tests if the method correctly identifies that a regular user is not an admin.
     */
    @Test
    @Order(5)
    void testIfNotAdmin() {
        assertFalse(DBUser.isAdmin("notAdmin"));
    }

    /**
     * Tests if the method correctly returns the total number of users in the database.
     */
    @Test
    @Order(6)
    void testGetNumberOfUsers() {
        assertEquals(3, DBUser.getNumberOfUsers());
    }

    /**
     * Tests the addition of a new user and checks if the total number of users is updated correctly.
     */
    @Test
    @Order(7)
    void testGetNumberOfUsersAdd() {
        DBUser.addUser("probablyUniqueUsername", "test2");
        assertEquals(4, DBUser.getNumberOfUsers());
    }
}
