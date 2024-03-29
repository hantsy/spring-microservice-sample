/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.post.service;

import com.example.post.model.Post;
import com.example.post.repository.PostRepository;
import com.example.post.web.PostForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hantsy
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public Post createPost(PostForm form) {
        Post _post = Post.builder()
                .title(form.getTitle())
                .content(form.getContent())
                .build();

        Post saved = this.postRepository.save(_post);

        return saved;
    }

    public Post updatePost(String slug, PostForm form) {
        Post _post = this.postRepository.findBySlug(slug).orElseThrow(
                () -> new PostNotFoundException(slug)
        );

        _post.setTitle(form.getTitle());
        _post.setContent(form.getContent());

        Post saved = this.postRepository.save(_post);

        return saved;
    }

    public void deletePost(String slug) {
        this.postRepository.delete(this.postRepository.findBySlug(slug).orElseThrow(
                () -> new PostNotFoundException(slug)
        ));
    }

}
