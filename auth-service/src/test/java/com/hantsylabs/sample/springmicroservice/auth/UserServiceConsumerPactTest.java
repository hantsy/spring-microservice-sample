package com.hantsylabs.sample.springmicroservice.auth;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class UserServiceConsumerPactTest {

    @Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2("user-service", "localhost", 8001, this);

    @Autowired
    private UserServiceClient client;

    @Before
    public void setup() {

    }

    @Pact(consumer = "auth-service")
    public RequestResponsePact createFragment(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");

        //@formatter:off
        return builder
            .given("should return user if existed")
                .uponReceiving("Get a user by existing username: user")
                .path("/users/user")
                .method("GET")
            .willRespondWith()
                .headers(headers)
                .status(200)
                .body("{\"username\": \"user\", \"password\": \"password\", \"email\": \"user@example.com\" }")
            .given("should return error 404 if not existed")
                .uponReceiving("Get a user by none-existing username: noneExisting")
                .method("GET")
                .path("/users/noneExisting")
            .willRespondWith()
                .headers(headers)
                .status(404)
                .body("{\"entity\": \"USER\",\"id\": \"noneExisting\",\"code\": \"not_found\",\"message\": \"User:noneExisting was not found\"}")
            .toPact();
        //@formatter:on
    }

    @Test
    @PactVerification()
    public void runTest() throws IOException {
        User user = this.client.findByUsername("user");
        assertNotNull(user);
        assertEquals("user", user.getUsername());
        User _user = this.client.findByUsername("noneExisting");
        assertNull(_user);
    }
}
