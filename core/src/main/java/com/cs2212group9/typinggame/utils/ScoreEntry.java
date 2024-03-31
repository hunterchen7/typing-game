package com.cs2212group9.typinggame.utils;

/**
 * Represents a score entry in the database.
 * @param user the user of the score entry
 * @param totalScore the total score of the user
 */
public record ScoreEntry(String user, int totalScore) {
    /**
     * Returns a string representation of the score entry.
     * @return a string representation of the score entry
     */
    @Override
    public String toString() {
        return user + ": " + totalScore;
    }

    /**
     * Returns the user of the score entry.
     * @return the user of the score entry
     */
    public String getScore() {
        return Integer.toString(totalScore);
    }
}
