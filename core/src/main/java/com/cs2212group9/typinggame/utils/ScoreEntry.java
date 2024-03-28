package com.cs2212group9.typinggame.utils;

public record ScoreEntry(String user, int totalScore) {
    @Override
    public String toString() {
        return user + ": " + totalScore;
    }
}
