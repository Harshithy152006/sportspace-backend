package com.example.sportspace.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentMigrationRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PaymentMigrationRunner.class);
    private final JdbcTemplate jdbc;

    public PaymentMigrationRunner(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(String... args) {
        try {
            jdbc.update("UPDATE booking SET payment_method='CASH' WHERE payment_method IS NULL");
            jdbc.update("UPDATE booking SET payment_status='PENDING' WHERE payment_status IS NULL");
            log.info("Payment columns check and default migration completed successfully.");
        } catch (Exception e) {
            log.info("Payment migration skipped or not needed: {}", e.getMessage());
        }
    }
}
