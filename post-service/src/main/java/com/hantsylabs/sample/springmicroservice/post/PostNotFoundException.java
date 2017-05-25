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

    private String slug;

    public PostNotFoundException(String slug) {
        super("post:" + slug + " was not found");
        this.slug = slug;
    }

    public String getSlug() {
        return slug;
    }
    
}
