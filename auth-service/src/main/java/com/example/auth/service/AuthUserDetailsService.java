package com.example.auth.service;

import com.example.auth.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthUserDetailsService implements UserDetailsService {

    private UserServiceClient userServiceClient;

    public AuthUserDetailsService(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User _user = userServiceClient.findByUsername(username);

        if (_user == null) {
            throw new UsernameNotFoundException("username not found:" + username);
        }

        return org.springframework.security.core.userdetails.User
            .withUsername(_user.getUsername())
            .password(_user.getPassword())
            //.authorities(AuthorityUtils.createAuthorityList(_user.getRoles().toArray(new String[0])))
            .roles(_user.getRoles().toArray(new String[0]))
            .accountLocked(!_user.isActive())
            .accountExpired(!_user.isActive())
            .disabled(!_user.isActive())
            .credentialsExpired(!_user.isActive())
            .build();
    }

}
