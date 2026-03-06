package com.example.consumer.service;

import com.example.consumer.client.AgentClient;
import com.example.consumer.model.Alert;
import com.example.consumer.model.LogEvent;
import com.example.consumer.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogConsumerService {

    private final AgentClient agentClient;
    private final AlertRepository alertRepository;

    private final List<LogEvent> buffer = Collections.synchronizedList(new ArrayList<>());
    private static final int BATCH_SIZE = 10;

    @KafkaListener(topics = "app-logs", groupId = "anomaly-detector-group")
    public void consume(LogEvent event) {
        log.debug("Received log event: {}", event);
        buffer.add(event);

        if (buffer.size() >= BATCH_SIZE) {
            List<LogEvent> batch;
            synchronized (buffer) {
                if (buffer.size() < BATCH_SIZE) return; // guard against race
                batch = new ArrayList<>(buffer);
                buffer.clear();
            }
            processBatch(batch);
        }
    }

    private void processBatch(List<LogEvent> batch) {
        log.info("Processing batch of {} log events", batch.size());
        try {
            AgentClient.AgentResponse response = agentClient.analyze(batch);
            if (response == null || response.anomalies() == null) {
                log.warn("Agent returned null or empty response");
                return;
            }
            response.anomalies().forEach(anomaly -> {
                Alert alert = new Alert();
                alert.setSeverity(anomaly.severity());
                alert.setAffectedService(anomaly.affectedService());
                alert.setSummary(anomaly.summary());
                alert.setSuggestedAction(anomaly.suggestedAction());
                alert.setDetectedAt(Instant.now());
                alertRepository.save(alert);
                log.info("Saved alert: severity={} service={}", anomaly.severity(), anomaly.affectedService());
            });
        } catch (Exception e) {
            log.error("Failed to process batch with agent service", e);
        }
    }
}