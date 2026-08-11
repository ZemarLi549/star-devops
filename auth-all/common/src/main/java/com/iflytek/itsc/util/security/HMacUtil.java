package com.iflytek.itsc.util.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * HMac工具类，替代讯飞内部的HMacUtil
 */
public class HMacUtil {

    /**
     * HMAC-MD5加密
     */
    public static String hMacMd5(String data) {
        try {
            // 默认使用固定密钥，实际应用中应该从配置中获取
            String key = "default_key"; // 这里使用固定密钥，实际应该从配置中读取
            Mac mac = Mac.getInstance("HmacMD5");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacMD5");
            mac.init(secretKeySpec);
            byte[] result = mac.doFinal(data.getBytes());
            
            // 转十六进制
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通用HMAC加密方法
     */
    public static String hmacEncode(String algorithm, String data, String key) {
        try {
            Mac mac = Mac.getInstance(algorithm);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), algorithm);
            mac.init(secretKeySpec);
            byte[] result = mac.doFinal(data.getBytes());
            
            // 转十六进制
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * HMAC-SHA1加密
     */
    public static String hMacSha1(String data, String key) {
        return hmacEncode("HmacSHA1", data, key);
    }

    /**
     * HMAC-SHA256加密
     */
    public static String hMacSha256(String data, String key) {
        return hmacEncode("HmacSHA256", data, key);
    }
}