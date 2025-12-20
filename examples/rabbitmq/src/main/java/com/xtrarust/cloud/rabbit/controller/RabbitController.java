package com.xtrarust.cloud.rabbit.controller;

import com.xtrarust.cloud.common.pojo.R;
import com.xtrarust.cloud.rabbit.event.OrderCreateEvent;
import com.xtrarust.cloud.rabbit.service.RabbitMessageService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RabbitController {

    @Resource
    private RabbitMessageService rabbitMessageService;

    @GetMapping("/send")
    public R<String> send(@RequestParam String exchange,
                          @RequestParam String routingKey) {
        rabbitMessageService.send(exchange, routingKey, new OrderCreateEvent().setOrderId(123456789L));
        return R.ok("send success");
    }
}
