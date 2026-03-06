package com.example.consumer.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Data
@NoArgsConstructor
@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String severity;        // HIGH, MEDIUM, LOW

    @Column(nullable = false)
    private String affectedService;

    @Column(nullable = false, length = 1000)
    private String summary;

    @Column(length = 1000)
    private String suggestedAction;

    @Column(nullable = false)
    private Instant detectedAt;
}