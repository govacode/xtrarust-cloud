package com.xtrarust.cloud.common.util.object;

import cn.hutool.core.bean.BeanUtil;
import com.xtrarust.cloud.common.domain.PageResult;
import com.xtrarust.cloud.common.util.collection.CollectionUtils;

import java.util.List;
import java.util.function.Consumer;

/**
 * Bean 工具类
 */
public class BeanUtils {

    public static <T> T toBean(Object source, Class<T> targetClass) {
        return BeanUtil.toBean(source, targetClass);
    }

    public static <T> T toBean(Object source, Class<T> targetClass, Consumer<T> peek) {
        T target = toBean(source, targetClass);
        if (target != null) {
            peek.accept(target);
        }
        return target;
    }

    public static <S, T> List<T> toBean(List<S> source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        return CollectionUtils.convertList(source, s -> toBean(s, targetType));
    }

    public static <S, T> List<T> toBean(List<S> source, Class<T> targetType, Consumer<T> peek) {
        List<T> list = toBean(source, targetType);
        if (list != null) {
            list.forEach(peek);
        }
        return list;
    }

    public static <S, T> PageResult<T> toBean(PageResult<S> source, Class<T> targetType) {
        return toBean(source, targetType, null);
    }

    public static <S, T> PageResult<T> toBean(PageResult<S> source, Class<T> targetType, Consumer<T> peek) {
        if (source == null) {
            return null;
        }
        List<T> list = toBean(source.getRecords(), targetType);
        if (peek != null) {
            list.forEach(peek);
        }
        return new PageResult<>(source.getTotal(), list);
    }

}