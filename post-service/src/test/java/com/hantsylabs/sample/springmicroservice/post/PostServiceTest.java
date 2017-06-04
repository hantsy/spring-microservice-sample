package com.hantsylabs.sample.springmicroservice.post;


import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@RunWith(SpringRunner.class)
@Slf4j
public class PostServiceTest {

    @MockBean
    private PostRepository posts;

//    @MockBean
//    private CommentRepository comments;
    
    @Inject
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
        Post post = Post.builder().title("test post title").content("test post content@").build();
        post.setId(1L);

        given(posts.save(any(Post.class)))
            .willReturn(post);

        Post savedPost = postService.createPost(PostForm.builder().title("test").content("test").build());

        assertTrue(savedPost == post);

        verify(posts, times(1)).save(any(Post.class));
        verifyNoMoreInteractions(posts);
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public PostService postService() {
            return new PostService();
        }
    }

}
