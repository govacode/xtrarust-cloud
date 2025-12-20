package com.xtrarust.cloud.rabbit.event;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class OrderCreateEvent {

    private Long orderId;

    private LocalDateTime createTime;
}
