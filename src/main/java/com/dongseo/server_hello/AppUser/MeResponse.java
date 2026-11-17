package com.dongseo.server_hello.AppUser;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public record MeResponse(String username, List<String> authorities) {
    public static MeResponse from(Authentication auth) {
        return new MeResponse(
                auth.getName(),                         // basic = username & JWT = "sub" claim
                auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority) // e.g. "ROLE_USER"
                        .toList()
        );
    }
}
