package com.xtrarust.cloud.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.RoundRobinAssignor;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Slf4j
public class SimpleProducer {

    public static void main(String[] args) {
        // try with resource自动关闭生产者
        try (Producer<String, String> producer = initProducer()) {
            // 同步发送
            syncSend(producer);
            // 异步发送
            //asyncSend(producer);
        } catch (Exception e) {
            log.error("exception", e);
        }
    }

    private static Producer<String, String> initProducer() {
        Properties properties = new Properties();
        // 配置 bootstrap.servers
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092,127.0.0.1:9192,127.0.0.1:9292");
        // key 和 value 序列化器
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // 从3.0.0开始 The producer has stronger delivery guarantees by default: idempotence is enabled and acks is set to all instead of 1
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        // batch.size 代表一个ProducerBatch中最多可以存放的数据大小 默认16k
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 32768);
        // linger.ms 收集完一个ProducerBatch的最长等待时间 默认0
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        // 配置 client.id 方便追踪程序的执行进度
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "SimpleProducer");
        properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, RoundRobinPartitioner.class.getName());

        // 初始化 KafkaProducer 对象
        return new KafkaProducer<>(properties);
    }

    private static void syncSend(Producer<String, String> producer) throws ExecutionException, InterruptedException {
        for (int i = 0; i < 1000; i++) {
            ProducerRecord<String, String> record = new ProducerRecord<>("test_topic", "hello world");
            producer.send(record).get();
        }
    }

    private static void asyncSend(Producer<String, String> producer) {
        ProducerRecord<String, String> record = new ProducerRecord<>("test_topic", "hello world");
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Send failed", exception);
            }
            if (metadata != null) {
                log.info("Send success: {}", metadata);
            }
        });
    }
}
