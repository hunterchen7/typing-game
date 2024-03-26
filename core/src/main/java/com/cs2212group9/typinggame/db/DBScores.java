package com.cs2212group9.typinggame.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBScores {
    static Connection conn = DBHelper.getConnection();

    public static void addScore(String username, int level, int score) {
        String sql = // default difficulty modifier is 0, date created is now
            """
                INSERT INTO scores (user, level, score, date_played)
                VALUES (?, ?, ?, datetime('now'));
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, level);
            pstmt.setInt(3, score);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
