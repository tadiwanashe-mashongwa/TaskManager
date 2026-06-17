package com.example.taskmanager.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {
    @Bean // 💡 This tells Spring: "Run this method and put the returned object into the container!"
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // You explicitly choose the algorithm implementation here
    }
}
