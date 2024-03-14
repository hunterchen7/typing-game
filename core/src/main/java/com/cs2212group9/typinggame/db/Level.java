package com.cs2212group9.typinggame.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

// class for retrieving level data
// this class does not require setters, level data should be preset
public class Level {
    static Connection conn = DBHelper.getConnection();

    public int levelCount() {
        String sql = "SELECT COUNT(*) FROM levels;";
        int count = 0;

        try (Statement stmt = conn.createStatement()) {
            count = stmt.executeQuery(sql).getInt(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return count;
    }

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

    // returns the difficulty of a level
    public int getLevelDifficulty(int level) {
        String sql = "SELECT difficulty FROM levels WHERE level_id = " + level + ";";
        int difficulty = 0;

        try (Statement stmt = conn.createStatement()) {
            difficulty = stmt.executeQuery(sql).getInt("difficulty");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return difficulty;
    }

    // returns the number of waves of a level
    public int getLevelWaves(int level) {
        String sql = "SELECT waves FROM levels WHERE level_id = " + level + ";";
        int waves = 0;

        try (Statement stmt = conn.createStatement()) {
            waves = stmt.executeQuery(sql).getInt("waves");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return waves;
    }
}
