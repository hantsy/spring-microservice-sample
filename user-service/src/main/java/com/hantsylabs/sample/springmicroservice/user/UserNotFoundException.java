/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hantsylabs.sample.springmicroservice.user;

/**
 *
 * @author hantsy
 */
public class UserNotFoundException extends RuntimeException {

    String username;

    public UserNotFoundException(String username) {
        super("user:" + username + " was not found");
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }
}
