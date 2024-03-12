package com.cs2212group9.typinggame.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Level {
    static Connection conn = DBHelper.getConnection();

    // returns the words of a level
    public String[] getLevelWords(int level) {
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
