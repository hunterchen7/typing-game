package com.cs2212group9.typinggame.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class User {
    /** The connection to the database */
    static Connection conn = DBHelper.getConnection();

    /**
     * @param username - the username of the user
     * @return true if the user exists, false otherwise
     */
    public static boolean userExists(String username) {
        String sql = "SELECT username FROM users WHERE username = '" + username + "';";
        String user = null;

        try (Statement stmt = conn.createStatement()) {
            user = stmt.executeQuery(sql).getString("username");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("username: " + user + " exists");

        return user != null;
    }

    // adds a user to the database
    /**
     * @param username - the username of the user
     * @param password - the hashed password of the user
     */
    public static void addUser(String username, String password) {
        if (userExists(username)) {
            System.out.println("User already exists");
            return;
        }

        String sql = // default difficulty modifier is 0, date created is now
            """
                INSERT INTO users (username, password, date_created, difficulty_modifier, is_admin, instant_death)
                VALUES (?, ?, datetime('now'), 0, FALSE, FALSE);
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param username - the username of the user
     * @return the hashed password of the user
     */
    public static String getUserPasswordHashed(String username) {
        String sql = "SELECT password FROM users WHERE username = '" + username + "';";
        String password = null;

        try (Statement stmt = conn.createStatement()) {
            password = stmt.executeQuery(sql).getString("password");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return password;
    }

    /**
     * @param username - the username of the user
     * @return the next unlocked level of the user
     */
    public static int getNextLevel(String username) {
        String sql = "SELECT level FROM scores WHERE user = '" + username + "' ORDER BY date_played DESC LIMIT 1;";
        int level = 1;

        try (Statement stmt = conn.createStatement()) {
            level = stmt.executeQuery(sql).getInt("level");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return level;
    }
}
