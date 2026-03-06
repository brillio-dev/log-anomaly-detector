package com.example.producer.service;

import com.example.producer.model.LogEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogSimulatorScheduler {

    private final LogProducerService producerService;
    private final Random random = new Random();

    private static final List<LogEvent> TEMPLATES = List.of(
            LogEvent.builder().level("INFO").service("order-service")
                    .message("Order placed successfully").build(),
            LogEvent.builder().level("INFO").service("payment-service")
                    .message("Payment processed for order-789").build(),
            LogEvent.builder().level("WARN").service("order-service")
                    .message("Retry attempt 2/3 for DB write").build(),
            LogEvent.builder().level("ERROR").service("order-service")
                    .message("DB connection timeout after 30s").build(),
            LogEvent.builder().level("ERROR").service("payment-service")
                    .message("NullPointerException in PaymentHandler").build(),
            LogEvent.builder().level("ERROR").service("auth-service")
                    .message("JWT validation failed: token expired").build(),
            LogEvent.builder().level("WARN").service("inventory-service")
                    .message("Stock level critically low for SKU-4821").build(),
            LogEvent.builder().level("INFO").service("auth-service")
                    .message("User login successful").build()
    );

    @Scheduled(fixedDelay = 15000)
    public void emitLog() {
        LogEvent template = TEMPLATES.get(random.nextInt(TEMPLATES.size()));
        LogEvent event = LogEvent.builder()
                .timestamp(Instant.now())
                .level(template.getLevel())
                .service(template.getService())
                .message(template.getMessage())
                .traceId(UUID.randomUUID().toString())
                .build();
        producerService.send(event);
        log.debug("Sent Log event: {}", event);
    }
}