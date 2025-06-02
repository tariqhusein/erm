package com.sky.erm.service;

import com.sky.erm.model.CreateProjectRequestDto;
import com.sky.erm.model.CreateUserRequestDto;
import com.sky.erm.model.ProjectResponseDto;
import com.sky.erm.model.UserResponseDto;
import com.sky.erm.model.UpdateUserRequestDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserResponseDto createUser(CreateUserRequestDto createUserRequestDto);
    List<UserResponseDto> getAllUsers();
    UserResponseDto getUserById(Long userId);
    void deleteUser(Long userId);
    ProjectResponseDto addProjectToUser(Long userId, CreateProjectRequestDto createProjectRequestDto);
    List<ProjectResponseDto> getUserProjects(Long userId);
    UserResponseDto updateUser(Long userId, UpdateUserRequestDto updateUserRequestDto);
} 