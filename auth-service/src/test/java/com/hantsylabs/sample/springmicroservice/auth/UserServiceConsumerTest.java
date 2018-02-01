package com.hantsylabs.sample.springmicroservice.auth;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.test.context.junit4.SpringRunner;
import wiremock.org.apache.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureStubRunner(workOffline = true, ids = "com.hantsylabs.sample.springmicroservice:user-service:+:stubs:8001")
@AutoConfigureStubRunner(
    workOffline = true,
    stubsPerConsumer = true,
    consumerName = "auth-service",
    ids = "com.hantsylabs.sample.springmicroservice.contracts:user-service-producer:+:stubs:8001"
)
@Slf4j
public class UserServiceConsumerTest {

    @Autowired
    private UserServiceClient client;

    @Before
    public void setup() {

    }

    @Test
    public void testFindbyUsername() {
        User user = this.client.findByUsername("user");
        assertNotNull(user);
        assertEquals("user", user.getUsername());
    }

    @Test
    public void testFindbyUsername_notFound() {
        User user = this.client.findByUsername("noneExisting");
        assertNull(user);
    }
}
