package com.hantsylabs.sample.springmicroservice.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Component
public class UserServiceClient {

    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    @Value("${services.user-service-url}")
    private String userServiceUrl;

    public UserServiceClient(RestTemplateBuilder builder, ObjectMapper objectMapper) {
        this.restTemplate = builder.build();
        this.objectMapper = objectMapper;
    }

    public void handleSignup(SignupForm form) {
        try {
            ResponseEntity<Void> response = this.restTemplate.postForEntity(userServiceUrl + "/users", form, Void.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == CONFLICT) {
                Map map = null;
                try {
                    map = objectMapper.readValue(e.getResponseBodyAsByteArray(), Map.class);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                throw new SignupConflictException((String) map.get("message"));
            }
        }
    }

    public User findByUsername(String username) {
        try {
            ResponseEntity<User> response = this.restTemplate.getForEntity(userServiceUrl + "/users/{username}", User.class, username);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == NOT_FOUND) {
                return null;
            }
        }
        return null;
    }
}
