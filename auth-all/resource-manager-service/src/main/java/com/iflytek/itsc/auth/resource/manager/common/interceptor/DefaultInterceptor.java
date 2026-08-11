package com.iflytek.itsc.auth.resource.manager.common.interceptor;

import com.iflytek.itsc.auth.resource.manager.common.annotation.CipherData;
import com.iflytek.itsc.auth.resource.manager.common.annotation.CipherField;
import com.iflytek.itsc.auth.resource.manager.common.utils.nativeCipher.SM3HMACUtil;
import com.iflytek.itsc.auth.resource.manager.common.utils.nativeCipher.SM4CBCUtil;
import com.iflytek.itsc.auth.common.exception.BaseBizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Classname DefaultInterceptor
 * @Description 默认加密拦截器
 * @Date 2024/5/13 10:39
 * @Created by wxqiu
 */
@Component
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
@Slf4j
@ConditionalOnExpression("'${security.select}'.equals('san') || '${security.select}'.equals('haitai')")
public class DefaultInterceptor implements Interceptor {

    @Resource
    SM3HMACUtil sm3HMACUtil;

    @Resource
    SM4CBCUtil sm4CBCUtil;

    private final Map<String, Method> mappedStatementMethodCache = new ConcurrentHashMap<>();


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            Object target = invocation.getTarget();
            Object[] args = invocation.getArgs();
            if (target instanceof Executor) {
                MappedStatement ms = (MappedStatement) args[0];
                Object parameterObject = args[1];
                //更新|新增 注解识别加密
                annotationEncrypt(ms, parameterObject);
            }
        } catch (Exception e) {
            log.error("intercept error", e);
        }
        //执行SQL方法
        return invocation.proceed();
    }

    private void annotationEncrypt(MappedStatement ms, Object parameterObject) {
        Method mapperMethod = getMethodByMappedStatementId(ms.getId());
        ParamNameResolver paramNameResolver = new ParamNameResolver(ms.getConfiguration(), mapperMethod);
        String[] paramNames = paramNameResolver.getNames();
        Parameter[] parameters = mapperMethod.getParameters();
        if (parameterObject instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap parameterMap = (MapperMethod.ParamMap) parameterObject;
            for (int i = 0; i < parameters.length; i++) {
                String paramName = paramNames[i];
                Object paramValue = parameterMap.get(paramName);
                if (paramValue == null) {
                    continue;
                }
                //
                //todo 将字段加密后查询  现在没有用email 和phone 字段作为查询条件的可以先不写
                //只能拦截以对象形式的新增和更新 lambdaUpdate() 拦截不了
                //处理对象字段加密
                encryptObjField(paramValue);
            }
        } else {
            //处理对象字段加密
            encryptObjField(parameterObject);
        }

    }

    private Method getMethodByMappedStatementId(String mappedStatementId) {
        return mappedStatementMethodCache.computeIfAbsent(mappedStatementId, id -> {
            int typeMethodSpiltIndex = id.lastIndexOf(".");
            String mapperClassName = id.substring(0, typeMethodSpiltIndex);
            String mapperMethodName = id.substring(typeMethodSpiltIndex + 1);
            try {
                Class<?> mapperClass = Class.forName(mapperClassName);
                for (Method method : mapperClass.getMethods()) {
                    if (method.getName().equals(mapperMethodName)) {
                        return method;
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new BaseBizException("获取mapper class失败");
            }
            throw new BaseBizException("获取mapper method失败");
        });
    }

    public void encryptObjField(Object object) {
        if (object instanceof Map) {
            for (Object o : ((Map<?, ?>) object).values()) {
                encryptObjField(o);
            }
        } else if (object instanceof Collection) {
            for (Object o : ((Collection<?>) object)) {
                encryptObjField(o);
            }
        } else if (object.getClass().isArray()) {
            for (Object o : ((Object[]) object)) {
                encryptObjField(o);
            }
        } else {
            try {
                String originSign = sm3HMACUtil.getOriginSign(object);
                if (StringUtils.isNotBlank(originSign)) {
                    String sign = sm3HMACUtil.integralityEncode(originSign);
                    Field field = object.getClass().getDeclaredField("sign");
                    field.setAccessible(true);
                    field.set(object, sign);
                }
                boolean isContain = object.getClass().isAnnotationPresent(CipherData.class);
                if (isContain) {
                    for (Field field : object.getClass().getDeclaredFields()) {
                        Boolean isCipherDataPresent = field.isAnnotationPresent(CipherField.class);
                        if (isCipherDataPresent) {
                            field.setAccessible(true);
                            Object fieldValue;
                            fieldValue = field.get(object);
                            if (fieldValue == null) {
                                continue;
                            }
                            if (fieldValue instanceof String) {
                                String encryptData = sm4CBCUtil.encode(fieldValue.toString());
                                field.set(object, encryptData);
                            }
                        }
                    }
                }

            } catch (Exception e) {
                log.error("encryptObjField error", e);
            }
        }
    }
}
