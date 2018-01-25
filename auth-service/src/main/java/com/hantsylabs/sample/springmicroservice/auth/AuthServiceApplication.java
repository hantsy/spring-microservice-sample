package com.hantsylabs.sample.springmicroservice.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600 * 24)
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
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
//@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
            http
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/auth/signin", "/auth/signup", "/").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable();
            //.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
            // @formatter:on
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
