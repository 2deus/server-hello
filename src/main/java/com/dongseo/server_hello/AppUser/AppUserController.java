package com.dongseo.server_hello.AppUser;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppUserController {
    private final AppUserService appUserService;
    public AppUserController(AppUserService appUserService) { this.appUserService = appUserService; }

    @PostMapping("/api/auth/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AppUserResponse signup(@Valid @RequestBody SignUpRequest req) {
        return AppUserResponse.from(
                appUserService.signup(req.username(), req.password()));
    }

    // spring fills in Authentication with whoever the security filters logged in
    // SecurityConfig rule makes this route authenticated so it is never null
    @GetMapping("/api/auth/me")
    public MeResponse me(Authentication authentication) {
        return MeResponse.from(authentication);
    }

    @PostMapping("/api/auth/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req) {
        return appUserService.login(req.username(), req.password());
    }
}
