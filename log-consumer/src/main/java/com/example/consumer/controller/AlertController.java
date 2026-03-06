package com.example.consumer.controller;

import com.example.consumer.model.Alert;
import com.example.consumer.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertRepository alertRepository;

    @GetMapping
    public List<Alert> getAll() {
        return alertRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alert> getById(@PathVariable Long id) {
        return alertRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/service/{service}")
    public List<Alert> getByService(@PathVariable String service) {
        return alertRepository.findByAffectedService(service);
    }

    @GetMapping("/severity/{severity}")
    public List<Alert> getBySeverity(@PathVariable String severity) {
        return alertRepository.findBySeverity(severity.toUpperCase());
    }

    // e.g. GET /alerts/filter?severity=HIGH&service=order-service
    @GetMapping("/filter")
    public List<Alert> filter(
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String service
    ) {
        if (severity != null && service != null) {
            return alertRepository.findBySeverityAndAffectedService(severity.toUpperCase(), service);
        } else if (severity != null) {
            return alertRepository.findBySeverity(severity.toUpperCase());
        } else if (service != null) {
            return alertRepository.findByAffectedService(service);
        }
        return alertRepository.findAll();
    }
}