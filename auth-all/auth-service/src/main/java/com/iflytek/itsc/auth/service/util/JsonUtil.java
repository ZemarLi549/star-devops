package com.iflytek.itsc.auth.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/**
 * 本地版本的JsonUtil类，用于解决外部依赖缺失问题
 * 这个是一个简化版，提供所需的JSON处理功能
 */
public class JsonUtil {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 将对象转换为JSON字符串
     * 这是一个简化实现，使用Jackson库进行JSON序列化
     */
    public static String object2Json(Object object) {
        try {
            if (object == null) {
                return "null";
            }
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            // 在实际项目中应该有更好的错误处理
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }
    
    /**
     * 将JSON字符串转换为指定类型的对象
     * 这是一个简化实现，使用Jackson库进行JSON反序列化
     */
    public static <T> T json2Object(String json, Class<T> clazz) {
        try {
            if (json == null || json.isEmpty() || "null".equals(json)) {
                return null;
            }
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            // 在实际项目中应该有更好的错误处理
            throw new RuntimeException("Failed to convert JSON to object", e);
        }
    }
}