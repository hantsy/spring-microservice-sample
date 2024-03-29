/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.post.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 *
 * @author hantsy
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class PostSlug implements Serializable {
    
    private @NonNull String slug;
    
}
