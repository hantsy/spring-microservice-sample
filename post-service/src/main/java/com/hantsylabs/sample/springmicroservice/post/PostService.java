/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hantsylabs.sample.springmicroservice.post;

import com.hantsylabs.sample.springmicroservice.post.PostRepository;
import com.hantsylabs.sample.springmicroservice.post.Post;
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
    
//    public PostService(PostRepository posts){
//        this.postRepository = posts;
//    }

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
            ()-> {
                return new PostNotFoundException(slug);
            }
        );
        
        _post.setTitle(form.getTitle());
        _post.setContent(form.getContent());
        
       Post saved =  this.postRepository.save(_post);
       
       return saved;
    }

    public void deletePost(String slug) {
        this.postRepository.delete(this.postRepository.findBySlug(slug).orElseThrow(
            () -> {
                return new PostNotFoundException(slug);
            }
        ));
    }

}
