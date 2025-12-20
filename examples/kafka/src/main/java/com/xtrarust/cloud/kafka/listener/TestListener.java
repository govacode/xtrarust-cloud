package com.xtrarust.cloud.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
public class TestListener {

//    @KafkaListener(topics = "test_topic", groupId = "test_group")
    public void consume(String message, Acknowledgment acknowledgment) {
//        log.info("received message size: {}", messages.size());
//        messages.forEach(msg -> log.info("---> received: {}", msg));
        log.info("Consumed message: {}", message);
        acknowledgment.acknowledge();
    }
}
