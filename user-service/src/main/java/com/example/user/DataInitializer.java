/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.user;

import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author hantsy
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... strings) throws Exception {
        User _admin = User.builder()
                .username("admin")
                .password(this.passwordEncoder.encode("test123"))
                .email("admin@test.com")
                .roles(Arrays.asList("ADMIN"))
                .build();

        User _user = User.builder()
                .username("user")
                .password(this.passwordEncoder.encode("test123"))
                .email("user@test.com")
                .roles(Arrays.asList("USER"))
                .build();

        this.userRepository.saveAll(Arrays.asList(_admin, _user));
    }

}
