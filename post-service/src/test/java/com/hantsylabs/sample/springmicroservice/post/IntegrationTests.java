/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hantsylabs.sample.springmicroservice.post;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static io.restassured.RestAssured.when;
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

    String test_title = "test title";
    String test_content = "test content";
    String slug = "";

    @Before
    public void setup() {
        RestAssured.port = this.port;
        this.posts.deleteAllInBatch();
        Post post = posts.save(
            Post.builder()
                .title(test_title)
                .content(test_content)
                .build()
        );
        log.debug("saving post:" + post);
        this.slug = post.getSlug();

        log.debug("print all posts:");
        posts.findAll().forEach(System.out::println);
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


}
