package com.example.producer.service;

import com.example.producer.model.LogEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogProducerService {

    private final KafkaTemplate<String, LogEvent> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String topic;

    public void send(LogEvent event) {
        CompletableFuture<SendResult<String, LogEvent>> future =
                kafkaTemplate.send(topic, event.getTraceId(), event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send log event: {}", event, ex);
            } else {
                log.debug("Sent log event to partition={} offset={}: {}",
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        event);
            }
        });
    }
}