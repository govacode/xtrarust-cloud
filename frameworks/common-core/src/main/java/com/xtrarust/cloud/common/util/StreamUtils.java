package com.xtrarust.cloud.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * stream 流工具类
 *
 * @author gova
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StreamUtils {

    /**
     * 集合过滤
     *
     * @param collection 需要转化的集合
     * @param predicate  过滤方法
     * @return 过滤后的 List 列表
     */
    public static <E> List<E> filter(Collection<E> collection, Predicate<E> predicate) {
        if (CollUtil.isEmpty(collection)) {
            return CollUtil.newArrayList();
        }
        return collection.stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * 找到流中满足条件的第一个元素
     *
     * @param collection 需要查询的集合
     * @param predicate  过滤方法
     * @return 找到符合条件的第一个元素，没有则返回 Optional.empty()
     */
    public static <E> Optional<E> findFirst(Collection<E> collection, Predicate<E> predicate) {
        if (CollUtil.isEmpty(collection)) {
            return Optional.empty();
        }
        return collection.stream().filter(predicate).findFirst();
    }

    /**
     * 找到流中满足条件的第一个元素值
     *
     * @param collection 需要查询的集合
     * @param predicate  过滤方法
     * @return 找到符合条件的第一个元素，没有则返回 null
     */
    public static <E> E findFirstValue(Collection<E> collection, Predicate<E> predicate) {
        return findFirst(collection, predicate).orElse(null);
    }

    /**
     * 找到流中任意一个满足条件的元素
     *
     * @param collection 需要查询的集合
     * @param predicate  过滤方法
     * @return 找到符合条件的任意一个元素，没有则返回 Optional.empty()
     */
    public static <E> Optional<E> findAny(Collection<E> collection, Predicate<E> predicate) {
        if (CollUtil.isEmpty(collection)) {
            return Optional.empty();
        }
        return collection.stream().filter(predicate).findAny();
    }

    /**
     * 找到流中任意一个满足条件的元素值
     *
     * @param collection 需要查询的集合
     * @param function   过滤方法
     * @return 找到符合条件的任意一个元素，没有则返回null
     */
    public static <E> E findAnyValue(Collection<E> collection, Predicate<E> function) {
        return findAny(collection, function).orElse(null);
    }

    /**
     * 将集合按,分隔符拼接成字符串
     *
     * @param collection 需要转化的集合
     * @param mapper     映射函数
     * @return 拼接后的字符串
     */
    public static <E> String join(Collection<E> collection, Function<E, String> mapper) {
        return join(collection, mapper, StringUtils.COMMA);
    }

    /**
     * 将集合按指定的分割拼接成字符串
     *
     * @param collection 需要转化的集合
     * @param mapper     映射函数
     * @param delimiter  分隔符
     * @return 拼接后的字符串
     */
    public static <E> String join(Collection<E> collection, Function<E, String> mapper, CharSequence delimiter) {
        if (CollUtil.isEmpty(collection)) {
            return StringUtils.EMPTY;
        }
        return collection.stream()
                .map(mapper)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(delimiter));
    }

    /**
     * 集合排序
     *
     * @param collection 需要排序的集合
     * @param comparator 集合元素比较器
     * @return 排序后的列表
     */
    public static <E> List<E> sorted(Collection<E> collection, Comparator<E> comparator) {
        if (CollUtil.isEmpty(collection)) {
            return CollUtil.newArrayList();
        }
        return collection.stream()
                .filter(Objects::nonNull)
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /**
     * 将集合转化为类型不变的 Map<br>
     * <B>{@code Collection<V> ----> Map<K, V>}</B>
     *
     * @param collection 需要转化的集合
     * @param keyMapper  V类型转化为K类型的 lambda 方法
     * @param <V>        集合元素类型
     * @param <K>        Map key类型
     * @return 转化后的 Map
     */
    public static <V, K> Map<K, V> toIdentityMap(Collection<V> collection, Function<V, K> keyMapper) {
        if (CollUtil.isEmpty(collection)) {
            return MapUtil.newHashMap();
        }
        return collection.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(keyMapper, Function.identity(), (v1, v2) -> v1));
    }

    /**
     * 将集合转化为Map(注意：valueMapper映射后的value为null时会抛NullPointerException)<br>
     * <B>{@code Collection<E> -----> Map<K, V>}</B>
     *
     * @param collection  需要转化的集合
     * @param keyMapper   E类型转化为K类型的 lambda 方法
     * @param valueMapper E类型转化为V类型的 lambda 方法
     * @param <E>         集合元素类型
     * @param <K>         Map key类型
     * @param <V>         Map value类型
     * @return 转化后的 Map
     */
    public static <E, K, V> Map<K, V> toMap(Collection<E> collection, Function<E, K> keyMapper, Function<E, V> valueMapper) {
        if (CollUtil.isEmpty(collection)) {
            return MapUtil.newHashMap();
        }
        return collection.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(keyMapper, valueMapper, (v1, v2) -> v1));
    }

    /**
     * 将集合转化为Map(注意：由于未直接使用Collectors#toMap()方法因此时空安全的)<br>
     * <B>{@code Collection<E> -----> Map<K, V>}</B>
     *
     * @param collection  需要转化的集合
     * @param keyMapper   E类型转化为K类型的 lambda 方法
     * @param valueMapper E类型转化为V类型的 lambda 方法
     * @param <E>         集合元素类型
     * @param <K>         Map key类型
     * @param <V>         Map value类型
     * @return 转化后的 Map
     */
    public static <E, K, V> Map<K, V> nullSafeToMap(Collection<E> collection, Function<E, K> keyMapper, Function<E, V> valueMapper) {
        if (CollUtil.isEmpty(collection)) {
            return MapUtil.newHashMap();
        }
        return collection.stream()
                .collect(
                        LinkedHashMap::new,
                        (map, e) -> map.put(keyMapper.apply(e), valueMapper.apply(e)),
                        Map::putAll
                );
    }

    /**
     * 获取 Map 中的数据作为新 Map 的 value(key 不变)
     *
     * @param map        需要转化的 Map
     * @param biFunction 取值函数
     * @param <K>        Map key类型
     * @param <E>        Map value类型
     * @param <V>        新 Map 中的value类型
     * @return 转化后的 Map
     */
    public static <K, E, V> Map<K, V> convertMap(Map<K, E> map, BiFunction<K, E, V> biFunction) {
        if (CollUtil.isEmpty(map)) {
            return MapUtil.newHashMap();
        }
        return toMap(map.entrySet(), Map.Entry::getKey, entry -> biFunction.apply(entry.getKey(), entry.getValue()));
    }

    /**
     * 将集合按照规则分组收集为 Map<br>
     * <B>{@code Collection<E> -----> Map<K, List<E>>} </B>
     *
     * @param collection 需要分类的集合
     * @param classifier 分类的规则
     * @param <E>        集合元素类型
     * @param <K>        Map key类型
     * @return 分组后的 Map
     */
    public static <E, K> Map<K, List<E>> groupByKey(Collection<E> collection, Function<E, K> classifier) {
        if (CollUtil.isEmpty(collection)) {
            return MapUtil.newHashMap();
        }
        return collection.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(classifier, LinkedHashMap::new, Collectors.toList()));
    }

    /**
     * 将集合按照两个规则分组收集为双层 Map<br>
     * <B>{@code Collection<E> -----> Map<T, Map<U, List<E>>>} </B>
     *
     * @param collection  需要分类的集合
     * @param classifier1 第一个分类的规则
     * @param classifier2 第二个分类的规则
     * @param <E>         集合元素类型
     * @param <K>         第一个Map key类型
     * @param <U>         第二个Map key类型
     * @return 分组后的 Map
     */
    public static <E, K, U> Map<K, Map<U, List<E>>> groupBy2Key(Collection<E> collection, Function<E, K> classifier1, Function<E, U> classifier2) {
        if (CollUtil.isEmpty(collection)) {
            return MapUtil.newHashMap();
        }
        return collection.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(classifier1, LinkedHashMap::new, Collectors.groupingBy(classifier2, LinkedHashMap::new, Collectors.toList())));
    }

    /**
     * 将集合按照两个规则分组收集双层 Map<br>
     * <B>{@code Collection<E> -----> Map<T, Map<U, E>>} </B>
     *
     * @param collection  需要转化的集合
     * @param classifier1 第一个分类的规则
     * @param classifier2 第二个分类的规则
     * @param <E>         集合元素类型
     * @param <T>         第一个Map key类型
     * @param <U>         第二个Map key类型
     * @return 分组后的 Map
     */
    public static <E, T, U> Map<T, Map<U, E>> group2Map(Collection<E> collection, Function<E, T> classifier1, Function<E, U> classifier2) {
        if (CollUtil.isEmpty(collection)) {
            return MapUtil.newHashMap();
        }
        return collection.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(classifier1, LinkedHashMap::new, Collectors.toMap(classifier2, Function.identity(), (v1, v2) -> v1)));
    }

    /**
     * 将集合转化为 List列表<br>
     * <B>{@code Collection<E> -----> List<T>} </B>
     *
     * @param collection 需要转化的集合
     * @param mapper     集合元素转列表元素函数
     * @param <E>        集合元素类型
     * @param <T>        列表元素类型
     * @return 转化后的列表
     */
    public static <E, T> List<T> toList(Collection<E> collection, Function<E, T> mapper) {
        if (CollUtil.isEmpty(collection)) {
            return CollUtil.newArrayList();
        }
        return collection.stream()
                .map(mapper)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 将集合转化为 Set集合<br>
     * <B>{@code Collection<E> -----> Set<T>} </B>
     *
     * @param collection 需要转化的集合
     * @param mapper     集合元素转Set元素函数
     * @param <E>        集合元素类型
     * @param <T>        Set元素类型
     * @return 转化后的Set
     */
    public static <E, T> Set<T> toSet(Collection<E> collection, Function<E, T> mapper) {
        if (CollUtil.isEmpty(collection)) {
            return CollUtil.newHashSet();
        }
        return collection.stream()
                .map(mapper)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * 合并两个相同 key 类型的Map
     *
     * @param map1  第一个需要合并的 Map
     * @param map2  第二个需要合并的 Map
     * @param merge 合并的lambda，将key value1 value2合并成最终的类型,注意value可能为空的情况
     * @param <K>   Map key类型
     * @param <X>   第一个Map value类型
     * @param <Y>   第二个Map value类型
     * @param <V>   合并后Map value类型
     * @return 合并后的 Map
     */
    public static <K, X, Y, V> Map<K, V> merge(Map<K, X> map1, Map<K, Y> map2, BiFunction<X, Y, V> merge) {
        if (CollUtil.isEmpty(map1) && CollUtil.isEmpty(map2)) {
            return MapUtil.newHashMap();
        } else if (CollUtil.isEmpty(map1)) {
            return toMap(map2.entrySet(), Map.Entry::getKey, entry -> merge.apply(null, entry.getValue()));
        } else if (CollUtil.isEmpty(map2)) {
            return toMap(map1.entrySet(), Map.Entry::getKey, entry -> merge.apply(entry.getValue(), null));
        }
        Set<K> keySet = new HashSet<>();
        keySet.addAll(map1.keySet());
        keySet.addAll(map2.keySet());
        return toMap(keySet, key -> key, key -> merge.apply(map1.get(key), map2.get(key)));
    }

}
