/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hantsylabs.sample.springmicroservice.user;

import javax.inject.Inject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author hantsy
 */
@Service
@Transactional
public class UserService {

    @Inject
    private UserRepository userRepository;

    @Inject
    private PasswordEncoder passwordEncoder;

    public User createUser(UserForm form) {
        if (this.userRepository.findByUsername(form.getUsername()).isPresent()) {
            throw new UsernameWasTakenException("username was taken");
        }

        if (this.userRepository.findByEmail(form.getEmail()).isPresent()) {
            throw new EmailWasTakenException("email was taken");
        }

        User _user = User.builder()
            .username(form.getUsername())
            .password(passwordEncoder.encode(form.getPassword()))
            .email(form.getEmail())
            .build();
        return this.userRepository.save(_user);
    }

    public User updateUser(String username, UserForm form) {
        User _user = this.userRepository.findByUsername(username).orElseThrow(
            () -> {
                return new UserNotFoundException(username);
            }
        );
        
            
        _user.setEmail(form.getEmail());
        
        return this.userRepository.save(_user);
    }

    public User lock(String username) {
         User _user = this.userRepository.findByUsername(username).orElseThrow(
            () -> {
                return new UserNotFoundException(username);
            }
        );
        
            
        _user.setActive(false);
        
        return this.userRepository.save(_user);
    }

    public User unlock(String username) {
         User _user = this.userRepository.findByUsername(username).orElseThrow(
            () -> {
                return new UserNotFoundException(username);
            }
        );
            
        _user.setActive(true);
        
        return this.userRepository.save(_user);
    }
}
