package com.xtrarust.cloud.sharding.mapper;

import com.xtrarust.cloud.db.mybatis.core.mapper.BaseMapperX;
import com.xtrarust.cloud.sharding.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapperX<Order> {

}
