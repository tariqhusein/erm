package com.sky.erm.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.erm.config.TestSecurityConfig;
import com.sky.erm.model.*;
import com.sky.erm.service.IdempotencyService;
import com.sky.erm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerTest {

    private static final String AUTHORIZATION_HEADER = "Basic dGFyZWs6cGFzc3dvcmQ=";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserService userService;

    @MockBean
    private IdempotencyService idempotencyService;

    private UserResponseDto mockUserResponse;
    private CreateUserRequestDto createUserRequest;
    private UpdateUserRequestDto updateUserRequest;
    private ProjectResponseDto mockProjectResponse;
    private CreateProjectRequestDto createProjectRequest;

    @BeforeEach
    void setUp() {
        // Initialize mock user response
        mockUserResponse = new UserResponseDto();
        mockUserResponse.setId(1L);
        mockUserResponse.setEmail("test@example.com");

        // Initialize create user request with required fields
        createUserRequest = new CreateUserRequestDto();
        createUserRequest.setName("newUser");
        createUserRequest.setEmail("newuser@example.com");
        createUserRequest.setPassword("password123");

        // Initialize update user request with required fields
        updateUserRequest = new UpdateUserRequestDto();
        updateUserRequest.setName("updatedUser");
        updateUserRequest.setEmail("updated@example.com");

        // Initialize mock project response
        mockProjectResponse = new ProjectResponseDto();
        mockProjectResponse.setId("123");
        mockProjectResponse.setName("Test Project");

        // Initialize create project request with required fields
        createProjectRequest = new CreateProjectRequestDto();
        createProjectRequest.setName("New Project");
        createProjectRequest.setId("abc");

        // Configure UserService for authentication
        when(userService.loadUserByUsername("tarek"))
                .thenReturn(User.withUsername("tarek")
                        .password(passwordEncoder.encode("password"))
                        .build());
    }

    @Test
    void createUser_Success() throws Exception {
        when(userService.createUser(any(CreateUserRequestDto.class))).thenReturn(mockUserResponse);

        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", AUTHORIZATION_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void createUser_WithIdempotency_Success() throws Exception {
        when(idempotencyService.processIdempotentRequest(
                eq("test-request-id"),
                any(),
                any()
        )).thenReturn(ResponseEntity.status(201).body(mockUserResponse));

        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", AUTHORIZATION_HEADER)
                        .header("Request-Id", "test-request-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void getAllUsers_Success() throws Exception {
        List<UserResponseDto> users = Arrays.asList(mockUserResponse);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", AUTHORIZATION_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void getUserById_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(mockUserResponse);

        mockMvc.perform(get("/api/v1/users/1")
                        .header("Authorization", AUTHORIZATION_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void updateUser_Success() throws Exception {
        when(userService.updateUser(eq(1L), any(UpdateUserRequestDto.class))).thenReturn(mockUserResponse);

        mockMvc.perform(put("/api/v1/users/1")
                        .header("Authorization", AUTHORIZATION_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void updateUser_WithIdempotency_Success() throws Exception {
        when(idempotencyService.processIdempotentRequest(
                eq("test-request-id"),
                any(),
                any()
        )).thenReturn(ResponseEntity.ok(mockUserResponse));

        mockMvc.perform(put("/api/v1/users/1")
                        .header("Authorization", AUTHORIZATION_HEADER)
                        .header("Request-Id", "test-request-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/v1/users/1")
                        .header("Authorization", AUTHORIZATION_HEADER))
                .andExpect(status().isNoContent());
    }

    @Test
    void getUserProjects_Success() throws Exception {
        List<ProjectResponseDto> projects = Arrays.asList(mockProjectResponse);
        when(userService.getUserProjects(1L)).thenReturn(projects);

        mockMvc.perform(get("/api/v1/users/1/projects")
                        .header("Authorization", AUTHORIZATION_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void addProjectToUser_Success() throws Exception {
        when(userService.addProjectToUser(eq(1L), any(CreateProjectRequestDto.class))).thenReturn(mockProjectResponse);

        mockMvc.perform(post("/api/v1/users/1/projects")
                        .header("Authorization", AUTHORIZATION_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProjectRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void addProjectToUser_WithIdempotency_Success() throws Exception {
        when(idempotencyService.processIdempotentRequest(
                eq("test-request-id"),
                any(),
                any()
        )).thenReturn(ResponseEntity.status(201).body(mockProjectResponse));

        mockMvc.perform(post("/api/v1/users/1/projects")
                        .header("Authorization", AUTHORIZATION_HEADER)
                        .header("Request-Id", "test-request-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProjectRequest)))
                .andExpect(status().isCreated());
    }
} 