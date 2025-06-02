package com.sky.erm.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

import java.time.OffsetDateTime;

@Entity
@Data
public class IdempotencyRecord {
    @Id
    private String key;

    @Column(nullable = false)
    private String path;

    @Lob
    @Column(nullable = false)
    private String responseBody;

    @Column(nullable = false)
    private Integer statusCode;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime expiresAt;
} 