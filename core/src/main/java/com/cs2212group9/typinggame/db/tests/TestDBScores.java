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

/**
 * This class conducts unit tests for the {@link DBScores} class, focusing on validating
 * the functionality related to managing and querying game scores in the database.
 */
@TestMethodOrder(OrderAnnotation.class)
public class TestDBScores {
    /** Establishes a database connection through the DBHelper utility class. */
    static final Connection db = DBHelper.getConnection();

    /**
     * Prepares the testing environment by reinitializing the scores table and populating it with initial test data.
     * This setup method is executed once before all test methods.
     */
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
        // Insert an initial score to facilitate testing.
        DBScores.addScore("user", 1, 12);
    }

    /**
     * Tests the retrieval of the maximum score achieved by a user on a specific level.
     * Ensures the functionality accounts for multiple score submissions for the same level.
     * @throws InterruptedException if the test execution is unexpectedly interrupted.
     */
    @Test
    @Order(1)
    void testGetLevelMaxScore() throws InterruptedException {
        Thread.sleep(1000); // wait 1 second to not break primary key constraint
        DBScores.addScore("user", 1, 25);
        assertEquals(DBScores.getLevelMaxScore("user", 1), 25);
    }

    /**
     * Verifies the correct calculation of the highest level unlocked by a user,
     * based on the scores recorded in the database.
     */
    @Test
    @Order(2)
    void testHighestUnlockedLevel() {
        assertEquals(DBScores.highestUnlockedLevel("user"), 2);
    }

    /**
     * Checks the functionality for fetching the top scores for a specific level.
     * Validates that the scores are correctly ordered and retrieved.
     */
    @Test
    @Order(3)
    void testGetTopScores() {
        assertEquals(DBScores.getTopScores(1).getFirst().getScore(), "25");
    }

    /**
     * Tests the calculation of a user's total score across all levels.
     */
    @Test
    @Order(4)
    void testGetUserTotalScore1() {
        assertEquals(DBScores.getUserTotalScore("user"), 25);
    }

    /**
     * Validates the update of a user's total score upon the addition of new scores for different levels.
     */
    @Test
    @Order(5)
    void testGetUserTotalScore2() {
        DBScores.addScore("user", 2, 30);
        assertEquals(DBScores.getUserTotalScore("user"), 55);
    }

    /**
     * Tests the retrieval of the highest score achieved across all users for a given level.
     */
    @Test
    @Order(6)
    void testGetTopLevelScore() {
        assertEquals(DBScores.getTopLevelScore(1).getScore(), "25");
    }

    /**
     * Assesses the functionality for fetching the highest score achieved by a specific user on a given level.
     */
    @Test
    @Order(7)
    void testGetUserTopLevelScore() {
        assertEquals(DBScores.getUserTopLevelScore("user", 1).getScore(), "25");
    }

    /**
     * Evaluates the count of attempts a user has made on a specific level.
     */
    @Test
    @Order(8)
    void testGetUserLevelPlays1() {
        assertEquals(DBScores.getUserLevelPlays("user", 1), 2);
    }

    /**
     * Further tests the attempt count functionality by examining plays on another level.
     */
    @Test
    @Order(9)
    void testGetUserLevelPlays2() {
        assertEquals(DBScores.getUserLevelPlays("user", 2), 1);
    }

    /**
     * Verifies the correct calculation of the total number of games played by all users.
     */
    @Test
    @Order(10)
    void testGetGamesPlayed() {
        assertEquals(DBScores.getGamesPlayed(), 3);
    }
}
