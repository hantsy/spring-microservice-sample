package com.example.user;

import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import com.example.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Optional;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.reset;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.webAppContextSetup;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@RunWith(SpringRunner.class)
@Slf4j
public abstract class BaseTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Before
    public void before() {
        Mockito.when(this.userRepository.findAll())
            .thenReturn(Arrays.asList(
                User.builder().id(1L).username("user").password("password").email("user@example.com").build(),
                User.builder().id(1L).username("admin").password("password").email("admin@example.com").build()
            ));

        Mockito.when(this.userRepository.findByUsername("user"))
            .thenReturn(
                Optional.of(User.builder().id(1L).username("user").password("password").email("user@example.com").build())
            );

        Mockito.when(this.userRepository.findByUsername("noneExisting")).thenReturn(Optional.empty());

        reset();
        webAppContextSetup(this.webApplicationContext);
    }
}