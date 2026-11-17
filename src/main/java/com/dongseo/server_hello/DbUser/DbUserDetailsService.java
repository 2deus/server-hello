package com.dongseo.server_hello.DbUser;

import com.dongseo.server_hello.AppUser.AppUser;
import com.dongseo.server_hello.AppUser.AppUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DbUserDetailsService implements UserDetailsService {
    private final AppUserRepository appUserRepository;

    public DbUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        // User here is Spring Security's class, not our AppUser and not Tomcat's User
        return User.withUsername(user.getUsername())
                .password(user.getPasswordHash())   // Bcrypt hash
                .roles(user.getRole())              // "USER" becomes "ROLE_USER"
                .build();
    }
}
