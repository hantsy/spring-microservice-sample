/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.post;

import com.example.post.model.Post;
import com.example.post.repository.CommentRepository;
import com.example.post.repository.PostRepository;
import com.example.post.service.PostService;
import com.example.post.web.PostForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author hantsy
 */
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@RunWith(SpringRunner.class)
@Slf4j
public class ApplicationMockMvcTest {

    @Autowired
    WebApplicationContext wac;

    @MockBean
    PostRepository posts;

    @MockBean
    CommentRepository comments;

    @MockBean
    PostService postService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    FilterChainProxy springSecurityFilterChain;

    MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac)
                .alwaysDo(print())
                .apply(springSecurity(springSecurityFilterChain))
                .build();
    }

    @Test
    //@Ignore
    public void testGetAllPosts() throws Exception {
        given(this.posts
                .findAll(any(Specification.class), any(Pageable.class)))
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

        MvcResult result = this.mockMvc
                .perform(
                        get("/posts?q=my")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].title", hasItem("my first post1")))
                .andReturn();

        log.debug("mvc result:::" + result.getResponse().getContentAsString());
        verify(this.posts, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verifyNoMoreInteractions(this.posts);
    }

    @Test
    public void createPostWithoutAuthentication() throws Exception {
        Post _data = Post.builder().title("my first post").content("my content of my post").build();
        given(this.postService.createPost(any(PostForm.class)))
                .willReturn(_data);

        MvcResult result = this.mockMvc
                .perform(
                        post("/posts")
                                .content(objectMapper.writeValueAsString(PostForm.builder().title("my first post").content("my content of my post").build()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized())
                .andReturn();

        log.debug("mvc result::" + result.getResponse().getContentAsString());

        verify(this.postService, times(0)).createPost(any(PostForm.class));
        verifyNoMoreInteractions(this.postService);
    }

    @Test
    @WithMockUser
    public void createPostWithMockUser() throws Exception {
        Post _data = Post.builder().title("my first post").content("my content of my post").build();
        given(this.postService.createPost(any(PostForm.class)))
                .willReturn(_data);

        MvcResult result = this.mockMvc
                .perform(
                        post("/posts")
                                .content(objectMapper.writeValueAsString(PostForm.builder().title("my first post").content("my content of my post").build()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, containsString("/posts")))
                .andReturn();

        log.debug("mvc result::" + result.getResponse().getContentAsString());

        verify(this.postService, times(1)).createPost(any(PostForm.class));
    }

}
