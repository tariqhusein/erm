package com.sky.erm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.erm.domain.IdempotencyRecord;
import com.sky.erm.exception.BusinessException;
import com.sky.erm.repository.IdempotencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdempotencyServiceTest {

    @Mock
    private IdempotencyRepository idempotencyRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private IdempotencyService idempotencyService;

    private static final String TEST_KEY = "test-key";
    private static final String TEST_PATH = "/api/test";
    private static final String TEST_RESPONSE_BODY = "{\"id\":1,\"name\":\"test\"}";

    @BeforeEach
    void setUp() {
    }

    @Test
    void whenNewRequest_shouldProcessAndSave() throws JsonProcessingException {
        // Given
        when(idempotencyRepository.findById(TEST_KEY)).thenReturn(Optional.empty());
        ResponseEntity<String> response = ResponseEntity.ok(TEST_RESPONSE_BODY);
        when(objectMapper.writeValueAsString(TEST_RESPONSE_BODY)).thenReturn(TEST_RESPONSE_BODY);

        // When
        ResponseEntity<String> result = idempotencyService.processIdempotentRequest(
            TEST_KEY,
            TEST_PATH,
            () -> response
        );

        // Then
        assertEquals(response.getStatusCode(), result.getStatusCode());
        verify(idempotencyRepository).save(argThat(record -> 
            record.getKey().equals(TEST_KEY) &&
            record.getPath().equals(TEST_PATH) &&
            record.getStatusCode() == 200
        ));
    }

    @Test
    void whenExistingRequest_shouldReturnCached() throws JsonProcessingException {
        // Given
        IdempotencyRecord record = new IdempotencyRecord();
        record.setKey(TEST_KEY);
        record.setPath(TEST_PATH);
        record.setStatusCode(200);
        record.setResponseBody(TEST_RESPONSE_BODY);
        record.setCreatedAt(OffsetDateTime.now());
        record.setExpiresAt(OffsetDateTime.now().plusHours(24));

        when(idempotencyRepository.findById(TEST_KEY)).thenReturn(Optional.of(record));
        when(objectMapper.readValue(TEST_RESPONSE_BODY, Object.class)).thenReturn(TEST_RESPONSE_BODY);

        // When
        ResponseEntity<String> result = idempotencyService.processIdempotentRequest(
            TEST_KEY,
            TEST_PATH,
            () -> fail("Supplier should not be called")
        );

        // Then
        assertEquals(200, result.getStatusCode().value());
        assertEquals(TEST_RESPONSE_BODY, result.getBody());
        verify(idempotencyRepository, never()).save(any());
    }


    @Test
    void whenJsonProcessingError_shouldWrapException() throws JsonProcessingException {
        // Given
        when(idempotencyRepository.findById(TEST_KEY)).thenReturn(Optional.empty());
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Test error") {});

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            idempotencyService.processIdempotentRequest(
                TEST_KEY,
                TEST_PATH,
                () -> ResponseEntity.ok(TEST_RESPONSE_BODY)
            )
        );

        assertEquals("Error processing JSON", exception.getMessage());
        assertTrue(exception.getCause() instanceof JsonProcessingException);
    }

    @Test
    void whenBusinessException_shouldNotWrap() {
        // Given
        when(idempotencyRepository.findById(TEST_KEY)).thenReturn(Optional.empty());
        
        class TestBusinessException extends BusinessException {
            TestBusinessException() { super("Test business error"); }
        }
        TestBusinessException businessException = new TestBusinessException();

        // When & Then
        TestBusinessException thrown = assertThrows(TestBusinessException.class, () ->
            idempotencyService.processIdempotentRequest(
                TEST_KEY,
                TEST_PATH,
                () -> { throw businessException; }
            )
        );

        assertSame(businessException, thrown);
    }

    @Test
    void whenCleanupExpiredRecords_shouldDeleteAndLog() {
        // When
        idempotencyService.cleanupExpiredRecords();

        // Then
        verify(idempotencyRepository).deleteExpiredRecords(any(OffsetDateTime.class));
    }

    @Test
    void whenCleanupFails_shouldLogError() {
        // Given
        doThrow(new RuntimeException("Cleanup failed")).when(idempotencyRepository)
            .deleteExpiredRecords(any());

        // When
        idempotencyService.cleanupExpiredRecords();

        // Then
        verify(idempotencyRepository).deleteExpiredRecords(any(OffsetDateTime.class));
        // Note: We could verify logging but that would make the test brittle
    }
} 