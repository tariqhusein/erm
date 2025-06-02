package com.sky.erm.config;

import com.sky.erm.domain.ErmUser;
import com.sky.erm.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile({"local","test"})
public class LocalDataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Create a test user if it doesn't exist
            if (userRepository.findByEmail("test@sky.com").isEmpty()) {
                ErmUser user = new ErmUser();
                user.setEmail("test@sky.com");
                user.setPassword(passwordEncoder.encode("password"));
                user.setName("tarek");
                
                userRepository.save(user);
                
                System.out.println("Local profile: Initialized test user - test@sky.com");
            }
        };
    }
} 