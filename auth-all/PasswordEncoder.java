package com.iflytek.itsc.auth.service.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A utility class to demonstrate password encryption process
 * based on the HMacUtil implementation in auth-service module
 */
public class PasswordEncoder {
    
    /**
     * Main method to demonstrate password encryption
     */
    public static void main(String[] args) {
        String plainPassword = "Cxmt#666";
        String encryptedPassword = hMacMd5(plainPassword);
        
        System.out.println("Plain Password: " + plainPassword);
        System.out.println("Encrypted Password (MD5): " + encryptedPassword);
        System.out.println("FINAL_RESULT: " + encryptedPassword);
    }
    
    /**
     * Mimics the hMacMd5 method from auth-service's HMacUtil
     * This is a simplified implementation that performs standard MD5 hashing
     */
    public static String hMacMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}