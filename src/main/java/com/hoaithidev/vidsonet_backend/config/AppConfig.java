package com.hoaithidev.vidsonet_backend.config;

import com.hoaithidev.vidsonet_backend.enums.UserRole;
import com.hoaithidev.vidsonet_backend.model.Category;
import com.hoaithidev.vidsonet_backend.model.User;
import com.hoaithidev.vidsonet_backend.repository.CategoryRepository;
import com.hoaithidev.vidsonet_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AppConfig {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner (){
        return args -> {
            if(!userRepository.existsByUsername("admin")){
                Category category = Category.builder()
                        .name("Sport")
                        .build();
                categoryRepository.save(category);
                User admin =  User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .email("adminvidsonet@gmail.com")
                        .role(UserRole.ADMIN)
                        .build();
                userRepository.save(admin);
            }
            log.info("Application started");
        };
    }

}
