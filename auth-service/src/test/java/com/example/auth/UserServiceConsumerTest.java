package com.example.auth;

import com.example.auth.model.User;
import com.example.auth.service.UserServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureStubRunner(
    stubsMode = StubRunnerProperties.StubsMode.LOCAL,
    stubsPerConsumer = true,
    consumerName = "auth-service",
    ids = "com.example.contracts:user-service-producer:+:stubs:8001"
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
