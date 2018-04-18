package com.rexwong.thinkingdata.app.dao;

import com.rexwong.thinkingdata.app.config.KafkaProperties;
import kafka.utils.ShutdownableThread;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 */
public class Consumer2 extends ShutdownableThread {
    // High level consumer
    // Low level consumer
    private final KafkaConsumer<Integer, String> consumer;

    public Consumer2() {
        super("KafkaConsumerTest", false);
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaProperties.KAFKAF_BROKER_LIST);
        //GroupId 消息所属的分组
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "producerDemo");
        //是否自动提交消息:offset
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        //自动提交的间隔时间
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        //设置使用最开始的offset偏移量为当前group.id的最早消息
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        //设置心跳时间
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS, "30000");
        properties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY, "");
        //对key和value设置反序列化对象
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        this.consumer = new KafkaConsumer<>(properties);
    }

    public void doWork() {
        consumer.subscribe(KafkaProperties.TOPIC);
        Map<String,ConsumerRecords<Integer, String>> records = consumer.poll(100);
        records.forEach((k,v)->{
            List<ConsumerRecord<Integer, String>> recordList =  v.records(0);
            recordList.forEach(e->{
                try {
                    System.out.println("["+e.partition()+"]receiver message:" +
                            "["+e.value()+"->"+e.value()+"],offset:"+e.offset()+"");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });

        });

    }

    public static void main(String[] args) {
        Consumer2 consumer = new Consumer2();
        consumer.start();


    }
}
