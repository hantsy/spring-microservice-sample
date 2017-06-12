/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hantsylabs.sample.springmicroservice.auth;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 *
 * @author hantsy
 */
@RestControllerAdvice
public class AuthExeceptionHandler {

    @ExceptionHandler(value = {UsernameWasTakenException.class, EmailWasTakenException.class})
    public ResponseEntity signupFailed(Exception ex, WebRequest req) {
        Map<String, String> errorMsg = new HashMap<>();
        errorMsg.put("code", "conflict");
        errorMsg.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex);
    }

}
