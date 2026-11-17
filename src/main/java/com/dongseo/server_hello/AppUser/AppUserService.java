package com.dongseo.server_hello.AppUser;

import com.dongseo.server_hello.Exception.ConflictException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;  // Bcrypt bean (SecurityConfig)
    private final JwtEncoder jwtEncoder;
    private final long ttlMinutes;

    public AppUserService(AppUserRepository appUserRepository,
                          PasswordEncoder passwordEncoder,
                          JwtEncoder jwtEncoder,
                          @Value("${app.jwt.ttl-minutes}") long ttlMinutes) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.ttlMinutes = ttlMinutes;
    }

    @Transactional
    public AppUser signup(String username, String password) {
        if (appUserRepository.existsByUsername(username)) {
            throw new ConflictException("username " + username + " is taken");
        }
        return appUserRepository.save(new AppUser(
                username, passwordEncoder.encode(password), "USER"));
    }

    public TokenResponse login(String username, String password) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("wrong username or password"));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {throw new BadCredentialsException("wrong username or password");}
        Instant rn = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("room-api").issuedAt(rn)
                .expiresAt(rn.plus(ttlMinutes, ChronoUnit.MINUTES))
                .subject(user.getUsername())
                .claim("role", user.getRole())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new TokenResponse(token, "Bearer", ttlMinutes * 60);
    }
}
