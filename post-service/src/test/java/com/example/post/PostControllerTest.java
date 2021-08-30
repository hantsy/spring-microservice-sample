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
import com.example.post.web.PostController;
import com.example.post.web.PostForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author hantsy
 */
@RunWith(SpringRunner.class)
@Slf4j
@WebMvcTest(controllers = PostController.class, secure = false)
public class PostControllerTest {

    @MockBean
    PostRepository posts;

    @MockBean
    CommentRepository comments;

    @MockBean
    PostService postService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void createPost() throws Exception {
        Post _data = Post.builder().slug("test-my-first-post").title("my first post").content("my content of my post").build();
        given(this.postService.createPost(any(PostForm.class)))
                .willReturn(_data);

        this.mockMvc
                .perform(
                        post("/posts")
                                .content(objectMapper.writeValueAsString(PostForm.builder().title("my first post").content("my content of my post").build()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(this.postService, times(1)).createPost(any(PostForm.class));
        verifyNoMoreInteractions(this.postService);
    }

}
