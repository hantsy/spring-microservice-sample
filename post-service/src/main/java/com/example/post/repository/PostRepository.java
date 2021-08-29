/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.post.repository;

import java.util.Optional;

import com.example.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author hantsy
 */
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    Optional<Post> findBySlug(String slug);
    
}
