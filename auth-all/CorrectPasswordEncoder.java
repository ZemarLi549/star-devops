package com.iflytek.itsc.auth.service.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A utility class to demonstrate the correct password encryption process
 * based on the system's implementation
 */
public class CorrectPasswordEncoder {
    
    /**
     * Main method to demonstrate correct password encryption process
     */
    public static void main(String[] args) {
        String plainPassword = "Cxmt#666";
        
        // Step 1: Get MD5 uppercase (as per MD5Util.getMD5)
        String md5UpperCase = getMD5(plainPassword);
        System.out.println("Step 1 - MD5 uppercase: " + md5UpperCase);
        
        // Step 2: Convert to lowercase
        String md5LowerCase = md5UpperCase.toLowerCase();
        System.out.println("Step 2 - MD5 lowercase: " + md5LowerCase);
        
        // Step 3: Apply hMacMd5 (which is actually standard MD5 in this implementation)
        String finalEncryptedPassword = hMacMd5(md5LowerCase);
        System.out.println("Step 3 - Final encrypted password (stored in database): " + finalEncryptedPassword);
        
        // Verify what would happen during login
        String loginEncryptedPassword = hMacMd5(plainPassword);
        System.out.println("Login verification uses: " + loginEncryptedPassword);
        System.out.println("Do they match? " + finalEncryptedPassword.equals(loginEncryptedPassword));
        System.out.println("\nFINAL_RESULT: " + finalEncryptedPassword);
    }
    
    /**
     * Mimics MD5Util.getMD5 method
     * Returns uppercase MD5 hash
     */
    public static String getMD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) i += 256;
                if (i < 16) buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            // 32-bit encryption - uppercase
            return buf.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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