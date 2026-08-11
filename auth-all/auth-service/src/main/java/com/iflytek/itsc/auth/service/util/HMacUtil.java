package com.iflytek.itsc.auth.service.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 本地版本的HMacUtil类，用于解决外部依赖缺失问题
 * 这个是一个简化版，提供所需的加密功能
 */
public class HMacUtil {
    
    /**
     * 使用MD5算法对字符串进行哈希计算
     * 这是一个简化实现，用于替代外部依赖的HMacUtil.hMacMd5方法
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
            // 在实际项目中应该有更好的错误处理
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}