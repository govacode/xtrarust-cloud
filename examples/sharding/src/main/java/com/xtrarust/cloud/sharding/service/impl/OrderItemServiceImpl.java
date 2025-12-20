package com.xtrarust.cloud.sharding.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xtrarust.cloud.sharding.entity.OrderItem;
import com.xtrarust.cloud.sharding.mapper.OrderItemMapper;
import com.xtrarust.cloud.sharding.service.IOrderItemService;
import org.springframework.stereotype.Service;

@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements IOrderItemService {

}
