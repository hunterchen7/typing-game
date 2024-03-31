package com.cs2212group9.typinggame.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class is used to interact with the users table in the database.
 */
public class DBUser {
    /** The connection to the database */
    static Connection conn = DBHelper.getConnection();

    /**
     * checks if a user exists in the database
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

        // System.out.println("username: " + user + " exists");

        return user != null;
    }

    // adds a user to the database
    /**
     * adds a user to the database
     * @param username - the username of the user
     * @param password - the hashed password of the user
     * @param isAdmin - true if the user is an admin, false otherwise
     */
    public static void addUser(String username, String password, Boolean isAdmin) {
        if (userExists(username)) {
            System.out.println("DBUser already exists");
            return;
        }

        String sql = // default difficulty modifier is 0, date created is now
            """
                INSERT INTO users (username, password, date_created, difficulty_modifier, is_admin, instant_death)
                VALUES (?, ?, datetime('now'), 0, ?, FALSE);
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setBoolean(3, isAdmin);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * adds a user to the database
     * @param username - the username of the user
     * @param password - the hashed password of the user
     */
    public static void addUser(String username, String password) {
        addUser(username, password, false);
    }

    /**
     * updates the password of a user
     * @param username - the username of the user
     * @param password - the hashed password of the user
     */
    public static void changePassword(String username, String password) {
        if (!userExists(username)) {
            System.out.println("DBUser does not exist");
            return;
        }

        String sql =
            """
                UPDATE users
                SET password = ?
                WHERE username = ?;
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, password);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * queries the database to return the user's stored password hash
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
     * return if a user is an admin
     * @param username - the username of the user
     * @return true if the user is an admin, false otherwise
     */
    public static boolean isAdmin(String username) {
        String sql = "SELECT is_admin FROM users WHERE username = '" + username + "';";
        boolean isAdmin = false;

        try (Statement stmt = conn.createStatement()) {
            isAdmin = stmt.executeQuery(sql).getBoolean("is_admin");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return isAdmin;
    }

    /**
     * returns the number of users in the database
     * @return the number of users in the database
     */
    public static int getNumberOfUsers() {
        int numberOfUsers = 0;
        String sql = """
            SELECT COUNT(*) AS number_of_users
            FROM users;
        """;

        try (Statement stmt = conn.createStatement()) {
            numberOfUsers = stmt.executeQuery(sql).getInt("number_of_users");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return numberOfUsers;
    }

}
