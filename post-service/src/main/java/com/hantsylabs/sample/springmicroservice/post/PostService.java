/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hantsylabs.sample.springmicroservice.post;

import java.util.Optional;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author hantsy
 */
@Service
@Transactional
public class PostService {

    @Inject
    private PostRepository postRepository;

    public Post createPost(PostForm form) {
        Post _post = Post.builder()
            .title(form.getTitle())
            .content(form.getContent())
            .build();
        
        Post saved = this.postRepository.save(_post);
        
        return saved;
    }

    public Post updatePost(Long id, PostForm form) {
        Post _post = this.postRepository.findById(id).orElseThrow(
            ()-> {
                return new PostNotFoundException(id);
            }
        );
        
        _post.setTitle(form.getTitle());
        _post.setContent(form.getContent());
        
       Post saved =  this.postRepository.save(_post);
       
       return saved;
    }

    public void deletePostById(Long id) {
        this.postRepository.deleteById(id);
    }

}
