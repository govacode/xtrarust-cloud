package com.xtrarust.cloud.rabbit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xtrarust.cloud.rabbit.entity.RabbitMessageDO;

import java.util.List;

public interface RabbitMessageService extends IService<RabbitMessageDO> {

    void send(String exchange, String routingKey, Object payload);

    void sendSuccess(Long messageId);

    void sendReturned(Long messageId);

    List<RabbitMessageDO> listNeedResend();

    void sendFailed(Long messageId);

    void resend(Long messageId);
}

