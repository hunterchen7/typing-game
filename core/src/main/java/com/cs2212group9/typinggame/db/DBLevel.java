package com.cs2212group9.typinggame.db;

import com.badlogic.gdx.utils.Array;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

// class for retrieving level data
// this class does not require setters, level data should be preset
public class DBLevel {
    static Connection conn = DBHelper.getConnection();

    /** @return the total number of levels */
    public static int getLevelCount() {
        String sql = "SELECT COUNT(*) FROM levels;";
        int count = 0;

        try (Statement stmt = conn.createStatement()) {
            count = stmt.executeQuery(sql).getInt(1);
            System.out.println("count: " + count);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return count;
    }

    // returns the words of a level

    /**
     * Return a list of words that belongs to a level
     * @param level - the level to get the words from
     * @return an array of words that belongs to a level
     */
    public static Array<String> getLevelWords(int level) {
        System.out.println("getting words for level " + level);
        String sql = "SELECT words FROM levels WHERE level_id = " + level + ";";
        String words = null;

        try (Statement stmt = conn.createStatement()) {
            words = stmt.executeQuery(sql).getString("words");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        assert words != null;
        Array<String> wordPool = new Array<>(words.split(","));
        return wordPool;
    }

    // returns the difficulty of a level

    /**
     * Return the difficulty of a level
     * @param level - the level to get the difficulty from
     * @return the difficulty of a level as an integer
     */
    public static int getLevelDifficulty(int level) {
        String sql = "SELECT difficulty FROM levels WHERE level_id = " + level + ";";
        int difficulty = 0;

        try (Statement stmt = conn.createStatement()) {
            difficulty = stmt.executeQuery(sql).getInt("difficulty");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return difficulty;
    }

    /**
     * Return the number of waves in a level
     * @param level - the level to get the number of waves from
     * @return the number of waves in a level as an integer
     */
    public static int getLevelWaves(int level) {
        String sql = "SELECT waves FROM levels WHERE level_id = " + level + ";";
        int waves = 0;

        try (Statement stmt = conn.createStatement()) {
            waves = stmt.executeQuery(sql).getInt("waves");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return waves;
    }

    /**
     * Return the minimum scores for each level as a dictionary
     * @return the minimum score needed for levels as an integer
     */
    public static HashMap<Integer, Integer> getMinScores() {
        String sql = "SELECT level_id, min_score FROM levels;";

        HashMap<Integer, Integer> minScores = new HashMap<>();

        try (Statement stmt = conn.createStatement()) {
            var results = stmt.executeQuery(sql);
            while (results.next()) {
                minScores.put(results.getInt("level_id"), results.getInt("min_score"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return minScores;
    }
}
