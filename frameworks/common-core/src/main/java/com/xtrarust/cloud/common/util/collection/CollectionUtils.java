package com.xtrarust.cloud.common.util.collection;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Collection 工具类
 */
public class CollectionUtils {

    public static <T> List<T> newArrayList(List<List<T>> list) {
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static boolean containsAny(Object source, Object... targets) {
        return Arrays.asList(targets).contains(source);
    }

    public static boolean isAnyEmpty(Collection<?>... collections) {
        return Arrays.stream(collections).anyMatch(CollectionUtil::isEmpty);
    }

    public static <T> boolean anyMatch(Collection<T> from, Predicate<T> predicate) {
        return CollectionUtil.isNotEmpty(from) && from.stream().anyMatch(predicate);
    }

    public static <T> List<T> filterList(Collection<T> from, Predicate<T> predicate) {
        if (CollectionUtil.isEmpty(from)) {
            return Collections.emptyList();
        }
        return from.stream().filter(predicate).collect(Collectors.toList());
    }

    public static <T, R> List<R> distinct(Collection<T> from, Function<T, R> func) {
        return distinct(from, t -> true, func);
    }

    public static <T, R> List<R> distinct(Collection<T> from, Predicate<T> filter, Function<T, R> func) {
        if (CollectionUtil.isEmpty(from)) {
            return Collections.emptyList();
        }
        return from.stream().filter(filter).map(func).distinct().collect(Collectors.toList());
    }

    public static <T, U> List<U> convertList(T[] from, Function<T, U> func) {
        if (ArrayUtil.isEmpty(from)) {
            return Collections.emptyList();
        }
        return convertList(Arrays.asList(from), func);
    }

    public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func) {
        if (CollectionUtil.isEmpty(from)) {
            return Collections.emptyList();
        }
        return from.stream().map(func).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static <T, U> List<U> convertList(Collection<T> from, Predicate<T> filter, Function<T, U> func) {
        if (CollectionUtil.isEmpty(from)) {
            return Collections.emptyList();
        }
        return from.stream().filter(filter).map(func).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static <T, U> List<U> convertListByFlatMap(Collection<T> from,
                                                      Function<T, ? extends Stream<? extends U>> func) {
        if (CollectionUtil.isEmpty(from)) {
            return Collections.emptyList();
        }
        return from.stream().flatMap(func).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static <T, U, R> List<R> convertListByFlatMap(Collection<T> from,
                                                         Function<? super T, ? extends U> mapper,
                                                         Function<U, ? extends Stream<? extends R>> func) {
        if (CollectionUtil.isEmpty(from)) {
            return Collections.emptyList();
        }
        return from.stream().map(mapper).flatMap(func).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static <K, V> List<V> mergeValuesFromMap(Map<K, List<V>> map) {
        if (MapUtil.isEmpty(map)) {
            return Collections.emptyList();
        }
        return map.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public static <T, U> Set<U> convertSet(Collection<T> from, Function<T, U> func) {
        if (CollectionUtil.isEmpty(from)) {
            return Collections.emptySet();
        }
        return from.stream().map(func).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public static <T, U> Set<U> convertSetByFlatMap(Collection<T> from,
                                                    Function<T, ? extends Stream<? extends U>> func) {
        if (CollectionUtil.isEmpty(from)) {
            return Collections.emptySet();
        }
        return from.stream().flatMap(func).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public static <T, U> Set<U> convertSet(Collection<T> from, Predicate<T> filter, Function<T, U> func) {
        if (CollectionUtil.isEmpty(from)) {
            return Collections.emptySet();
        }
        return from.stream().filter(filter).map(func).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public static <T, U, R> Set<R> convertSetByFlatMap(Collection<T> from,
                                                       Function<? super T, ? extends U> mapper,
                                                       Function<U, ? extends Stream<? extends R>> func) {
        if (CollectionUtil.isEmpty(from)) {
            return Collections.emptySet();
        }
        return from.stream().map(mapper).flatMap(func).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public static <T, K> Map<K, T> convertMap(Collection<T> from, Predicate<T> filter, Function<T, K> keyFunc) {
        if (CollectionUtil.isEmpty(from)) {
            return Collections.emptyMap();
        }
        return from.stream().filter(filter).collect(Collectors.toMap(keyFunc, Function.identity()));
    }

    public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc) {
        if (CollectionUtil.isEmpty(from)) {
            return Collections.emptyMap();
        }
        return convertMap(from, keyFunc, Function.identity());
    }

    public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc, Supplier<? extends Map<K, T>> supplier) {
        if (CollectionUtil.isEmpty(from)) {
            return supplier.get();
        }
        return convertMap(from, keyFunc, Function.identity(), supplier);
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        if (CollectionUtil.isEmpty(from)) {
            return Collections.emptyMap();
        }
        return convertMap(from, keyFunc, valueFunc, (k1, k2) -> k1);
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction) {
        if (CollectionUtil.isEmpty(from)) {
            return Collections.emptyMap();
        }
        return convertMap(from, keyFunc, valueFunc, mergeFunction, HashMap::new);
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, Supplier<? extends Map<K, V>> supplier) {
        if (CollectionUtil.isEmpty(from)) {
            return supplier.get();
        }
        return convertMap(from, keyFunc, valueFunc, (k1, k2) -> k1, supplier);
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction, Supplier<? extends Map<K, V>> supplier) {
        if (CollectionUtil.isEmpty(from)) {
            return Collections.emptyMap();
        }
        return from.stream().collect(Collectors.toMap(keyFunc, valueFunc, mergeFunction, supplier));
    }

    public static <T, K> Map<K, List<T>> convertMultiMap(Collection<T> from, Function<T, K> keyFunc) {
        if (CollectionUtil.isEmpty(from)) {
            return Collections.emptyMap();
        }
        return from.stream().collect(Collectors.groupingBy(keyFunc, Collectors.mapping(Function.identity(), Collectors.toList())));
    }

    public static <T, K, V> Map<K, List<V>> convertMultiMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        if (CollectionUtil.isEmpty(from)) {
            return Collections.emptyMap();
        }
        return from.stream().collect(Collectors.groupingBy(keyFunc, Collectors.mapping(valueFunc, Collectors.toList())));
    }

    public static <T, K, V> Map<K, Set<V>> convertMultiMap2(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        if (CollectionUtil.isEmpty(from)) {
            return Collections.emptyMap();
        }
        return from.stream().collect(Collectors.groupingBy(keyFunc, Collectors.mapping(valueFunc, Collectors.toSet())));
    }

    public static <T, K> Map<K, T> convertImmutableMap(Collection<T> from, Function<T, K> keyFunc) {
        if (CollectionUtil.isEmpty(from)) {
            return Collections.emptyMap();
        }
        ImmutableMap.Builder<K, T> builder = ImmutableMap.builder();
        from.forEach(item -> builder.put(keyFunc.apply(item), item));
        return builder.build();
    }

    /**
     * 对比老、新两个列表，找出新增、修改、删除的数据
     *
     * @param oldList  老列表
     * @param newList  新列表
     * @param sameFunc 对比函数，返回 true 表示相同，返回 false 表示不同
     *                 注意，same 是通过每个元素的“标识”，判断它们是不是同一个数据
     * @return [新增列表、修改列表、删除列表]
     */
    public static <T> List<List<T>> diffList(Collection<T> oldList, Collection<T> newList,
                                             BiFunction<T, T, Boolean> sameFunc) {
        List<T> createList = new LinkedList<>(newList); // 默认都认为是新增的，后续会进行移除
        List<T> updateList = new ArrayList<>();
        List<T> deleteList = new ArrayList<>();

        // 通过以 oldList 为主遍历，找出 updateList 和 deleteList
        for (T oldObj : oldList) {
            // 1. 寻找是否有匹配的
            T foundObj = null;
            for (Iterator<T> iterator = createList.iterator(); iterator.hasNext(); ) {
                T newObj = iterator.next();
                // 1.1 不匹配，则直接跳过
                if (!sameFunc.apply(oldObj, newObj)) {
                    continue;
                }
                // 1.2 匹配，则移除，并结束寻找
                iterator.remove();
                foundObj = newObj;
                break;
            }
            // 2. 匹配添加到 updateList；不匹配则添加到 deleteList 中
            if (foundObj != null) {
                updateList.add(foundObj);
            } else {
                deleteList.add(oldObj);
            }
        }
        return Arrays.asList(createList, updateList, deleteList);
    }

    public static boolean containsAny(Collection<?> source, Collection<?> candidates) {
        return org.springframework.util.CollectionUtils.containsAny(source, candidates);
    }

    // CollectionUtil#getFirst
    public static <T> T getFirst(List<T> from) {
        return CollectionUtil.isNotEmpty(from) ? from.get(0) : null;
    }

    public static <T> T findFirst(Collection<T> from, Predicate<T> predicate) {
        return findFirst(from, predicate, Function.identity());
    }

    public static <T, U> U findFirst(Collection<T> from, Predicate<T> predicate, Function<T, U> func) {
        if (CollectionUtil.isEmpty(from)) {
            return null;
        }
        return from.stream().filter(predicate).findFirst().map(func).orElse(null);
    }

    public static <T, V extends Comparable<? super V>> V getMaxValue(Collection<T> from, Function<T, V> valueFunc) {
        if (CollectionUtil.isEmpty(from)) {
            return null;
        }
        assert !from.isEmpty(); // 断言，避免告警
        T t = from.stream().max(Comparator.comparing(valueFunc)).get();
        return valueFunc.apply(t);
    }

    public static <T, V extends Comparable<? super V>> V getMinValue(List<T> from, Function<T, V> valueFunc) {
        if (CollectionUtil.isEmpty(from)) {
            return null;
        }
        assert !from.isEmpty(); // 断言，避免告警
        T t = from.stream().min(Comparator.comparing(valueFunc)).get();
        return valueFunc.apply(t);
    }

    public static <T, V extends Comparable<? super V>> V reduce(List<T> from, Function<T, V> valueFunc,
                                                                BinaryOperator<V> accumulator) {
        return reduce(from, valueFunc, accumulator, null);
    }

    public static <T, V extends Comparable<? super V>> V reduce(Collection<T> from, Function<T, V> valueFunc,
                                                                BinaryOperator<V> accumulator, V defaultValue) {
        if (CollectionUtil.isEmpty(from)) {
            return defaultValue;
        }
        assert !from.isEmpty(); // 断言，避免告警
        return from.stream().map(valueFunc).filter(Objects::nonNull).reduce(accumulator).orElse(defaultValue);
    }

    public static <T> void addIfNotNull(Collection<T> coll, T item) {
        if (item == null) {
            return;
        }
        coll.add(item);
    }

    public static <T> Collection<T> singleton(T obj) {
        return obj == null ? Collections.emptyList() : Collections.singleton(obj);
    }

    /**
     * 集合批处理
     *
     * @param collection 集合
     * @param batchSize  每批次处理多少个元素
     * @param consumer   消费者
     */
    public static <T> void batch(Collection<T> collection, int batchSize, Consumer<List<T>> consumer) {
        if (CollectionUtil.isEmpty(collection)) {
            return;
        }
        Assert.isTrue(batchSize > 0, "batchSize must be greater than 0");
        Assert.notNull(consumer, "consumer must not be null");
        int size = CollectionUtil.size(collection);
        int batch = (size + batchSize - 1) / batchSize;
        List<T> list = Lists.newArrayListWithCapacity(batch);
        int index = 0;
        for (T item : collection) {
            list.add(item);
            index++;
            if (index % batchSize == 0) {
                consumer.accept(list);
                list.clear();
            }
        }
        if (!list.isEmpty()) {
            consumer.accept(list);
        }
    }

}
