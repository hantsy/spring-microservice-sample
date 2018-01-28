/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hantsylabs.sample.springmicroservice.post;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;


/**
 * @author hantsy
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Slf4j
public class IntegrationTests {

    @LocalServerPort
    int port;

    @Inject
    PostRepository posts;

    @Inject
    CommentRepository comments;

    String test_title = "test title";
    String test_content = "test content";
    String test_comment = "test_comment";
    String slug = "";

    @Before
    public void setup() {
        RestAssured.port = this.port;
        this.comments.deleteAllInBatch();
        this.posts.deleteAllInBatch();

        Post post = posts.save(
            Post.builder()
                .title(test_title)
                .content(test_content)
                .build()
        );
        log.debug("saved post:" + post);
        this.slug = post.getSlug();

        log.debug("print all posts:");
        posts.findAll().forEach(System.out::println);

        Comment comment = this.comments.save(
            Comment.builder()
                .content(test_comment)
                .post(new Slug(this.slug))
                .build()
        );
        log.debug("saved comment:" + comment);
    }

    @Test
    public void testGetNoneExistingPost_shouldReturn404() throws Exception {
        //@formatter:off
        when()
            .get("/posts/100000")
        .then()
            .statusCode(HttpStatus.SC_NOT_FOUND);
        //@formatter:on
    }


    @Test
    public void testGetAllPosts_shouldBeOK() throws Exception {
        //@formatter:off
        when()
            .get("/posts")
        .then()
            .body("content[0].title", is(test_title))
            .statusCode(HttpStatus.SC_OK);
        //@formatter:on
    }

    @Test
    public void testGetPostBySlug_shouldBeOK() throws Exception {
        //@formatter:off
        when()
            .get("/posts/"+ this.slug)
        .then()
            .body("title", is(test_title))
            .body("content", is(test_content))
            .statusCode(HttpStatus.SC_OK);
        //@formatter:on
    }

    @Test
    public void testGetCommentsOfPostBySlug_shouldBeOK() throws Exception {
        //@formatter:off
        when()
            .get("/posts/"+ this.slug+"/comments")
        .then()
            .body("content[0].content", is(test_comment))
            .statusCode(HttpStatus.SC_OK);
        //@formatter:on
    }

    //-------------- test with auth -----------------------
    @Test
    public void testCreateAPost_withoutUserAuth_shouldReturn401() throws Exception {
        PostForm _data = PostForm.builder().title(test_title).content(test_content).build();

        //@formatter:off
        given()
            //.auth().basic("user", "password")
            .body(_data)
            .contentType(ContentType.JSON)
        .when()
            .post("/posts")
        .then()
            .statusCode(HttpStatus.SC_UNAUTHORIZED);
        //@formatter:on
    }

    @Test
    public void testCreateAPost_withUserAuth_shouldBeOK() throws Exception {
        PostForm _data = PostForm.builder().title(test_title).content(test_content).build();

        //@formatter:off
        given()
            .auth().basic("user", "password")
            .body(_data)
            .contentType(ContentType.JSON)
        .when()
            .post("/posts")
        .then()
            .header("Location", containsString("/posts"))
            .statusCode(HttpStatus.SC_CREATED);
        //@formatter:on
    }


    @Test
    public void testUpdateAPost_withoutUserAuth_shouldReturn401() throws Exception {
        PostForm _data = PostForm.builder().title(test_title).content(test_content).build();

        //@formatter:off
        given()
            //.auth().basic("user", "password")
            .body(_data)
            .contentType(ContentType.JSON)
        .when()
            .put("/posts/"+ this.slug)
        .then()
            .statusCode(HttpStatus.SC_UNAUTHORIZED);
        //@formatter:on
    }

    @Test
    public void testUpdateAPost_withUserAuth_shouldBeOK() throws Exception {
        PostForm _data = PostForm.builder().title(test_title).content(test_content).build();

        //@formatter:off
        given()
            .auth().basic("user", "password")
            .body(_data)
            .contentType(ContentType.JSON)
        .when()
            .put("/posts/"+ this.slug)
        .then()
            .statusCode(HttpStatus.SC_NO_CONTENT);
        //@formatter:on
    }


    @Test
    public void testDeleteAPost_withoutAuth_shouldReturn401() throws Exception {

        //@formatter:off
        when()
            .delete("/posts/"+ this.slug)
        .then()
            .statusCode(HttpStatus.SC_UNAUTHORIZED);
        //@formatter:on
    }

    @Test
    public void testDeleteAPost_withUserAuth_shouldReturn403() throws Exception {

        //@formatter:off
        given()
            .auth().basic("user", "password")
        .when()
            .delete("/posts/"+ this.slug)
        .then()
            .statusCode(HttpStatus.SC_FORBIDDEN);
        //@formatter:on
    }


    @Test
    public void testDeleteAPost_withAdminAuth_shouldOK() throws Exception {

        //@formatter:off
        given()
            .auth().basic("admin", "password")
        .when()
            .delete("/posts/"+ this.slug)
        .then()
            .statusCode(HttpStatus.SC_NO_CONTENT);
        //@formatter:on
    }


    @Test
    public void testCreateACommentsOfPostBySlug_withoutAuth_shouldReturn401() throws Exception {
        CommentForm _data = CommentForm.builder().content(test_comment).build();

        //@formatter:off
        given()
            .body(_data)
            .contentType(ContentType.JSON)
        .when()
            .post("/posts/"+ this.slug+"/comments")
        .then()
            .statusCode(HttpStatus.SC_UNAUTHORIZED);
        //@formatter:on
    }

    @Test
    public void testCreateACommentsOfPostBySlug_withUserAuth_shouldBeOk() throws Exception {
        CommentForm _data = CommentForm.builder().content(test_comment).build();

        //@formatter:off
        given()
            .auth().basic("user", "password")
            .body(_data)
            .contentType(ContentType.JSON)
        .when()
            .post("/posts/"+ this.slug+"/comments")
        .then()
            .header("Location", containsString("/posts/"+ this.slug+"/comments"))
            .statusCode(HttpStatus.SC_CREATED);
        //@formatter:on
    }

    /*
    @TestComponent
    static class TestUserDetailsService implements UserDetailsService {
        private PasswordEncoder passwordEncoder;

        TestUserDetailsService(PasswordEncoder passwordEncoder) {
            this.passwordEncoder = passwordEncoder;
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            UserDetails user = User.withUsername("user")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();

            UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("password"))
                .roles("ADMIN")
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();

            log.debug("dummy user:" + user);
            log.debug("dummy admin:" + admin);

            if ("user".equals(username)) {
                return user;
            } else {
                return admin;
            }

        }
    }
    */


    @TestConfiguration
    @Slf4j
    static class TestSecurityConfig {

        @Inject
        PasswordEncoder passwordEncoder;

        @Bean
        UserDetailsService userDetailsService() {
            UserDetails user = User.withUsername("user")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();

            UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("password"))
                .roles("ADMIN")
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();

            log.debug("dummy user:" + user);
            log.debug("dummy admin:" + admin);

            return (username) -> {
                if ("user".equals(username)) {
                    return user;
                } else {
                    return admin;
                }
            };
        }
    }
}
