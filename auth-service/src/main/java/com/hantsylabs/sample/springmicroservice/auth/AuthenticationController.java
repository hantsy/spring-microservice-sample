package com.hantsylabs.sample.springmicroservice.auth;

import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Hantsy Bai<hantsy@gmail.com>
 */
@RequestMapping(value = "/auth")
@RestController
public class AuthenticationController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);
    
    @Inject
    private UserService userService;
    
    @Inject
    private AuthenticationManager authenticationManager;
    
    public AuthenticationController() {
    }
    
    @PostMapping(value = "/signin")
    public AuthenticationResult signin(
        @RequestBody @Valid AuthenticationRequest authenticationRequest,
        HttpServletRequest request) {
        
        if (log.isDebugEnabled()) {
            log.debug("signin form  data@" + authenticationRequest);
        }
        
        return this.handleAuthentication(
            authenticationRequest.getUsername(),
            authenticationRequest.getPassword(),
            request);
    }
    
    private AuthenticationResult handleAuthentication(
        String username,
        String password,
        HttpServletRequest request) {
        
        final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            username,
            password
        );
        
        final Authentication authentication = this.authenticationManager
            .authenticate(token);
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        final HttpSession session = request.getSession(true);
        
        session.setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            SecurityContextHolder.getContext());
        
        return AuthenticationResult.builder()
            .name(authentication.getName())
            .roles(authentication.getAuthorities().stream().map(r -> r.getAuthority()).collect(Collectors.toList()))
            .token(session.getId())
            .build();
    }
    
    @PostMapping(value = {"/signup"})
    public AuthenticationResult signup(
        @RequestBody @Valid SignupForm form,
        HttpServletRequest req) {
        log.debug("signup data@" + form);
        
        User saved = this.userService.handleSignup(form);
        
        log.debug("registered user:" + saved);
        return this.handleAuthentication(form.getUsername(), form.getPassword(), req);
    }
    
    @PostMapping(value = {"/signout"})
    public ResponseEntity signout(HttpServletRequest req) {
        req.getSession().invalidate();
        return ResponseEntity.ok().build();
    }
}
