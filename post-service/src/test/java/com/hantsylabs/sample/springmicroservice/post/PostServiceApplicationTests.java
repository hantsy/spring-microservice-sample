package com.hantsylabs.sample.springmicroservice.post;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostServiceApplicationTests {

    @LocalServerPort
    private int port;

    private TestRestTemplate template = new TestRestTemplate();

    @Test
    public void getAllPosts() {
        ResponseEntity<String> response = template.getForEntity("http://localhost:{port}/posts", String.class, port);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void createPost() {
        ResponseEntity<Void> response = template.postForEntity("http://localhost:{port}/posts", Post.builder().title("my title").content("my content of my title").build(), Void.class, port);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

//    @Test
//    public void createPostWithMockUser() {
//        ResponseEntity<Void> response = template.postForEntity("http://localhost:{port}/posts", Post.builder().title("my title").content("my content of my title").build(), Void.class, port);
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//    }


}
