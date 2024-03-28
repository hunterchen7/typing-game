package com.cs2212group9.typinggame.db.tests;

import com.cs2212group9.typinggame.db.DBHelper;
import com.cs2212group9.typinggame.db.DBScores;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDBScores {
    static final Connection db = DBHelper.getConnection();
    @BeforeAll
    static void setup() {
        System.out.println("Setting up");
        // delete table
        String drop = "DROP TABLE IF EXISTS scores;";
        // create table
        String create = """
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
            """;
        try (var stmt = db.createStatement()) {
            stmt.executeUpdate(drop);
            stmt.executeUpdate(create);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        DBScores.addScore("user", 1, 12);
    }

    @Test
    void testGetLevelMaxScore() throws InterruptedException {
        Thread.sleep(1000); // wait 1 second to not break primary key constraint
        DBScores.addScore("user", 1, 25);
        assertEquals(DBScores.getLevelMaxScore("user", 1), 25);
    }

    @Test
    void testHighestUnlockedLevel() {
        assertEquals(DBScores.highestUnlockedLevel("user"), 2);
    }
}
