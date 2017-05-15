/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hantsylabs.sample.springmicroservice.user;

import java.util.Arrays;
import javax.inject.Inject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 *
 * @author hantsy
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Inject
    private UserRepository userRepository;

    @Inject
    private PasswordEncoder passwordEncoder;

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
