package com.example.auth;

import com.example.auth.model.User;
import com.example.auth.service.UserServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.junit.Assert.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(UserServiceClient.class)
@Slf4j
public class UserServiceClientTest {

    @Value("${services.user-service-url:http://localhost:8001}")
    private String userServiceUrl;

    @Autowired
    private UserServiceClient client;

    @Autowired
    private MockRestServiceServer server;

    @Test
    public void testFindbyUsername() {
        this.server.expect(requestTo(userServiceUrl + "/users/user"))
            .andRespond(withSuccess(new ClassPathResource("/find-user-by-username.json"), MediaType.APPLICATION_JSON_UTF8));
            //.andRespond(withSuccess("{\"username\":\"user\",\"password\":\"password\",\"email\":\"user@example.com\"}", MediaType.APPLICATION_JSON_UTF8));

        User user = this.client.findByUsername("user");
        assertNotNull(user);
        assertEquals("user", user.getUsername());

        this.server.verify();
    }

    @Test
    public void testFindbyUsername_notFound() {
        this.server.expect(requestTo(userServiceUrl + "/users/user1"))
            .andRespond(withStatus(NOT_FOUND));

        User user = this.client.findByUsername("user1");
        assertNull(user);

        this.server.verify();
    }
}
