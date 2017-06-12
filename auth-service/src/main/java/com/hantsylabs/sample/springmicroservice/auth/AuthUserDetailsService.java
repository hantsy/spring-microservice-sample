package com.hantsylabs.sample.springmicroservice.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public AuthUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User _user = userRepository.findByUsername(username)
            .orElseThrow(
                () -> {
                    return new UsernameNotFoundException("username not found:" + username);
                }
            );

        return  org.springframework.security.core.userdetails.User.withUserDetails(_user).build();
    }

}
