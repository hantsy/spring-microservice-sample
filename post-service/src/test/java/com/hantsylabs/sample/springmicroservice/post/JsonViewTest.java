/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hantsylabs.sample.springmicroservice.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author hantsy
 */
@RunWith(SpringRunner.class)
@JsonTest
@Slf4j
public class JsonViewTest {

    @Inject
    private JacksonTester<Post> json;

    @Test
    public void serializeJson() throws IOException {
        Post details = Post.builder().title("test title").content("test content").build();
        
        assertThat(this.json.write(details)).extractingJsonPathStringValue("@.title")
            .isEqualTo("test title");
        assertThat(this.json.write(details)).extractingJsonPathStringValue("@.content")
            .isEqualTo("test content");

    }

    @Test
    public void serializeJsonWithView() throws IOException {
        Post details = Post.builder().title("test title").content("test content").build();
        
        ObjectMapper mapper = new ObjectMapper();
        String result = mapper
            .writerWithView(View.Summary.class)
            .writeValueAsString(details);
        log.debug("result:::" + result);
        
        assertTrue(result.contains("test title"));
        assertTrue(!result.contains("test content"));

    }
}
