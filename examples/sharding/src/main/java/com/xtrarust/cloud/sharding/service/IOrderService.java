package com.xtrarust.cloud.sharding.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xtrarust.cloud.common.util.LoadRunnerUtils;
import com.xtrarust.cloud.sharding.entity.Order;

import java.util.concurrent.TimeUnit;

public interface IOrderService extends IService<Order> {

    public static void main(String[] args) throws InterruptedException {
        LoadRunnerUtils.LoadRunnerResult result = LoadRunnerUtils.run(1000, 100, () -> {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println(result);
    }
}
