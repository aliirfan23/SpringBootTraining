package com.redmath.security;

import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("Security config loaded");
        return http
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/v1/news/**").hasRole("REPORTER")
                        .requestMatchers(  "/api/v1/{newsId}").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .csrf(csrf->csrf.disable())
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .headers(headers -> headers.disable())
                .build();
    }

}
