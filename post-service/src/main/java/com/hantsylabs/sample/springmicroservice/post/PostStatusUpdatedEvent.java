/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hantsylabs.sample.springmicroservice.post;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author hantsy
 */
@Data()
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public class PostStatusUpdatedEvent extends PostEvent {

    private @NonNull PostId post;
    private @NonNull Post.Status originalStatus;
    private @NonNull Post.Status newStatus;
}
