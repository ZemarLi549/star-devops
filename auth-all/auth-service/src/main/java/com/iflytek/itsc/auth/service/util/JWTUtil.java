package com.iflytek.itsc.auth.service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类，使用JJWT库实现真实的JWT验证功能
 */
public class JWTUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);
    
    // JWT签名密钥，实际项目中应该从配置文件中读取并使用更安全的密钥
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    
    // 令牌前缀，实际项目中可以从配置文件读取
    private static final String TOKEN_PREFIX = "Bearer ";
    
    /**
     * 从token中获取用户账号
     */
    public static String getAccount(String token) {
        try {
            // 检查token是否为null或空字符串
            if (token == null || token.trim().isEmpty()) {
                logger.warn("Token is null or empty when getting account");
                return null;
            }
            
            // 移除可能的令牌前缀
            if (token.startsWith(TOKEN_PREFIX)) {
                token = token.substring(TOKEN_PREFIX.length());
            }
            
            Claims claims = parseToken(token);
            return claims != null ? claims.get("account", String.class) : null;
        } catch (Exception e) {
            logger.error("Failed to get account from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从token中获取用户ID
     */
    public static String getUserId(String token) {
        try {
            // 检查token是否为null或空字符串
            if (token == null || token.trim().isEmpty()) {
                logger.warn("Token is null or empty when getting userId");
                return null;
            }
            
            // 移除可能的令牌前缀
            if (token.startsWith(TOKEN_PREFIX)) {
                token = token.substring(TOKEN_PREFIX.length());
            }
            
            Claims claims = parseToken(token);
            return claims != null ? claims.get("userId", String.class) : null;
        } catch (Exception e) {
            logger.error("Failed to get userId from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从token中获取签发时间
     */
    public static long getIat(String token) {
        try {
            // 检查token是否为null或空字符串
            if (token == null || token.trim().isEmpty()) {
                logger.warn("Token is null or empty when getting iat");
                return 0;
            }
            
            // 移除可能的令牌前缀
            if (token.startsWith(TOKEN_PREFIX)) {
                token = token.substring(TOKEN_PREFIX.length());
            }
            
            Claims claims = parseToken(token);
            return claims != null ? claims.getIssuedAt().getTime() / 1000 : 0;
        } catch (Exception e) {
            logger.error("Failed to get iat from token: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 验证token是否有效
     */
    public static boolean verify(String token) {
        try {
            // 检查token是否为null或空字符串
            if (token == null || token.trim().isEmpty()) {
                logger.warn("Token is null or empty when verifying");
                return false;
            }
            
            // 移除可能的令牌前缀
            if (token.startsWith(TOKEN_PREFIX)) {
                token = token.substring(TOKEN_PREFIX.length());
            }
            
            // 解析token并验证签名和过期时间
            Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            logger.error("Token verification failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从token中获取过期时间
     */
    public static long getExp(String token) {
        try {
            // 检查token是否为null或空字符串
            if (token == null || token.trim().isEmpty()) {
                logger.warn("Token is null or empty when getting exp");
                return 0;
            }
            
            // 移除可能的令牌前缀
            if (token.startsWith(TOKEN_PREFIX)) {
                token = token.substring(TOKEN_PREFIX.length());
            }
            
            Claims claims = parseToken(token);
            return claims != null ? claims.getExpiration().getTime() / 1000 : 0;
        } catch (Exception e) {
            logger.error("Failed to get exp from token: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 生成JWT token
     */
    public static String sign(String userId, String account, Integer expireMinutes) {
        try {
            // 设置过期时间
            long now = System.currentTimeMillis();
            Date expireDate = new Date(now + expireMinutes * 60 * 1000);
            
            // 设置token的claims
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", userId);
            claims.put("account", account);
            
            // 生成token
            String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(expireDate)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                .compact();
            
            return token;
        } catch (Exception e) {
            logger.error("Failed to generate token: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 解析token获取claims
     */
    private static Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (Exception e) {
            logger.error("Failed to parse token: {}", e.getMessage());
            return null;
        }
    }
}