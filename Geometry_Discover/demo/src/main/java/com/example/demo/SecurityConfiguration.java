package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
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
                .requestMatchers("/", "/home", "/login", "/register", "/search", "/css/**", "/js/**", "/images/**", "/profile").permitAll()
                .requestMatchers("/api/level-feedback/*/rating").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/api/level-feedback/*/comment").hasAnyRole("ADMIN", "USER")

                .requestMatchers("/api/level-feedback").authenticated()
                .requestMatchers("/api/level-feedback/**").authenticated()
            )


            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout.permitAll());

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails Henny = User.withUsername("TheGodNoob")
            .password(passwordEncoder.encode("GeometryDiscoverAdmin560!"))
            .roles("ADMIN")
            .build();

        UserDetails TakashiTheAeternusGlazer = User.withUsername("Takashi")
            .password(passwordEncoder.encode("GeometryDiscoverAdmin270!"))
            .roles("ADMIN")
            .build();
        
        UserDetails Sam = User.withUsername("Sam")
            .password(passwordEncoder.encode("GeometryDiscoverAdmin120!"))
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(Henny, TakashiTheAeternusGlazer, Sam);
    }
}
