/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hantsylabs.sample.springmicroservice.post;

import javax.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author hantsy
 */
@Data
@Builder
@RequiredArgsConstructor
@Embeddable
public class UserId {
    
    private @NonNull String id;
    
}
