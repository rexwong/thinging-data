package com.rexwong.thinkingdata.app.config;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.Properties;

/**
 * @author rexwong
 */
@Configuration
public class KafkaProducerConfig {

    private org.apache.kafka.clients.producer.KafkaProducer<String, String> producer;

    public void setKafkaProducerConfig() {
        Properties props = new Properties();
        props.put("bootstrap.servers", KafkaProperties.KAFKAF_BROKER_LIST);
        props.put("client.id", "producerDemo");
        Serializer keySerializer = new StringSerializer();
        Serializer valueSerializer = new StringSerializer();
        this.producer = new org.apache.kafka.clients.producer.KafkaProducer<>(props, keySerializer, valueSerializer);
    }

    @Bean
    @DependsOn
    org.apache.kafka.clients.producer.KafkaProducer getKafkaProducer() {
        setKafkaProducerConfig();
        return producer;
    }

    public static void main(String[] args) {
        KafkaProducerConfig config = new KafkaProducerConfig();
        KafkaProducer producer = config.getKafkaProducer();
        producer.send(new ProducerRecord<>(KafkaProperties.TOPIC, "1", "message"), (recordMetadata, e) -> {
            System.out.println("message send to:[" + recordMetadata.partition() + "],offset:[" + recordMetadata.offset() + "]");
        });
    }
}
