package com.kobylchak.carsharing.security;

import com.kobylchak.carsharing.repository.UserReposiotry;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserReposiotry userReposiotry;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userReposiotry.findByEmailWithRoles(email)
                       .orElseThrow(() -> new UsernameNotFoundException(
                               "Can't find user by email."
                       ));
    }
}
