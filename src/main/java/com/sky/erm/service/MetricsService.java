package com.sky.erm.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetricsService {

    private final MeterRegistry meterRegistry;
    private Counter userCreationCounter;
    private Counter userUpdateCounter;
    private Counter projectAdditionCounter;
    private Counter failedLoginCounter;

    public void init() {
        userCreationCounter = Counter.builder("app.user.creation")
                .description("Number of users created")
                .register(meterRegistry);

        userUpdateCounter = Counter.builder("app.user.update")
                .description("Number of user updates")
                .register(meterRegistry);

        projectAdditionCounter = Counter.builder("app.project.addition")
                .description("Number of projects added")
                .register(meterRegistry);

        failedLoginCounter = Counter.builder("app.login.failed")
                .description("Number of failed login attempts")
                .register(meterRegistry);
    }

    public void incrementUserCreation() {
        userCreationCounter.increment();
    }

    public void incrementUserUpdate() {
        userUpdateCounter.increment();
    }

    public void incrementProjectAddition() {
        projectAdditionCounter.increment();
    }

    public void incrementFailedLogin() {
        failedLoginCounter.increment();
    }
} 