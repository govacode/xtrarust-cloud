package com.xtrarust.cloud.kafka.listener;

import cn.hutool.core.thread.ThreadUtil;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleConsumer {

    private static volatile boolean isBusy = false;

    public static void setBusy() {
        isBusy = true;
    }

    public static void setNotBusy() {
        isBusy = false;
    }


    public static void main(String[] args) {
        // Kafka消费者配置
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092,127.0.0.1:9192,127.0.0.1:9292"); // Kafka服务器地址
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test_group"); // 消费者组ID
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()); // Key反序列化器
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()); // Value反序列化器
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // 从最早的偏移量开始消费
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100); // 从最早的偏移量开始消费

        // 创建Kafka消费者
        AtomicInteger count = new AtomicInteger(0);
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            // 订阅主题
            consumer.subscribe(Collections.singletonList("test_topic")); // 替换为你的主题名

            // 持续消费消息
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1_000)); // 拉取消息
                if (!records.isEmpty()) {
                    int c = count.incrementAndGet();
                    System.out.println("第" + c + "次拉取到消息 共" + records.count() + "条");

                    if (c == 2) {
                        setBusy();
                    }
                    if (c == 4) {
                        setNotBusy();
                    }

                    for (ConsumerRecord<String, String> record : records) {
                        if (isBusy) {
                            System.out.println("busy, start to seek...");
                            consumer.seek(new TopicPartition("test_topic", record.partition()), record.offset());
                            break;
                        }

                        // 处理消息
                        System.out.printf("partition = %d, offset = %d %n", record.partition(), record.offset());

                        // 手动提交位移
                        consumer.commitSync(
                                Collections.singletonMap(
                                        new TopicPartition(record.topic(), record.partition()),
                                        new OffsetAndMetadata(record.offset() + 1))); // 提交当前消息的下一个偏移量
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
