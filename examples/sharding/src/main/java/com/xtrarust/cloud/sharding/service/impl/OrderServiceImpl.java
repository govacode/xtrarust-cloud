package com.xtrarust.cloud.sharding.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xtrarust.cloud.sharding.entity.Order;
import com.xtrarust.cloud.sharding.mapper.OrderMapper;
import com.xtrarust.cloud.sharding.service.IOrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

}
