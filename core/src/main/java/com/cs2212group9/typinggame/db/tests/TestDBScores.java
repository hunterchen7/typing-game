package com.cs2212group9.typinggame.db.tests;

import com.cs2212group9.typinggame.db.DBHelper;
import com.cs2212group9.typinggame.db.DBScores;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(OrderAnnotation.class)
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
    @Order(1)
    void testGetLevelMaxScore() throws InterruptedException {
        Thread.sleep(1000); // wait 1 second to not break primary key constraint
        DBScores.addScore("user", 1, 25);
        assertEquals(DBScores.getLevelMaxScore("user", 1), 25);
    }

    @Test
    @Order(2)
    void testHighestUnlockedLevel() {
        assertEquals(DBScores.highestUnlockedLevel("user"), 2);
    }

    @Test
    @Order(3)
    void testGetTopScores() {
        assertEquals(DBScores.getTopScores(1).getFirst().getScore(), "25");
    }

    @Test
    @Order(4)
    void testGetUserTotalScore1() {
        assertEquals(DBScores.getUserTotalScore("user"), 25);
    }

    @Test
    @Order(5)
    void testGetUserTotalScore2() {
        DBScores.addScore("user", 2, 30);
        assertEquals(DBScores.getUserTotalScore("user"), 55);
    }

    @Test
    @Order(6)
    void testGetTopLevelScore() {
        assertEquals(DBScores.getTopLevelScore(1).getScore(), "25");
    }

    @Test
    @Order(7)
    void testGetUserTopLevelScore() {
        assertEquals(DBScores.getUserTopLevelScore("user", 1).getScore(), "25");
    }

    @Test
    @Order(8)
    void testGetUserLevelPlays1() {
        assertEquals(DBScores.getUserLevelPlays("user", 1), 2);
    }

    @Test
    @Order(9)
    void testGetUserLevelPlays2() {
        assertEquals(DBScores.getUserLevelPlays("user", 2), 1);
    }

    @Test
    @Order(10)
    void testGetGamesPlayed() {
        assertEquals(DBScores.getGamesPlayed(), 3);
    }
}
