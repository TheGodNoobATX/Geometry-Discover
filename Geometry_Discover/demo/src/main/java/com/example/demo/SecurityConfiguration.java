package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserService userService) throws Exception {
        http
            .userDetailsService(userService)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/home", "/login", "/register", "/search", "/error", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/profile/**").permitAll()
                .requestMatchers("/profile").authenticated()
                .requestMatchers("/account", "/account/**").authenticated()
                .requestMatchers("/api/level-feedback/*/rating").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.DELETE, "/api/level-feedback/*/comment").hasRole("ADMIN")
                .requestMatchers("/api/level-feedback/*/comment").hasAnyRole("ADMIN", "USER")

                .requestMatchers("/api/level-feedback").permitAll()
                .requestMatchers("/api/level-feedback/**").authenticated()
            )


            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )
            .logout(logout -> logout.permitAll());

        return http.build();
    }
}
