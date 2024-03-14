package com.cs2212group9.typinggame.utils;

import com.cs2212group9.typinggame.db.User;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;


public class UserAuthenticator {
    private final String username;
    private final String password;
    private final User user;

    // number of bytes in pepper
    private final int pepperBytes = 2;

    public UserAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
        this.user = new User(username);
    }

    // check if password matches
    // requires a separate function because it will be hashed
    private boolean passwordMatches(String entered, String stored) throws NoSuchAlgorithmException {
        byte[] hashedPassword = hashBytesToBytes(entered.getBytes(StandardCharsets.UTF_8));
        for (int i = 0; i < Math.pow(2, 8 * pepperBytes); i++) { // 2^(n * 8), loops through all possible peppers
            // all strings of byte values
            byte[] pepper = new byte[pepperBytes];
            for (int j = 0; j < pepperBytes; j++) { // for each byte
                pepper[j] = (byte) ((i >> (8 * j)) & 0xFF); // shift and mask
            }
            byte[] combined = combineArrays(hashedPassword, pepper);
            if (hashBytesToString(combined).equals(stored)) {
                return true; // match found
            }
        }
        return false; // no match found for all possible peppers
    }

    // checks user/pw pair against DB
    public boolean authenticate() throws NoSuchAlgorithmException {
        return User.userExists()
            && passwordMatches(this.password, User.getUserPasswordHashed(this.username));
    }

    // adds user to DB, checks if already exists first
    public boolean register() {
        // check if in DB
        if (User.userExists()) {
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

    // hash password, add pepper, hash again
    private String pepperAndHash(String password) throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();

        byte[] pepper = new byte[pepperBytes];
        random.nextBytes(pepper);

        byte[] hashedPassword = hashBytesToBytes(password.getBytes(StandardCharsets.UTF_8));

        byte[] combined = combineArrays(hashedPassword, pepper);
        // System.out.println("combined: " + new String(combined));

        return hashBytesToString(combined);
    }

    private byte[] combineArrays(byte[] first, byte[] second) {
        byte[] combined = new byte[first.length + second.length];
        System.arraycopy(first, 0, combined, 0, first.length);
        System.arraycopy(second, 0, combined, first.length, second.length);
        return combined;
    }

    private String hashBytesToString(byte[] bytes) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        final byte[] hashbytes = digest.digest(bytes);
        return bytesToString(hashbytes);
    }

    private String bytesToString(byte[] bytes) {
        return new String(Hex.encode(bytes));
    }

    private byte[] hashBytesToBytes(byte[] bytes) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        return digest.digest(bytes);
    }
}
