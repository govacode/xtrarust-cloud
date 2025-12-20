package com.xtrarust.cloud.rabbit.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xtrarust.cloud.db.mybatis.core.entity.BaseDO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("rabbit_message")
public class RabbitMessageDO extends BaseDO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 交换机
     */
    private String exchange;

    /**
     * 路由键
     */
    private String routingKey;

    /**
     * 消息体JSON
     */
    private String messageBody;

    /**
     * 状态：0-待发送，1-已发送，2-未正确路由，3-发送失败
     */
    private Integer status;

    /**
     * 重试次数
     */
    private Integer retryNum;

    /**
     * 下次驱动开始时间
     */
    private LocalDateTime nextTime;
}

