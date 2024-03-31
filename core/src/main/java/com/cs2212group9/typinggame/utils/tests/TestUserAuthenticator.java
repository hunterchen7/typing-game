package com.cs2212group9.typinggame.utils.tests;

import com.cs2212group9.typinggame.db.DBHelper;
import com.cs2212group9.typinggame.utils.UserAuthenticator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the UserAuthenticator class
 */
public class TestUserAuthenticator {
    @BeforeAll
    static void setup() {
        Connection conn = DBHelper.getConnection();
        String delete = "DELETE FROM users;"; // clear all users
        try (var stmt = conn.createStatement()) {
            stmt.executeUpdate(delete);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        UserAuthenticator user1 = new UserAuthenticator("testuser", "testpass");
        // add user to database
        user1.register();
    }

    @Test
    void testRegister() {
        UserAuthenticator user2 = new UserAuthenticator("testuser2", "testpass2");
        assertTrue(user2.register());
    }

    @Test
    void testRegisterAlreadyExists() {
        UserAuthenticator user1 = new UserAuthenticator("testuser", "testpass");
        assertFalse(user1.register());
    }

    @Test
    void testAuthenticate() throws NoSuchAlgorithmException {
        UserAuthenticator user1 = new UserAuthenticator("testuser", "testpass");
        assertTrue(user1.authenticate());
    }

    @Test
    void testAuthenticateWrongPassword() throws NoSuchAlgorithmException {
        UserAuthenticator user1 = new UserAuthenticator("testuser", "testpass2");
        assertFalse(user1.authenticate());
    }

    @Test
    void testAuthenticateNoSuchUser() throws NoSuchAlgorithmException {
        UserAuthenticator user1 = new UserAuthenticator("testuser7", "testpass");
        assertFalse(user1.authenticate());
    }
}
