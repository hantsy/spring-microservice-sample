/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hantsylabs.sample.springmicroservice.test;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

/**
 *
 * @author hantsy
 */
@Data
@Builder
public class AuthenticationRequest implements Serializable{
    private String username;
    private String password;
}
