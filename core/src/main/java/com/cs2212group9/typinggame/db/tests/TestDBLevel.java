package com.cs2212group9.typinggame.db.tests;


import com.cs2212group9.typinggame.db.DBHelper;
import com.cs2212group9.typinggame.db.DBLevel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void TestLevelCount() {
        Assertions.assertEquals(2, DBLevel.getLevelCount());
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
