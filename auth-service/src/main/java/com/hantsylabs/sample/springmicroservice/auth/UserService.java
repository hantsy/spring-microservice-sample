/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hantsylabs.sample.springmicroservice.auth;

import javax.inject.Inject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author hantsy
 */
@Service
public class UserService {

    @Inject
    private UserRepository userRepository;

    @Inject
    private PasswordEncoder passwordEncoder;

    public User handleSignup(SignupForm form) {
        if(this.userRepository.findByUsername(form.getUsername()).isPresent()){
            throw new UsernameWasTakenException("username was taken");
        }
        
        if(this.userRepository.findByEmail(form.getEmail()).isPresent()){
            throw new EmailWasTakenException("email was taken");
        }
        
        User _user = User.builder()
            .username(form.getUsername())
            .password(passwordEncoder.encode(form.getPassword()))
            .email(form.getEmail())
            .build();
        return this.userRepository.save(_user);
    }
}
