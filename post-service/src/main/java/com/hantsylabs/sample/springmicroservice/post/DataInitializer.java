/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hantsylabs.sample.springmicroservice.post;

import javax.inject.Inject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 *
 * @author hantsy
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Inject
    private PostRepository postRepository;

    @Inject
    private CommentRepository commentRepository;

    @Override
    public void run(String... strings) throws Exception {
        Post _post = Post.builder()
            .title("My first post")
            .content("content of my first post")
            .build();

        Post saved = this.postRepository.save(_post);

        this.commentRepository.save(Comment.builder().content("comment 1 of my first post").post(new PostId(saved.getId())).build());
        this.commentRepository.save(Comment.builder().content("comment 2 of my first post").post(new PostId(saved.getId())).build());
    }

}
