package com.sky.erm.api;

import com.sky.erm.model.CreateProjectRequestDto;
import com.sky.erm.model.CreateUserRequestDto;
import com.sky.erm.model.ProjectResponseDto;
import com.sky.erm.model.UserResponseDto;
import com.sky.erm.model.UpdateUserRequestDto;
import com.sky.erm.service.IdempotencyService;
import com.sky.erm.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UserService userService;
    private final IdempotencyService idempotencyService;
    private final HttpServletRequest request;

    @Override
    public ResponseEntity<ProjectResponseDto> addProjectToUser(Long userId, String requestId, CreateProjectRequestDto createProjectRequestDto) {
        return idempotencyService.processIdempotentRequest(
                requestId,
                request.getRequestURI(),
                () -> new ResponseEntity<>(userService.addProjectToUser(userId, createProjectRequestDto), CREATED)
        );
    }

    @Override
    public ResponseEntity<UserResponseDto> createUser(String requestId, CreateUserRequestDto createUserRequestDto) {
        return idempotencyService.processIdempotentRequest(
                requestId,
                request.getRequestURI(),
                () -> new ResponseEntity<>(userService.createUser(createUserRequestDto), CREATED)
        );
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long userId) {
        log.info("Deleting user with ID: {}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        log.info("Retrieving all users");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Override
    public ResponseEntity<UserResponseDto> getUserById(Long userId) {
        log.info("Retrieving user with ID: {}", userId);
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @Override
    public ResponseEntity<List<ProjectResponseDto>> getUserProjects(Long userId) {
        log.info("Retrieving projects for user with ID: {}", userId);
        return ResponseEntity.ok(userService.getUserProjects(userId));
    }

    @Override
    public ResponseEntity<UserResponseDto> updateUser(Long userId, String requestId, UpdateUserRequestDto updateUserRequestDto) {
        return idempotencyService.processIdempotentRequest(
                requestId,
                request.getRequestURI(),
                () -> new ResponseEntity<>(userService.updateUser(userId, updateUserRequestDto), OK)
        );
    }

}
