package com.cs2212group9.typinggame.db;

import com.cs2212group9.typinggame.utils.ScoreEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        return extractInt(sql);
    }

    private static int extractInt(String sql) {
        int score = 0;
        try (var stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            score = rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return score;
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

    /**
     * Returns the n highest user total scores
     * @param n - the number of scores to return
     * @return a ResultSet of the n highest user total scores
      */
    public static List<ScoreEntry> getTopScores(int n) {
        List<ScoreEntry> scores = new ArrayList<>();
        String sql = """
                    SELECT user, SUM(max_score) AS total_score
                    FROM (
                        SELECT user, level, MAX(score) AS max_score
                        FROM scores
                        GROUP BY user, level
                    ) AS user_max_scores
                    GROUP BY user
                    ORDER BY total_score DESC
                    LIMIT ?;
                    """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, n);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String user = rs.getString("user");
                    int totalScore = rs.getInt("total_score");
                    scores.add(new ScoreEntry(user, totalScore));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return scores;
    }

    /**
     * Returns the total score for a user
     * @param username - the username of the user
     * @return an integer that represents the total score for a user
     */
    public static int getUserTotalScore(String username) {
        int totalScore = 0;
        String sql = """
                    SELECT SUM(max_score) AS total_score
                    FROM (
                        SELECT MAX(score) AS max_score
                        FROM scores
                        WHERE user = ?
                        GROUP BY user, level
                    ) AS user_max_scores;
                    """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    totalScore = rs.getInt("total_score");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return totalScore;
    }

    /**
     * Returns the top score that has a String username and an integer score for a level
     * @param level - the level to get the top score from
     * @return a ScoreEntry object that represents the top score for a level
     */
    public static ScoreEntry getTopLevelScore(int level) {
        ScoreEntry score = null;
        String sql = """
                    SELECT user, MAX(score) AS max_score
                    FROM scores
                    WHERE level = ?
                    GROUP BY user
                    ORDER BY score DESC
                    LIMIT 1;
                    """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, level);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String user = rs.getString("user");
                    int maxScore = rs.getInt("max_score");
                    score = new ScoreEntry(user, maxScore);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return score;
    }

    /**
     * Returns the top score that has a String username and an integer score for a level
     * @param username - the username of the user
     * @param level - the level to get the top score from
     * @return a ScoreEntry object that represents the top score for a level
     */
    public static ScoreEntry getUserTopLevelScore(String username, int level) {
        ScoreEntry score = null;
        String sql = """
                    SELECT user, MAX(score) AS max_score
                    FROM scores
                    WHERE level = ? AND user = ?
                    GROUP BY user
                    ORDER BY score DESC
                    LIMIT 1;
                    """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, level);
            pstmt.setString(2, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String user = rs.getString("user");
                    int maxScore = rs.getInt("max_score");
                    score = new ScoreEntry(user, maxScore);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return score;
    }

    /**
     * Returns the number of times a user has played a level
     * @param username - the username of the user
     * @param level - the level to get the number of plays from
     * @return an integer that represents the number of times a user has played a level
     */
    public static int getUserLevelPlays(String username, int level) {
        int plays = 0;
        String sql = """
                    SELECT COUNT(*) AS plays
                    FROM scores
                    WHERE user = ? AND level = ?;
                    """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, level);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    plays = rs.getInt("plays");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return plays;
    }

    /**
     * Returns the number of games played by counting the number of rows in the scores table
     * @return an integer that represents the number of games played
     */
    public static int getGamesPlayed() {
        int gamesPlayed = 0;
        String sql = """
                    SELECT COUNT(*) AS games_played
                    FROM scores;
                    """;

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            gamesPlayed = rs.getInt("games_played");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return gamesPlayed;
    }
}
