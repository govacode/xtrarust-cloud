package com.xtrarust.cloud.rabbit.job;

import cn.hutool.core.collection.CollectionUtil;
import com.xtrarust.cloud.rabbit.config.RabbitConstants;
import com.xtrarust.cloud.rabbit.entity.RabbitMessageDO;
import com.xtrarust.cloud.rabbit.service.RabbitMessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class RabbitMessageResendJob {

    @Resource
    private RabbitMessageService rabbitMessageService;

//    @XxlJob("rabbitResendJob")
//    @Scheduled(cron = "*/5 * * * * ?")
    public void resendMessage() {
        List<RabbitMessageDO> messages = rabbitMessageService.listNeedResend();
        if (CollectionUtil.isEmpty(messages)) {
            return;
        }
        for (RabbitMessageDO message : messages) {
            if (Objects.nonNull(message.getRetryNum()) && message.getRetryNum() >= RabbitConstants.MAX_RETRY_COUNT) {
                rabbitMessageService.sendFailed(message.getId());
                continue;
            }
            rabbitMessageService.resend(message.getId());
        }
    }
}
