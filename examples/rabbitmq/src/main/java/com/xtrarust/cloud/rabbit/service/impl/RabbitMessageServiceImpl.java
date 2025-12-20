package com.xtrarust.cloud.rabbit.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xtrarust.cloud.rabbit.config.RabbitConstants;
import com.xtrarust.cloud.rabbit.mapper.RabbitMessageMapper;
import com.xtrarust.cloud.rabbit.entity.RabbitMessageDO;
import com.xtrarust.cloud.rabbit.producer.RabbitProducer;
import com.xtrarust.cloud.rabbit.service.RabbitMessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.xtrarust.cloud.rabbit.enums.RabbitMessageSendStatus.*;

@Slf4j
@Service
public class RabbitMessageServiceImpl extends ServiceImpl<RabbitMessageMapper, RabbitMessageDO> implements RabbitMessageService {

    @Resource
    private RabbitProducer rabbitProducer;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void send(String exchange, String routingKey, Object payload) {
        // 计算时间，防止定时任务扫描将还在正常流程中的消息进行重试
        LocalDateTime nextTime = LocalDateTime.now().plusSeconds(10);
        String messageBody = JSON.toJSONString(payload);
        long id = IdUtil.getSnowflakeNextId();
        RabbitMessageDO messageDO = new RabbitMessageDO()
                .setId(id)
                .setExchange(exchange)
                .setRoutingKey(routingKey)
                .setMessageBody(messageBody)
                .setStatus(WAITING_SEND.getStatus())
                .setRetryNum(0)
                .setNextTime(nextTime);
        save(messageDO);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

            @Override
            public void afterCommit() {
                rabbitProducer.send(exchange, routingKey, payload, String.valueOf(id));
            }
        });
    }

    @Override
    public void sendSuccess(Long messageId) {
        if (Objects.isNull(messageId) || Objects.isNull(getById(messageId))) {
            return;
        }
        log.info("message send success, id: {}", messageId);
        updateById(new RabbitMessageDO().setId(messageId).setStatus(SEND_SUCCESS.getStatus()));
    }

    @Override
    public void sendReturned(Long messageId) {
        if (Objects.isNull(messageId) || Objects.isNull(getById(messageId))) {
            return;
        }
        log.warn("message returned, id: {}", messageId);
        updateById(new RabbitMessageDO().setId(messageId).setStatus(RETURNED.getStatus()));
    }

    @Override
    public List<RabbitMessageDO> listNeedResend() {
        LambdaQueryWrapper<RabbitMessageDO> queryWrapper = new LambdaQueryWrapper<RabbitMessageDO>()
                .eq(RabbitMessageDO::getStatus, WAITING_SEND.getStatus())
                .lt(RabbitMessageDO::getNextTime, LocalDateTime.now())
                .le(RabbitMessageDO::getRetryNum, RabbitConstants.MAX_RETRY_COUNT);
        List<RabbitMessageDO> messages = baseMapper.selectList(queryWrapper);
        return CollectionUtil.isEmpty(messages) ? new ArrayList<>() : messages;
    }

    @Override
    public void sendFailed(Long messageId) {
        if (Objects.isNull(messageId) || Objects.isNull(getById(messageId))) {
            return;
        }
        log.info("message send failed, id: {}", messageId);
        LambdaQueryWrapper<RabbitMessageDO> queryWrapper = new LambdaQueryWrapper<RabbitMessageDO>()
                .eq(RabbitMessageDO::getId, messageId)
                .ge(RabbitMessageDO::getRetryNum, RabbitConstants.MAX_RETRY_COUNT);
        update(new RabbitMessageDO().setStatus(SEND_FAILED.getStatus()), queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resend(Long messageId) {
        RabbitMessageDO message;
        if (Objects.isNull(messageId) || Objects.isNull(message = getById(messageId))) {
            return;
        }
        log.info("resend message, id: {}, retry num: {}", messageId, message.getRetryNum() + 1);
        RabbitMessageDO messageDO = new RabbitMessageDO()
                .setId(messageId)
                .setRetryNum(message.getRetryNum() + 1)
                .setNextTime(message.getNextTime().plusSeconds(5));
        updateById(messageDO);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

            @Override
            public void afterCommit() {
                log.info("rabbit retry send message, id: {}", messageId);
                rabbitProducer.send(message.getExchange(), message.getRoutingKey(), message.getMessageBody(), String.valueOf(messageId));
            }
        });
    }

}

