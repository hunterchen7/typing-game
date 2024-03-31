package com.cs2212group9.typinggame.db;

import com.cs2212group9.typinggame.utils.UserAuthenticator;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.sql.*;

/**
 * Class to interact with the database
 */
public class DBHelper {
    private static Connection conn = null;

    // init db connection, singleton
    static {
        // tests run on core/typing-game.db and the main program runs on group9/typing-game.db
        // tests create new items, so I don't want them to interfere with each other
        String url = "jdbc:sqlite:typing-game.db";

        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        createNewTable();
    }

    /**
     * Get the connection to the database
     * @return Connection object
     */
    public static Connection getConnection() {
        return conn;
    }

    /**
     * Create users, levels, and scores tables if they don't exist, also insert default admin user with "secret" password
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
            """
                CREATE TABLE IF NOT EXISTS levels (
                    level_id INTEGER PRIMARY KEY,
                    words TEXT,
                    difficulty INTEGER,
                    waves INTEGER,
                    min_score INTEGER
                );
            """,
            """
                CREATE TABLE IF NOT EXISTS scores (
                    user TEXT,
                    level INTEGER,
                    score INTEGER,
                    date_played DATETIME,
                    passed BOOLEAN,
                    FOREIGN KEY (user) REFERENCES users(username),
                    FOREIGN KEY (level) REFERENCES levels(level_id),
                    PRIMARY KEY (user, level, date_played)
                );
            """,
            // insert default admin user, password is a secret :^)
            """
                INSERT INTO users (username, password, date_created, difficulty_modifier, is_admin, instant_death)
                VALUES ('admin', 'c039c7c7331f0cdc357724dd3b1441765a283ac853be5f34adb6a87956b6ea14', datetime('now'), 0, TRUE, FALSE);
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
}
