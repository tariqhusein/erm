package com.sky.erm.service.impl;

import com.sky.erm.domain.ErmUser;
import com.sky.erm.domain.UserExternalProject;
import com.sky.erm.domain.UserExternalProjectId;
import com.sky.erm.exception.DuplicateEmailException;
import com.sky.erm.exception.UserNotFoundException;
import com.sky.erm.mapper.ProjectMapper;
import com.sky.erm.mapper.UserMapper;
import com.sky.erm.model.CreateProjectRequestDto;
import com.sky.erm.model.CreateUserRequestDto;
import com.sky.erm.model.ProjectResponseDto;
import com.sky.erm.model.UserResponseDto;
import com.sky.erm.model.UpdateUserRequestDto;
import com.sky.erm.repository.UserRepository;
import com.sky.erm.service.MetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MetricsService metricsService;

    @InjectMocks
    private UserServiceImpl userService;

    private CreateUserRequestDto createUserRequest;
    private UpdateUserRequestDto updateUserRequest;
    private CreateProjectRequestDto createProjectRequest;
    private ErmUser mockUser;
    private UserExternalProject mockProject;

    @BeforeEach
    void setUp() {
        // Initialize test data
        createUserRequest = new CreateUserRequestDto();
        createUserRequest.setEmail("test@example.com");
        createUserRequest.setName("Test User");
        createUserRequest.setPassword("password123");

        updateUserRequest = new UpdateUserRequestDto();
        updateUserRequest.setEmail("updated@example.com");
        updateUserRequest.setName("Updated User");
        updateUserRequest.setPassword("newpassword123");

        createProjectRequest = new CreateProjectRequestDto();
        createProjectRequest.setName("Test Project");
        createProjectRequest.setId("proj-123");

        mockUser = new ErmUser();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setName("Test User");
        mockUser.setPassword("encoded_password");

        mockProject = new UserExternalProject();
        UserExternalProjectId projectId = new UserExternalProjectId("proj-123", 1L);
        mockProject.setId(projectId);
        mockProject.setName("Test Project");
        mockProject.setUser(mockUser);
    }

    @Test
    void createUser_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userMapper.toEntity(any(CreateUserRequestDto.class))).thenReturn(mockUser);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(ErmUser.class))).thenReturn(mockUser);
        when(userMapper.toDto(any(ErmUser.class))).thenReturn(mock(UserResponseDto.class));

        UserResponseDto result = userService.createUser(createUserRequest);

        assertNotNull(result);
        verify(userRepository).save(any(ErmUser.class));
        verify(metricsService).incrementUserCreation();
    }

    @Test
    void createUser_DuplicateEmail() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

        assertThrows(DuplicateEmailException.class, () -> 
            userService.createUser(createUserRequest)
        );
    }

    @Test
    void getAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(mockUser));
        when(userMapper.toDto(any(ErmUser.class))).thenReturn(mock(UserResponseDto.class));

        List<UserResponseDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(userMapper).toDto(mockUser);
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findByIdWithProjects(anyLong())).thenReturn(Optional.of(mockUser));
        when(userMapper.toDto(any(ErmUser.class))).thenReturn(mock(UserResponseDto.class));

        UserResponseDto result = userService.getUserById(1L);

        assertNotNull(result);
        verify(userMapper).toDto(mockUser);
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findByIdWithProjects(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> 
            userService.getUserById(1L)
        );
    }

    @Test
    void updateUser_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("new_encoded_password");
        when(userRepository.save(any(ErmUser.class))).thenReturn(mockUser);
        when(userMapper.toDto(any(ErmUser.class))).thenReturn(mock(UserResponseDto.class));

        UserResponseDto result = userService.updateUser(1L, updateUserRequest);

        assertNotNull(result);
        verify(metricsService).incrementUserUpdate();
        verify(userRepository).save(mockUser);
    }

    @Test
    void updateUser_DuplicateEmail() {
        ErmUser existingUser = new ErmUser();
        existingUser.setId(2L);
        
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(userRepository.findByEmail(updateUserRequest.getEmail())).thenReturn(Optional.of(existingUser));

        assertThrows(DuplicateEmailException.class, () -> 
            userService.updateUser(1L, updateUserRequest)
        );
    }

    @Test
    void addProjectToUser_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(projectMapper.toEntity(any(CreateProjectRequestDto.class), any(ErmUser.class))).thenReturn(mockProject);
        when(userRepository.save(any(ErmUser.class))).thenReturn(mockUser);
        when(projectMapper.toDto(any(UserExternalProject.class))).thenReturn(mock(ProjectResponseDto.class));

        ProjectResponseDto result = userService.addProjectToUser(1L, createProjectRequest);

        assertNotNull(result);
        verify(metricsService).incrementProjectAddition();
        verify(userRepository).save(mockUser);
    }

    @Test
    void getUserProjects_Success() {
        mockUser.setExternalProjects(Collections.singleton(mockProject));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(projectMapper.toDto(any(UserExternalProject.class))).thenReturn(mock(ProjectResponseDto.class));

        List<ProjectResponseDto> result = userService.getUserProjects(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(projectMapper).toDto(mockProject);
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(userRepository).deleteById(anyLong());

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_NotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> 
            userService.deleteUser(1L)
        );
    }
} 