package com.sky.erm.service.impl;

import com.sky.erm.domain.ErmUser;
import com.sky.erm.domain.UserExternalProject;
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
import com.sky.erm.service.UserService;
import io.micrometer.core.annotation.Timed;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ProjectMapper projectMapper;
    private final PasswordEncoder passwordEncoder;
    private final MetricsService metricsService;

    @PostConstruct
    public void init() {
        metricsService.init();
    }

    @Override
    @Timed(value = "user.creation", description = "Time taken to create a new user")
    public UserResponseDto createUser(CreateUserRequestDto createUserRequestDto) {
        if (userRepository.findByEmail(createUserRequestDto.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email already exists: " + createUserRequestDto.getEmail());
        }

        ErmUser user = userMapper.toEntity(createUserRequestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        metricsService.incrementUserCreation();
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    @Timed(value = "project.addition", description = "Time taken to add a project to a user")
    public ProjectResponseDto addProjectToUser(Long userId, CreateProjectRequestDto createProjectRequestDto) {
        ErmUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        UserExternalProject project = projectMapper.toEntity(createProjectRequestDto, user);
        project.setUser(user);
        user.addExternalProject(project);

        user = userRepository.save(user);
        metricsService.incrementProjectAddition();
        return projectMapper.toDto(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDto> getUserProjects(Long userId) {
        ErmUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        return user.getExternalProjects().stream()
                .map(projectMapper::toDto)
                .toList();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ErmUser user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return User.builder()
                .username(user.getName())
                .password(user.getPassword())
                .build();
    }

    @Override
    @Timed(value = "user.update", description = "Time taken to update a user")
    public UserResponseDto updateUser(Long userId, UpdateUserRequestDto updateUserRequestDto) {
        ErmUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (updateUserRequestDto.getEmail() != null) {
            userRepository.findByEmail(updateUserRequestDto.getEmail())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(userId)) {
                            throw new DuplicateEmailException("Email already exists: " + updateUserRequestDto.getEmail());
                        }
                    });
            user.setEmail(updateUserRequestDto.getEmail());
        }
        
        if (updateUserRequestDto.getName() != null) {
            user.setName(updateUserRequestDto.getName());
        }
        
        if (updateUserRequestDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateUserRequestDto.getPassword()));
        }

        user = userRepository.save(user);
        metricsService.incrementUserUpdate();
        return userMapper.toDto(user);
    }
}