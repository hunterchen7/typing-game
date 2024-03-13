package com.cs2212group9.typinggame.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class User {
    static Connection conn = DBHelper.getConnection();

    // checks if a user exists
    public static boolean userExists(String username) {
        String sql = "SELECT username FROM users WHERE username = '" + username + "';";
        String user = null;

        try (Statement stmt = conn.createStatement()) {
            user = stmt.executeQuery(sql).getString("username");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("user: " + user);

        return user != null;
    }

    // adds a user to the database
    public static void addUser(String username, String password) {
        if (userExists(username)) {
            return;
        }

        String sql = // default difficulty modifier is 0, date created is now
            """
                INSERT INTO users (username, password, date_created, difficulty_modifier)
                VALUES (?, ?, datetime('now'), 0);
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // returns hashed password
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
}
