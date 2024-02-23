package com.cs2212group9.typinggame.utils;

import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class UserAuthenticator {
    private String username;
    private String password;
    private DBHelper db;

    private final int pepperBytes = 2;

    public UserAuthenticator(DBHelper db, String username, String password) {
        this.username = username;
        this.password = password;
        this.db = db;
    }

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
        return this.db.userExists(this.username)
            && passwordMatches(this.password, this.db.getUserPasswordHashed(this.username));
    }

    // adds user to DB, checks if already exists first
    // use enum?
    public boolean register() {
        // check if in DB
        if (this.db.userExists(this.username)) {
            return false;
        } else {
            try {
                this.db.addUser(this.username, pepperAndHash(this.password));
            } catch (NoSuchAlgorithmException e) {
                // TODO: handle this
                e.printStackTrace();
            }
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
