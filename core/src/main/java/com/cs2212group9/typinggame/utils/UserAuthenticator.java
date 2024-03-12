package com.cs2212group9.typinggame.utils;

import com.cs2212group9.typinggame.db.User;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;


public class UserAuthenticator {
    private String username;
    private String password;

    private final int pepperBytes = 2;

    public UserAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // check if password matches
    // requires a separate function because it will be hashed
    private boolean passwordMatches(String entered, String stored) throws NoSuchAlgorithmException {
        for (int i = 0; i < Math.pow(2, 8 * pepperBytes); i++) { // 2^(n * 8), loops through all possible peppers
            // all strings of byte values
            byte[] pepper = new byte[pepperBytes];
            for (int j = 0; j < pepperBytes; j++) { // yay bit math
                pepper[j] = (byte) ((i >> (8 * j)) & 0xFF); // shift and mask
            }
            byte[] combined = combineArrays(entered.getBytes(StandardCharsets.UTF_8), pepper);
            if (hashBytes(combined).equals(stored)) {
                // System.out.println("combined: " + new String(combined));
                return true;
            }
        }
        return false; // no match found for all possible peppers
    }

    // checks user/pw pair against DB
    public boolean authenticate() throws NoSuchAlgorithmException {
        return User.userExists(this.username)
            && passwordMatches(this.password, User.getUserPasswordHashed(this.username));
    }

    // adds user to DB, checks if already exists first
    public boolean register() {
        // check if in DB
        if (User.userExists(this.username)) {
            return false;
        } else {
            String hashedPassword;
            try {
                hashedPassword = pepperAndHash(this.password);
            } catch (NoSuchAlgorithmException e) {
                // don't need to handle this, we can assume this error never gets thrown
                return false;
            }
            User.addUser(this.username, hashedPassword);
        }
        return true;
    }

    private String pepperAndHash(String password) throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();

        byte[] pepper = new byte[pepperBytes];
        random.nextBytes(pepper);

        byte[] combined = combineArrays(password.getBytes(StandardCharsets.UTF_16), pepper);
        // System.out.println("combined: " + new String(combined));

        return hashBytes(combined);
    }

    private byte[] combineArrays(byte[] first, byte[] second) {
        byte[] combined = new byte[first.length + second.length];
        System.arraycopy(first, 0, combined, 0, first.length);
        System.arraycopy(second, 0, combined, first.length, second.length);
        return combined;
    }

    private String hashBytes(byte[] bytes) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        final byte[] hashbytes = digest.digest(bytes);
        return new String(Hex.encode(hashbytes));
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        return hashBytes(password.getBytes(StandardCharsets.UTF_16));
    }

}
