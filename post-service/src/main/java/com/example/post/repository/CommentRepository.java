/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.post.repository;

import com.example.post.model.Comment;
import com.example.post.model.PostSlug;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author hantsy
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    public Page<Comment> findByPost(PostSlug postSlug, Pageable page);
}
