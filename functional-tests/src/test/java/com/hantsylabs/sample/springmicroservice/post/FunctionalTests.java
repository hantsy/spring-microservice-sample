package com.hantsylabs.sample.springmicroservice.post;

import com.hantsylabs.sample.springmicroservice.test.AuthenticationRequest;
import com.hantsylabs.sample.springmicroservice.test.PostForm;
import lombok.extern.slf4j.Slf4j;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestClientTest
@Slf4j
public class FunctionalTests {

    private final String AUTH_URL = "http://localhost/auth";
    private final String POST_URL = "http://localhost/posts";

    private final TestRestTemplate template = new TestRestTemplate();

    @Test
    public void getAllPosts() {
        ResponseEntity<String> response = template.getForEntity(POST_URL, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void createPostWithoutAuthentication() {
        ResponseEntity<Void> response = template.postForEntity(POST_URL, PostForm.builder().title("my title").content("my content of my title").build(), Void.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void createPostWithAuthentication() {

        //signin with user/test123
        ResponseEntity<String> authResponse = template.postForEntity(AUTH_URL + "/signin", AuthenticationRequest.builder().username("user").password("test123").build(), String.class);
        assertEquals(HttpStatus.OK, authResponse.getStatusCode());
        assertNotNull(authResponse.getHeaders().getFirst("X-Auth-Token"));
        log.debug("\nsignin response:\n {}", authResponse.getBody());

        //create a new post
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", authResponse.getHeaders().getFirst("X-Auth-Token"));
        //headers.add("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);

        ResponseEntity<Void> response = template.exchange(POST_URL,
            HttpMethod.POST,
            new HttpEntity<>(PostForm.builder().title("my title").content("my content of my title").build(), headers),
            Void.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
        
        //verify the created post
        ResponseEntity<String> postresponse = template.exchange(response.getHeaders().getLocation(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            String.class
        );

        assertEquals(HttpStatus.OK, postresponse.getStatusCode());
        assertNotNull(postresponse.getBody().contains("my title"));
    }

}
