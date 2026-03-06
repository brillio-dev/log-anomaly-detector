package com.example.consumer.client;

import com.example.consumer.model.LogEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AgentClient {

    private final RestClient restClient;

    public AgentResponse analyze(List<LogEvent> logs) {
        log.debug("Calling agent service with {} logs", logs.size());
        return restClient.post()
                .uri("/analyze")
                .body(Map.of("logs", logs))
                .retrieve()
                .body(AgentResponse.class);
    }

    public record AgentResponse(List<AnomalyDto> anomalies) {}

    public record AnomalyDto(
            String severity,
            String affectedService,
            String summary,
            String suggestedAction
    ) {}
}