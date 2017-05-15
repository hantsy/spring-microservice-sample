/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hantsylabs.sample.springmicroservice.auth;

/**
 *
 * @author hantsy
 */
public class UserNotFoundException extends RuntimeException {

    Long id;

    public UserNotFoundException(Long id) {
        super("user:" + id + " was not found");
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }
}
