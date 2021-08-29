package com.example.user;

import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactFolder;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.spring.SpringRestPactRunner;
import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import com.example.user.service.UserService;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Optional;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRestPactRunner.class)
@Provider("user-service")
//@PactBroker(
//    protocol = "https",
//    host = "${pactBrokerHost}",
//    port = "8443",
//    authentication = @PactBrokerAuth(
//        username = "${pactBrokerUser}",
//        password = "${pactBrokerPassword}"
//    )
//)
@PactFolder("pacts")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserServiceProviderPactTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @TestTarget
    public final Target target = new HttpTarget(8001);

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

    @State("should return user if existed")
    public void shouldReturnUserIfExisted() {
        //@formatter:off
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/users/user")
        .then()
            .body("username", is("user"))
            .body("password", is("password"))
            .body("email", is("user@example.com"))
            .statusCode(HttpStatus.SC_OK);
        //@formatter:on
    }

    @State("should return error 404 if not existed")
    public void shouldReturnError404IfExisted() {
        //@formatter:off
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/users/noneExisting")
        .then()
            .body("entity", is("USER"))
            .body("id", is("noneExisting"))
            .body("code", is("not_found"))
            .body("message", is("User:noneExisting was not found"))
            .statusCode(HttpStatus.SC_NOT_FOUND);
        //@formatter:on
    }

}