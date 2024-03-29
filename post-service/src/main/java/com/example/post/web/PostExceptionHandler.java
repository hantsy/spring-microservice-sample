/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.post.web;

import java.util.HashMap;
import java.util.Map;

import com.example.post.service.PostNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.http.ResponseEntity.*;

/**
 *
 * @author hantsy
 */
@RestControllerAdvice
public class PostExceptionHandler {

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity notFound(PostNotFoundException ex, WebRequest req) {
        Map<String, String> errors = new HashMap<>();
        errors.put("entity", "POST");
        errors.put("id", "" + ex.getSlug());
        errors.put("code", "not_found");
        errors.put("message", ex.getMessage());

        return status(HttpStatus.NOT_FOUND).body(errors);
    }

}
