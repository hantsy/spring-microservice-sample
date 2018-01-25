package com.hantsylabs.sample.springmicroservice.post;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

import java.util.Optional;

@SpringBootApplication
public class PostServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostServiceApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

@Configuration
class RedisSessionConfig {

    @Bean
    public HttpSessionIdResolver httpSessionStrategy() {
        return HeaderHttpSessionIdResolver.xAuthToken();
    }

}

@Configuration
@Slf4j
class SecurityConfig {

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

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@Slf4j
class DataJpaConfig {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public AuditorAware<Username> auditorAware() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.debug("current authentication:" + authentication);

        if (authentication == null || !authentication.isAuthenticated()) {
            return () -> Optional.<Username>empty();
        }

        return () -> Optional.of(
            Username.builder()
                .username(((UserDetails) authentication.getPrincipal()).getUsername())
                .build()
        );

    }
}

