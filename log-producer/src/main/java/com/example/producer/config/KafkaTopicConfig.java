package com.example.producer.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Slf4j
public class KafkaTopicConfig {

    @Value("${app.kafka.topic}")
    private String topic;

    @Bean
    public NewTopic logTopic() {
        log.debug("Building Kafka Topic");
        return TopicBuilder.name(topic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}