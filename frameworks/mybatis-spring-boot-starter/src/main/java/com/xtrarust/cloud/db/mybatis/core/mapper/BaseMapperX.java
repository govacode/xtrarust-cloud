package com.xtrarust.cloud.db.mybatis.core.mapper;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.interfaces.MPJBaseJoin;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.xtrarust.cloud.common.domain.PageParam;
import com.xtrarust.cloud.common.domain.PageResult;
import com.xtrarust.cloud.common.domain.SortablePageParam;
import com.xtrarust.cloud.common.domain.SortingField;
import com.xtrarust.cloud.db.mybatis.core.util.MyBatisUtils;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 在 MyBatis Plus 的 BaseMapper 的基础上拓展，提供更多的能力
 * <p>
 * 1. {@link BaseMapper} 为 MyBatis Plus 的基础接口，提供基础的 CRUD 能力
 * 2. {@link MPJBaseMapper} 为 MyBatis Plus Join 的基础接口，提供连表 Join 能力
 */
public interface BaseMapperX<T> extends MPJBaseMapper<T> {

    /**
     * 批量插入 仅适用于mysql
     *
     * @param entityList 实体列表
     * @return 影响行数
     */
    Integer insertBatchSomeColumn(List<T> entityList);

    default PageResult<T> selectPage(SortablePageParam pageParam, @Param("ew") Wrapper<T> queryWrapper) {
        return selectPage(pageParam, pageParam.getSortingFields(), queryWrapper);
    }

    default PageResult<T> selectPage(PageParam pageParam, @Param("ew") Wrapper<T> queryWrapper) {
        return selectPage(pageParam, null, queryWrapper);
    }

    default PageResult<T> selectPage(PageParam pageParam, Collection<SortingField> sortingFields, @Param("ew") Wrapper<T> queryWrapper) {
        // 特殊：不分页，直接查询全部
        if (PageParam.PAGE_SIZE_NONE.equals(pageParam.getPageSize())) {
            List<T> list = selectList(queryWrapper);
            return new PageResult<>((long) list.size(), list);
        }

        // MyBatis Plus 分页查询
        IPage<T> mpPage = MyBatisUtils.buildPage(pageParam, sortingFields);
        selectPage(mpPage, queryWrapper);
        return new PageResult<>(mpPage.getTotal(), mpPage.getRecords());
    }

    default <D> PageResult<D> selectJoinPage(PageParam pageParam, Class<D> clazz, MPJLambdaWrapper<T> lambdaWrapper) {
        // 特殊：不分页，直接查询全部
        if (PageParam.PAGE_SIZE_NONE.equals(pageParam.getPageNo())) {
            List<D> list = selectJoinList(clazz, lambdaWrapper);
            return new PageResult<>((long) list.size(), list);
        }

        // MyBatis Plus Join 分页查询
        IPage<D> mpPage = MyBatisUtils.buildPage(pageParam);
        mpPage = selectJoinPage(mpPage, clazz, lambdaWrapper);
        return new PageResult<>(mpPage.getTotal(), mpPage.getRecords());
    }

    default <DTO> PageResult<DTO> selectJoinPage(PageParam pageParam, Class<DTO> resultTypeClass, MPJBaseJoin<T> joinQueryWrapper) {
        IPage<DTO> mpPage = MyBatisUtils.buildPage(pageParam);
        selectJoinPage(mpPage, resultTypeClass, joinQueryWrapper);
        return new PageResult<>(mpPage.getTotal(), mpPage.getRecords());
    }

    default T selectOne(String column, Object val) {
        return selectOne(new QueryWrapper<T>().eq(column, val));
    }

    default T selectOne(SFunction<T, ?> column, Object val) {
        return selectOne(new LambdaQueryWrapper<T>().eq(column, val));
    }

    default T selectOne(String column1, Object val1, String column2, Object val2) {
        return selectOne(new QueryWrapper<T>().eq(column1, val1).eq(column2, val2));
    }

    default T selectOne(SFunction<T, ?> column1, Object val1,
                        SFunction<T, ?> column2, Object val2) {
        return selectOne(new LambdaQueryWrapper<T>().eq(column1, val1).eq(column2, val2));
    }

    default T selectOne(SFunction<T, ?> column1, Object val1,
                        SFunction<T, ?> column2, Object val2,
                        SFunction<T, ?> column3, Object val3) {
        return selectOne(new LambdaQueryWrapper<T>().eq(column1, val1).eq(column2, val2).eq(column3, val3));
    }

    default Long selectCount() {
        return selectCount(new QueryWrapper<>());
    }

    default Long selectCount(String column, Object val) {
        return selectCount(new QueryWrapper<T>().eq(column, val));
    }

    default Long selectCount(SFunction<T, ?> column, Object val) {
        return selectCount(new LambdaQueryWrapper<T>().eq(column, val));
    }

    default List<T> selectList() {
        return selectList(new QueryWrapper<>());
    }

    default List<T> selectList(String column, Object val) {
        return selectList(new QueryWrapper<T>().eq(column, val));
    }

    default List<T> selectList(SFunction<T, ?> column, Object val) {
        return selectList(new LambdaQueryWrapper<T>().eq(column, val));
    }

    default List<T> selectList(String column, Collection<?> coll) {
        if (CollUtil.isEmpty(coll)) {
            return CollUtil.newArrayList();
        }
        return selectList(new QueryWrapper<T>().in(column, coll));
    }

    default List<T> selectList(SFunction<T, ?> column, Collection<?> coll) {
        if (CollUtil.isEmpty(coll)) {
            return CollUtil.newArrayList();
        }
        return selectList(new LambdaQueryWrapper<T>().in(column, coll));
    }

    @Deprecated
    default List<T> selectList(SFunction<T, ?> leColumn, SFunction<T, ?> geColumn, Object val) {
        return selectList(new LambdaQueryWrapper<T>().le(leColumn, val).ge(geColumn, val));
    }

    default List<T> selectList(SFunction<T, ?> column1, Object val1,
                               SFunction<T, ?> column2, Object val2) {
        return selectList(new LambdaQueryWrapper<T>().eq(column1, val1).eq(column2, val2));
    }

    /**
     * 批量插入，适合大量数据插入
     *
     * @param entities 实体集合
     */
    default Boolean insertBatch(Collection<T> entities) {
        return Db.saveBatch(entities);
    }

    /**
     * 批量插入，适合大量数据插入
     *
     * @param entities 实体们
     * @param size     插入数量 Db.saveBatch 默认为 1000
     */
    default Boolean insertBatch(Collection<T> entities, int size) {
        return Db.saveBatch(entities, size);
    }

    default Boolean updateBatchById(Collection<T> entityList) {
        return Db.updateBatchById(entityList);
    }

    default Boolean updateBatchById(Collection<T> entityList, int size) {
        return Db.updateBatchById(entityList, size);
    }

    default boolean insertOrUpdate(T entity) {
        return Db.saveOrUpdate(entity);
    }

    default Boolean insertOrUpdateBatch(Collection<T> entityList) {
        return Db.saveOrUpdateBatch(entityList);
    }

    default int delete(String column, String val) {
        return delete(new QueryWrapper<T>().eq(column, val));
    }

    default int delete(SFunction<T, ?> column, Object val) {
        return delete(new LambdaQueryWrapper<T>().eq(column, val));
    }

}
