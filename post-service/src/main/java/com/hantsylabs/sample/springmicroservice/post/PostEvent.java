/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hantsylabs.sample.springmicroservice.post;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

/**
 *
 * @author hantsy
 */
@Getter
public class PostEvent implements Serializable {

    private UUID id = UUID.randomUUID();
    private LocalDateTime createdDate = LocalDateTime.now();
    
}
