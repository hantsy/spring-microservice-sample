/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.post.model;

import com.example.post.model.json.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.slugify.Slugify;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotEmpty;

import lombok.*;


/**
 *
 * @author hantsy
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public
class Post extends AuditableEntity {

    @JsonView(View.Summary.class)
    @NotEmpty
    private String title;
    
    @NotEmpty
    private String slug;

    @JsonView(View.Public.class)
    @NotEmpty
    private String content;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @JsonView(View.Summary.class)
    private PostStatus status = PostStatus.DRAFT;

    @PrePersist
    public void slugify(){
        this.slug = new Slugify().slugify(this.title);
    }

}
