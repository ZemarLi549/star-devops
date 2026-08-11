package com.iflytek.itsc.auth.resource.manager.common.interceptor;

import com.iflytek.itsc.auth.resource.manager.common.annotation.CipherData;
import com.iflytek.itsc.auth.resource.manager.common.annotation.CipherField;
import com.iflytek.itsc.auth.resource.manager.common.utils.nativeCipher.SM4CBCUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.List;

/**
 * @Classname DecodeInterceptor
 * @Description 解密拦截器
 * @Date 2024/5/9 14:23
 * @Created by wxqiu
 */
@Component
@Intercepts({
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = Statement.class)
})
@Slf4j
@ConditionalOnExpression("'${security.select}'.equals('san') || '${security.select}'.equals('haitai')")
public class DecodeInterceptor implements Interceptor {


    @Resource
    SM4CBCUtil sm4CBCUtil;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        List<Object> resultObject = (List<Object>) invocation.proceed();
        if (resultObject == null || resultObject.isEmpty()) {
            return resultObject;
        }
        try {
            // 获取结果集的类型
            DefaultResultSetHandler defaultResultSetHandler = (DefaultResultSetHandler) invocation.getTarget();
            MetaObject metaObject = SystemMetaObject.forObject(defaultResultSetHandler);
            MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("mappedStatement");
            List<ResultMap> resultMaps = mappedStatement.getResultMaps();
            Class<?> resultType = resultMaps.get(0).getType();
            boolean isContain = resultType.isAnnotationPresent(CipherData.class);
            if (isContain) {
                for (Object item : resultObject) {
                    this.deal(item);
                }
            }
        } catch (Exception e) {
            log.error("intercept error", e);
        }
        return resultObject;
    }

    /**
     * 处理结果集
     *
     * @param data: 待处理的数据
     **/
    protected void deal(Object data) throws IllegalAccessException {
        Field[] fields = data.getClass().getDeclaredFields();
        for (Field field : fields) {
            boolean isContainCiphertextField = field.isAnnotationPresent(CipherField.class);
            if (isContainCiphertextField) {
                field.setAccessible(true);
                Object o = field.get(data);
                if (o != null) {
                    String content = sm4CBCUtil.decode(String.valueOf(o));
                    field.set(data, content);
                }
            }
        }
    }
}

