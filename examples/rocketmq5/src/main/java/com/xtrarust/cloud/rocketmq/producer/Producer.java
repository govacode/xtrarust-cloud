package com.xtrarust.cloud.rocketmq.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;

@Slf4j
public class Producer {

    public static final String PRODUCER_GROUP = "PG_1";
    public static final String TOPIC = "test";
    public static final String TAG = "TagA";

    public static void main(String[] args) throws MQClientException, InterruptedException {

        DefaultMQProducer producer = new DefaultMQProducer(PRODUCER_GROUP);

        producer.setNamesrvAddr("127.0.0.1:9876");
        producer.setVipChannelEnabled(false);
        producer.setSendMsgTimeout(60_000);

        producer.start();
        try {
            Message msg = new Message(TOPIC, TAG, "OrderID188", "Hello world".getBytes(StandardCharsets.UTF_8));
            SendResult sendResult = producer.send(msg);
            log.info("{}", sendResult);
        } catch (Exception e) {
            log.error("Failed to send message", e);
        } finally {
            producer.shutdown();
        }
    }
}
