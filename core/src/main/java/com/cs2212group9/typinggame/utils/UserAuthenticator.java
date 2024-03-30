package com.cs2212group9.typinggame.utils;

import com.cs2212group9.typinggame.db.DBUser;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class UserAuthenticator {
    private final String username;
    private final String password;

    // number of bytes in pepper
    private static final int pepperBytes = 2;

    public UserAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // check if password matches
    // requires a separate function because it will be hashed
    /**
     * Checks if the entered password matches the stored password, cycles all possible peppers
     * @param entered - the entered unhashed password
     * @param stored - the stored hashed password
     * @return true if the entered password matches the stored password, false otherwise
     */
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
    /**
     * Authenticates the user against the database
     * @return true if the user exists and the password matches, false otherwise
     */
    public boolean authenticate() throws NoSuchAlgorithmException {
        return DBUser.userExists(this.username)
            && passwordMatches(this.password, DBUser.getUserPasswordHashed(this.username));
    }

    // adds user to DB, checks if already exists first
    /**
     * Registers the user in the database, returns false if they already exist
     * @return true if the user was successfully registered, false otherwise
     */
    public boolean register() {
        // check if in DB
        if (DBUser.userExists(this.username)) {
            return false;
        } else {
            String hashedPassword;
            try {
                hashedPassword = pepperAndHash(this.password);
            } catch (NoSuchAlgorithmException e) {
                // don't need to handle this, we can assume this error never gets thrown
                return false;
            }
            DBUser.addUser(this.username, hashedPassword);
        }
        return true;
    }

    // hash password, add pepper, hash again
    /**
     * Hashes the password with a pepper
     * SHA-3-256 is used for hashing, the plain password is hashed, then peppered, then hashed again
     * @param password - the password to be peppered and hashed
     * @return the peppered and hashed password
     */
    public static String pepperAndHash(String password) throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();

        byte[] pepper = new byte[pepperBytes];
        random.nextBytes(pepper);

        byte[] hashedPassword = hashBytesToBytes(password.getBytes(StandardCharsets.UTF_8));

        byte[] combined = combineArrays(hashedPassword, pepper);
        // System.out.println("combined: " + new String(combined));

        return hashBytesToString(combined);
    }

    /**
     * Combines two byte arrays into one
     * @param first - the first byte array
     * @param second - the second byte array
     * @return the combined byte array
     */
    private static byte[] combineArrays(byte[] first, byte[] second) {
        byte[] combined = new byte[first.length + second.length];
        System.arraycopy(first, 0, combined, 0, first.length);
        System.arraycopy(second, 0, combined, first.length, second.length);
        return combined;
    }

    /**
     * Converts a byte array to a string
     * @param bytes - the byte array to be converted
     * @return the string representation of the byte array
     */
    private static String hashBytesToString(byte[] bytes) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        final byte[] hashbytes = digest.digest(bytes);
        return bytesToString(hashbytes);
    }

    /**
     * Converts a byte array to a string
     * @param bytes - the byte array to be converted
     * @return the string representation of the byte array
     */
    private static String bytesToString(byte[] bytes) {
        return new String(Hex.encode(bytes));
    }

    /**
     * Hashes a byte array
     * @param bytes - the byte array to be hashed
     * @return the hashed byte array
     * @throws NoSuchAlgorithmException - if the algorithm is not found
     */
    private static byte[] hashBytesToBytes(byte[] bytes) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        return digest.digest(bytes);
    }
}
