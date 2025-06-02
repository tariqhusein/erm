package com.sky.erm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.erm.domain.IdempotencyRecord;
import com.sky.erm.repository.IdempotencyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyRepository idempotencyRepository;
    private final ObjectMapper objectMapper;
    private static final long EXPIRATION_HOURS = 24;

    @Transactional
    public <T> ResponseEntity<T> processIdempotentRequest(String idempotencyKey, String path, Supplier<ResponseEntity<T>> operation) {
        try {
            Optional<IdempotencyRecord> existingRecord = idempotencyRepository.findById(idempotencyKey);
            
            if (existingRecord.isPresent()) {
                IdempotencyRecord record = existingRecord.get();
                if (!record.getPath().equals(path)) {
                    throw new IllegalStateException("Idempotency key already used with different path");
                }
                
                @SuppressWarnings("unchecked")
                T responseBody = (T) objectMapper.readValue(record.getResponseBody(), Object.class);
                return ResponseEntity.status(record.getStatusCode()).body(responseBody);
            }

            ResponseEntity<T> response = operation.get();
            
            IdempotencyRecord record = new IdempotencyRecord();
            record.setKey(idempotencyKey);
            record.setPath(path);
            record.setResponseBody(objectMapper.writeValueAsString(response.getBody()));
            record.setStatusCode(response.getStatusCode().value());
            record.setCreatedAt(OffsetDateTime.now());
            record.setExpiresAt(OffsetDateTime.now().plus(EXPIRATION_HOURS, ChronoUnit.HOURS));
            
            idempotencyRepository.save(record);
            
            return response;
        } catch (Exception e) {
            log.error("Error processing idempotent request", e);
            throw new RuntimeException("Error processing idempotent request", e);
        }
    }

    @Scheduled(cron = "0 0 * * * *") // Run every hour
    @Transactional
    public void cleanupExpiredRecords() {
        try {
            idempotencyRepository.deleteExpiredRecords(OffsetDateTime.now());
            log.info("Cleaned up expired idempotency records");
        } catch (Exception e) {
            log.error("Error cleaning up expired idempotency records", e);
        }
    }
} 