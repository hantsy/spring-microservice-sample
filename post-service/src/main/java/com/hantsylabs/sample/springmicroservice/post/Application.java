package com.hantsylabs.sample.springmicroservice.post;

import java.util.Optional;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.TraceProperties;
import org.springframework.boot.actuate.trace.TraceRepository;
import org.springframework.boot.actuate.trace.WebRequestTraceFilter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;

@SpringBootApplication
public class Application extends WebSecurityConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public HttpSessionStrategy httpSessionStrategy() {
        return new HeaderHttpSessionStrategy();
    }

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

    @Bean
    public WebRequestTraceFilter webRequestLoggingFilter(ErrorAttributes errorAttributes,
        TraceRepository traceRepository, TraceProperties traceProperties) {
        WebRequestTraceFilter filter = new WebRequestTraceFilter(traceRepository,
            traceProperties);
        if (errorAttributes != null) {
            filter.setErrorAttributes(errorAttributes);
        }
        filter.setOrder(SecurityProperties.DEFAULT_FILTER_ORDER - 1);
        return filter;
    }

    @Bean
    public AuditorAware<UserId> auditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {

            return () -> Optional.of(
                UserId.builder()
                    .id(((UserDetails) authentication.getPrincipal()).getUsername())
                    .build()
            );
        }

        return null;
    }

}
