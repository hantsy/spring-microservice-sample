/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hantsylabs.sample.springmicroservice.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import java.util.Arrays;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.reset;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * @author hantsy
 */
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@RunWith(SpringRunner.class)
@Slf4j
public class ApplicationRestAssuredMockMvcTest {
    @Inject
    WebApplicationContext webApplicationContext;

    @MockBean
    PostRepository posts;

    @MockBean
    CommentRepository comments;

    @MockBean
    PostService postService;

    @Inject
    ObjectMapper objectMapper;


    @Before
    public void setup() {
        reset();
        config().getMockMvcConfig().automaticallyApplySpringSecurityMockMvcConfigurer();
        webAppContextSetup(this.webApplicationContext);
    }

    @Test
    public void testGetAllPosts() throws Exception {

        given(this.posts
            .findAll(any(Specification.class), any(PageRequest.class)))
            .willReturn(
                new PageImpl(
                    Arrays.asList(
                        Post.builder().title("my first post1").content("my content of my post1").build(),
                        Post.builder().title("my first post2").content("my content of my post2").build(),
                        Post.builder().title("my first post3").content("my content of my post3").build()
                    ),
                    PageRequest.of(0, 10),
                    3L
                )
            );

        //@formatter:off
        when()
            .get("/posts")
        .then()
            .body("content[0].title", is("my first post1"))
            .body("content[1].title", is("my first post2"))
            .body("content[2].title", is("my first post3"))
            .statusCode(HttpStatus.SC_OK);
        //@formatter:on

        verify(this.posts, times(1)).findAll(any(Specification.class), any(PageRequest.class));
        verifyNoMoreInteractions(this.posts);
    }

    @Test
    public void createPostWithoutAuthentication() throws Exception {
        Post _data = Post.builder().title("my first post").content("my content of my post").build();
        given(this.postService.createPost(any(PostForm.class)))
            .willReturn(_data);


        //@formatter:off
        RestAssuredMockMvc.given()
                .body(objectMapper.writeValueAsString(PostForm.builder().title("my first post").content("my content of my post").build()))
                .contentType(ContentType.JSON)
            .when()
                .post("/posts")
            .then()
                .assertThat()
            .statusCode(HttpStatus.SC_UNAUTHORIZED);
        //@formatter:on


        verify(this.postService, times(0)).createPost(any(PostForm.class));
        verifyNoMoreInteractions(this.postService);
    }

    @Test
    @WithMockUser
    public void createPostWithMockUser() throws Exception {
        Post _data = Post.builder().title("my first post").content("my content of my post").build();
        given(this.postService.createPost(any(PostForm.class)))
            .willReturn(_data);

        //@formatter:off
        RestAssuredMockMvc.given()
                .body(objectMapper.writeValueAsString(PostForm.builder().title("my first post").content("my content of my post").build()))
                .contentType(ContentType.JSON)
            .when()
                .post("/posts")
            .then()
                .assertThat()
                .header("Location", containsString("/posts"));
        //@formatter:on

        verify(this.postService, times(1)).createPost(any(PostForm.class));
    }


}
