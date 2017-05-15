/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hantsylabs.sample.springmicroservice.post;

/**
 *
 * @author hantsy
 */
public class PostNotFoundException extends RuntimeException {

    private Long id;

    public PostNotFoundException(Long id) {
        super("post:" + id + " was not found");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    
}
