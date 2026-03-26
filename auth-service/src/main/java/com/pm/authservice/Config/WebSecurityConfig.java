package com.pm.authservice.Config;

import com.pm.authservice.Filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Disable CSRF (JWT based)
                .csrf(csrf -> csrf.disable())

                // Stateless session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/calls/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/posts/**").permitAll()
                        .requestMatchers("/auth/**", "/login/**", "/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )

                // JWT filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                // OAuth2 Login
                /*.oauth2Login(oauth2 -> oauth2
                                .failureUrl("/login?error=true")
                        // .successHandler(oAuth2SuccessHandler) // recommended
                )*/

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}