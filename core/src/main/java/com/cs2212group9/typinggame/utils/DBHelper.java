package com.cs2212group9.typinggame.utils;

import java.sql.*;

public class DBHelper {
    private Connection conn;

    private void connect() {
        try {
            if (this.conn == null || this.conn.isClosed()) {
                String url = "jdbc:sqlite:typinggame.db";
                Connection conn = null;

                try {
                    conn = DriverManager.getConnection(url);
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }

                this.conn = conn;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    // creates new tables if they don't already exist
    public void createNewTable() {
        connect();
        String[] commands = {
            // considering making a "user id" primary key so that people can have the same usernames
            """
                CREATE TABLE IF NOT EXISTS users (
                    username TEXT PRIMARY KEY,
                    password TEXT,
                    date_created DATETIME,
                    difficulty_modifier INTEGER
                );
            """,
            // words as csv??
            """
                CREATE TABLE IF NOT EXISTS levels (
                    level_id INTEGER PRIMARY KEY,
                    words TEXT,
                    difficulty INTEGER,
                    waves INTEGER
                );
            """,
            //
            """
                CREATE TABLE IF NOT EXISTS scores (
                    user TEXT,
                    level INTEGER,
                    score INTEGER,
                    date_played DATETIME,
                    FOREIGN KEY (user) REFERENCES users(username),
                    FOREIGN KEY (level) REFERENCES levels(level_id),
                    PRIMARY KEY (user, level, date_played)
                );
            """
        };

        try (Statement stmt = conn.createStatement()) {
            for (String command : commands) {
                stmt.execute(command);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // checks if a user exists
    public boolean userExists(String username) {
        connect();
        String sql = "SELECT username FROM users WHERE username = '" + username + "';";
        String user = null;

        try (Statement stmt = conn.createStatement()) {
            user = stmt.executeQuery(sql).getString("username");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return user != null;
    }

    // adds a user to the database
    public boolean addUser(String username, String password) {
        if (userExists(username)) {
            return false;
        }

        connect();
        String sql = // default difficulty modifier is 0, date created is now
            """
                INSERT INTO users (username, password, date_created, difficulty_modifier)
                VALUES (?, ?, datetime('now'), 0);
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    // returns hashed password
    public String getUserPasswordHashed(String username) {
        connect();
        String sql = "SELECT password FROM users WHERE username = '" + username + "';";
        String password = null;

        try (Statement stmt = conn.createStatement()) {
            password = stmt.executeQuery(sql).getString("password");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return password;
    }

    // returns the words of a level
    public String[] getLevelWords(int level) {
        connect();
        String sql = "SELECT words FROM levels WHERE level_id = " + level + ";";
        String words = null;

        try (Statement stmt = conn.createStatement()) {
            words = stmt.executeQuery(sql).getString("words");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        assert words != null;
        return words.split(",");
    }

}
