package com.example.consumer.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class LogEvent {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp;

    private String level;
    private String service;
    private String message;
    private String traceId;
}