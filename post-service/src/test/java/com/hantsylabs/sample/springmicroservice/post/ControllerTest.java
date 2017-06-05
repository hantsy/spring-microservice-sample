/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hantsylabs.sample.springmicroservice.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author hantsy
 */
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@RunWith(SpringRunner.class)
@Slf4j
public class ControllerTest {
    
    @Inject
    WebApplicationContext wac;
    
    @MockBean
    PostRepository posts;
    
    @MockBean
    CommentRepository comments;
    
    @MockBean
    PostService postService;
    
    @Inject
    ObjectMapper objectMapper;
    
    @Inject
    FilterChainProxy springSecurityFilterChain;
    
    MockMvc mockMvc;
    
    @Before
    public void setup() {
        this.mockMvc = standaloneSetup(new PostController(postService, posts, comments))
            .setCustomArgumentResolvers(
                new PageableHandlerMethodArgumentResolver()
            )
//            .setViewResolvers(new ViewResolver() {
//                @Override
//                public org.springframework.web.servlet.View resolveViewName(String viewName, Locale locale) 
//                    throws Exception {
//                    return new MappingJackson2JsonView(objectMapper);
//                }
//            })
            
            .setMessageConverters(
                new MappingJackson2HttpMessageConverter(objectMapper)
            )

            .alwaysDo(print())
            .apply(springSecurity(springSecurityFilterChain))
            .build();
    }
    
    @Test
    //@Ignore
    public void testGetAllPosts() throws Exception {
        given(this.posts
            .findAll(PostSpecifications.filterByKeywordAndStatus("my", null), PageRequest.of(0, 10)))
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
