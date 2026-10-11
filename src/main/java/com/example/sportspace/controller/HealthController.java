package com.example.sportspace.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping({"/", "/api/health"})
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "UP");
        response.put("service", "SportSpace Backend API");
        response.put("timestamp", Instant.now().toString());

        boolean dbConnected = false;
        String dbProduct = "UNKNOWN";
        try (Connection conn = dataSource.getConnection()) {
            dbConnected = !conn.isClosed();
            dbProduct = conn.getMetaData().getDatabaseProductName();
        } catch (Exception ignored) {
        }

        response.put("databaseConnected", dbConnected);
        response.put("databaseType", dbProduct);
        return ResponseEntity.ok(response);
    }
}
