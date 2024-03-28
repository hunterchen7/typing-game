package com.cs2212group9.typinggame.db;

import java.sql.*;
import java.util.HashMap;

public class DBScores {
    static Connection conn = DBHelper.getConnection();

    /**
     * Adds a score to the database, must exceed minimum score for level
     * @param username - the username of the user
     * @param level - the level of the score
     * @param score - the score to be added
     */
    public static void addScore(String username, int level, int score) {
        String sql = // default difficulty modifier is 0, date created is now
            """
                INSERT INTO scores (user, level, score, date_played, passed)
                VALUES (?, ?, ?, datetime('now'), ?);
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, level);
            pstmt.setInt(3, score);
            pstmt.setBoolean(4, score >= DBLevel.getMinScores().get(level));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Returns the highest score for a user on a level, returns 0 if they don't have a score for that level
     * Used in highestUnlockedLevel to determine the highest level a user has unlocked
     * @param username - the username of the user
     * @param level - the level id of the level to get the score from
     * @return an integer that represents the highest score for a user on a level
     */
    public static int getLevelMaxScore(String username, int level) {
        String sql = "SELECT MAX(score) FROM scores WHERE user = '" + username + "' AND level = " + level + ";";
        int maxScore = 0;

        try (var stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            maxScore = rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return maxScore;
    }

    /**
     * Returns the highest level a user has unlocked by checking levels they've completed with a high
     * returns 1 if they haven't completed any levels
     * @param username - the username of the user
     * @return an integer that represents the highest level a user has unlocked
     */
    public static int highestUnlockedLevel(String username) {
        HashMap<Integer, Integer> levelMinScores = DBLevel.getMinScores();

        // loop through levels to find the highest unlocked
        int highest = 1;
        for (int level : levelMinScores.keySet()) {
            if (getLevelMaxScore(username, level) >= levelMinScores.get(level)) {
                highest = level + 1;
            } else {
                break;
            }
        }

        return highest;
    }
}
