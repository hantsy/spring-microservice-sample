/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hantsylabs.sample.springmicroservice.post;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 *
 * @author hantsy
 */
@Configuration
@Slf4j
public class SecurityConfig {
    
    @Bean
    public WebSecurityConfigurerAdapter securityConfigBean(){
        
        return new  WebSecurityConfigurerAdapter() {

            @Override
            protected void configure(HttpSecurity http) throws Exception {
                // We need this to prevent the browser from popping up a dialog on a 401
                http
                    .httpBasic()
                    .and()
                        .authorizeRequests()
                        .antMatchers(HttpMethod.GET, "/posts/**").permitAll()
                        .antMatchers(HttpMethod.DELETE, "/posts/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                    .and()
                        .csrf().disable();
            }
        };   
    }
}
