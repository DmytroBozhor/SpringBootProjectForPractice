package com.example.demowithtests.util.config;

import com.example.demowithtests.service.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private final UserService userService;

    //TODO: 30-July-23 Create 2 users for demo
    @Bean
    public UserDetailsService userDetailsService() {

        List<com.example.demowithtests.domain.User> userList = userService.findAll();
        List<UserDetails> userDetailsList = userList.stream()
                .map(user -> User
                        .withUsername(user.getName())
                        .password(user.getPassword())
                        .roles(user.getRole().toString())
                        .build()).toList();

        return new InMemoryUserDetailsManager(userDetailsList);
    }

    // TODO: 30-July-23 Secure the endpoints with HTTP Basic authentication
    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {

        return http
                //HTTP Basic authentication
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasRole("USER")
//                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.POST, "/api/employees").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.POST, "/api/users/jpa").hasRole("USER")
//                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.PATCH, "/api/users/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                                .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                .build();
    }
}
