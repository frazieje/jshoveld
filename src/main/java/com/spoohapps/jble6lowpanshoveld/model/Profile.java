package com.spoohapps.jble6lowpanshoveld.model;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class Profile {

    private static final int PROFILE_ID_LENGTH = 8;

    private String id;

    private Profile(String id) {
        this.id = id;
    }

    public static Profile generate() throws NoSuchAlgorithmException {
        MessageDigest salt = MessageDigest.getInstance("SHA-256");
        salt.update(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        return new Profile(bytesToHex(salt.digest()).substring(0, PROFILE_ID_LENGTH));
    }

    private final static String hexString = "0123456789abcdef";

    private final static char[] hexArray = hexString.toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static Profile from(String id) {
        if (id == null || id.length() != PROFILE_ID_LENGTH)
            throw new IllegalArgumentException();

        String lower = id.toLowerCase();

        for (int i = 0; i < lower.length(); i++) {
            if (!hexString.contains(lower.substring(i, i+1))) {
                throw new IllegalArgumentException();
            }
        }

        return new Profile(lower);
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object other) {
        Profile otherProfile = (Profile)other;
        if (other == null)
            return false;
        return this.toString().equals(otherProfile.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }
}
