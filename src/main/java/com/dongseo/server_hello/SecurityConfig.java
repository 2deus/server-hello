package com.dongseo.server_hello;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, ApiAuthenticationEntryPoint entryPoint) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // no browser forms/cookies = no CSRF
            .sessionManagement(s -> s.sessionCreationPolicy(STATELESS)) // no session = logs in each time
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(POST, "/api/auth/signup").permitAll()
                    .requestMatchers(POST, "/api/auth/login").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**",
                            "/swagger-ui.html", "/error").permitAll()
                    .requestMatchers(GET, "/api/auth/me").authenticated()   // must be ABOVE GET /api/**
                    .requestMatchers(GET, "/api/**").permitAll()            // reading is public
                    .anyRequest().authenticated())                              // everything else needs a login
            .httpBasic(b -> b.authenticationEntryPoint(entryPoint))                 // wrong Basic login = 401
                .oauth2ResourceServer(o -> o
                        .authenticationEntryPoint(entryPoint)
                        .jwt(Customizer.withDefaults()))
            .exceptionHandling(e -> e.authenticationEntryPoint(entryPoint)); // no login = 401
        return http.build();
    }

    @Bean
    public SecretKey jwtKey(@Value("${app.jwt.secret}") String secret) {
        return new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtKey));
    }
    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtKey) {
        return NimbusJwtDecoder.withSecretKey(jwtKey)
                .macAlgorithm(MacAlgorithm.HS256).build();
    }
}
