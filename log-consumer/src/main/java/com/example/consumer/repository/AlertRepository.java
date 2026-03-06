package com.example.consumer.repository;

import com.example.consumer.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByAffectedService(String affectedService);
    List<Alert> findBySeverity(String severity);
    List<Alert> findBySeverityAndAffectedService(String severity, String affectedService);
}