package com.iflytek.itsc.auth.resource.manager.common.utils;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 对象拷贝工具实现类，去掉了dozer组件，改用hutool工具中的BeanUtil.copyProperties来实现
 * 注意：这里是浅拷贝
 * @ClassName DozerBeanUtil
 * @Description Bean工具类
 **/
public class DozerBeanUtil {
    private static final Logger logger = LoggerFactory.getLogger(DozerBeanUtil.class);

    public static <T, S> Page<T> convert(Page<S> s, Class<T> clz) {
        if (s == null) {
            return null;
        }
        Page<T> result = new Page<T>();
        result.setSize(s.getSize());
        result.setCurrent(s.getCurrent());
        convert(s, result);
        List<S> list = s.getRecords();
        if (list != null) {
            result.setRecords(convert(list, clz));
        }
        return result;
    }

    /**
     * 单个对象的浅复制及类型转换，vo/domain , po
     * @param s   数据对象
     * @param clz 复制目标类型
     * @return
     */
    public static <T, S> T convert(S s, Class<T> clz) {
        if (s == null) {
            return null;
        }
        try {
            Object dest = clz.getDeclaredConstructor().newInstance();
            BeanUtil.copyProperties(s, dest);
            return (T)dest;
        } catch (InstantiationException e) {
            logger.error("转换异常",e);
        } catch (IllegalAccessException e) {
            logger.error("转换异常",e);
        } catch (InvocationTargetException e) {
            logger.error("转换异常",e);
        } catch (NoSuchMethodException e) {
            logger.error("转换异常",e);
        }
        return null;
    }

    /**
     * 单个对象的浅复制及类型转换
     * @Description: 单个对象的深度复制及类型转换
     * @return: void
     **/
    public static void convert(Object source, Object dest) {
        BeanUtil.copyProperties(source, dest);
    }

    /**
     * list浅复制
     * @param s   数据对象
     * @param clz 复制目标类型
     */
    public static <T, S> List<T> convert(List<S> s, Class<T> clz) {
        if (s == null) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>();
        for (S vs : s) {
            try {
                Object dest = clz.getDeclaredConstructor().newInstance();
                BeanUtil.copyProperties(vs, dest);
                list.add((T)dest);
            } catch (InstantiationException e) {
                logger.error("转换异常",e);
            } catch (IllegalAccessException e) {
                logger.error("转换异常",e);
            } catch (InvocationTargetException e) {
                logger.error("转换异常",e);
            } catch (NoSuchMethodException e) {
                logger.error("转换异常",e);
            }
        }
        return list;
    }

    /*
    /**
     * set深度复制
     * @param s   数据对象
     * @param clz 复制目标类型
     *//*
    public static <T, S> Set<T> convert(Set<S> s, Class<T> clz) {
        if (s == null) {
            return Collections.emptySet();
        }
        Set<T> set = new HashSet<>();
        for (S vs : s) {
            set.add(dozerMapper.map(vs, clz));
        }
        return set;
    }

    /**
     * 数组深度复制
     * @param s   数据对象
     * @param clz 复制目标类型
     * @return
     * @Description: 数组深度复制
     * @author banjuer@outlook.com
     * @Time 2018年5月9日 下午3:54:57
     *//*
    public static <T, S> T[] convert(S[] s, Class<T> clz) {
        if (s == null) {
            return (T[]) Collections.emptyList().toArray();
        }
        @SuppressWarnings("unchecked")
        T[] arr = (T[]) Array.newInstance(clz, s.length);
        for (int i = 0; i < s.length; i++) {
            arr[i] = dozerMapper.map(s[i], clz);
        }
        return arr;
    }*/
}

