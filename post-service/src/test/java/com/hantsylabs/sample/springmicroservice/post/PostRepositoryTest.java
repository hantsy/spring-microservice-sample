/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hantsylabs.sample.springmicroservice.post;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author hantsy
 */
@RunWith(SpringRunner.class)
@DataJpaTest()
@Slf4j
//@Ignore//too much config in PostServiceApplication, the config should be extracted in standalone files.
public class PostRepositoryTest {

    @Inject
    private TestEntityManager em;

    @Inject
    PostRepository posts;

    @Before
    public void setup() {
        assertNotNull("posts is not null", posts);
        posts.deleteAllInBatch();
        em.persist(Post.builder().title("test post 1").content("test content of test post 1").build());
    }

    @Test
    public void testGetAllPosts() {
        assertTrue(1 == posts.findAll().size());
        Post post = posts.findAll().get(0);
        assertTrue("test-post-1".equals(post.getSlug()));
    }

}
