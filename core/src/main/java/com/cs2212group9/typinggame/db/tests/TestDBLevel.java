package com.cs2212group9.typinggame.db.tests;


import com.cs2212group9.typinggame.db.DBHelper;
import com.cs2212group9.typinggame.db.DBLevel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;



public class TestDBLevel {
    static final Connection db = DBHelper.getConnection();

    @BeforeAll
    public static void setup() {
        System.out.println("Setting up");
        // delete table
        String drop = "DROP TABLE IF EXISTS levels;";
        // create table
        String create = """
            CREATE TABLE IF NOT EXISTS levels (
                level_id INTEGER PRIMARY KEY,
                words TEXT,
                difficulty INTEGER,
                waves INTEGER,
                min_score INTEGER
            );
        """;
        String sql1 = """
            INSERT INTO levels (level_id, words, difficulty, waves, min_score)
            VALUES (1, 'test1,test2,test3', 1, 3, 10);
        """;
        String sql2 = """
            INSERT INTO levels (level_id, words, difficulty, waves, min_score)
            VALUES (2, 'test4,test5,test6,test7', 2, 5, 10);
        """;
        try (Statement stmt = db.createStatement()) {
            stmt.executeUpdate(drop);
            stmt.executeUpdate(create);
            stmt.executeUpdate(sql1);
            stmt.executeUpdate(sql2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testGetLevelDifficulty() {
        assertEquals(1, DBLevel.getLevelDifficulty(1));
    }

    @Test
    public void testGetLevelWaves() {
        assertEquals(3, DBLevel.getLevelWaves(1));
    }

    @Test
    public void testInvalidLevelDifficulty() {
        int invalidLevelId = 999;
        int difficulty = DBLevel.getLevelDifficulty(invalidLevelId);
        // Zero as invalid level difficulty
        assertEquals(0, difficulty, "Difficulty for invalid level ID should be -1.");
    }

    @Test
    public void testInvalidLevelWaves() {
        int invalidLevelId = 999;
        int waves = DBLevel.getLevelWaves(invalidLevelId);
        // Zero as invalid level difficulty
        assertEquals(0, waves, "Waves for invalid level ID should be -1.");
    }

    @Test
    public void TestLevelCount() {
        Assertions.assertEquals(2, DBLevel.levelCount());
    }

    @Test
    public void TestGetLevelWords1() {
        assertEquals(3, DBLevel.getLevelWords(1).size);
    }

    @Test
    public void TestGetLevelWords2() {
        assertEquals(4, DBLevel.getLevelWords(2).size);
    }
}
