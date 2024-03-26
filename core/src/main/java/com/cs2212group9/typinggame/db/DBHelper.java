package com.cs2212group9.typinggame.db;

import org.junit.jupiter.api.Test;

import java.sql.*;

public class DBHelper {
    private static Connection conn = null;

    // init db connection, singleton
    static {
        String url = "jdbc:sqlite:typinggame.db";

        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        createNewTable();
    }

    /** The connection to the database */
    public static Connection getConnection() {
        return conn;
    }

    /**
     * Create users, levels, and scores tables if they don't exist
     */
    public static void createNewTable() {
        String[] commands = {
            // considering making a "user id" primary key so that people can have the same usernames
            """
                CREATE TABLE IF NOT EXISTS users (
                    username TEXT PRIMARY KEY,
                    password TEXT,
                    date_created DATETIME,
                    difficulty_modifier INTEGER,
                    is_admin BOOLEAN,
                    instant_death BOOLEAN
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
