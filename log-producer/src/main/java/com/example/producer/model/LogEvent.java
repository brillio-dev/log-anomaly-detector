package com.example.producer.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class LogEvent {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp;

    private String level;     // INFO, WARN, ERROR
    private String service;
    private String message;
    private String traceId;
}