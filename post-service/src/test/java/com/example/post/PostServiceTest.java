package com.example.post;

import com.example.post.model.Post;
import com.example.post.repository.PostRepository;
import com.example.post.service.PostService;
import com.example.post.web.PostForm;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@Slf4j
public class PostServiceTest {

    @MockBean
    private PostRepository posts;

    @Autowired
    private PostService postService;

    public PostServiceTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void createPost() {
        final String TITLE = "test post title";
        final String CONTENT = "test post content";

        final PostForm input = PostForm.builder().title(TITLE).content(CONTENT).build();
        Post expected = Post.builder().title(TITLE).content(CONTENT).build();
        expected.setId(1L);

        given(posts.save(Post.builder().title(input.getTitle()).content(input.getContent()).build()))
                .willReturn(expected);

        Post returned = postService.createPost(input);

        assertTrue(returned == expected);

        verify(posts, times(1)).save(any(Post.class));
        verifyNoMoreInteractions(posts);
    }

    @TestConfiguration
    @Import(PostService.class)
    static class TestConfig {

    }

}
