/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.auth.web;

import java.io.Serializable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.Data;


/**
 *
 * @author hantsy
 */
@Data
public class SignupForm implements Serializable {

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    @Email
    @NotEmpty
    private String email;
}
