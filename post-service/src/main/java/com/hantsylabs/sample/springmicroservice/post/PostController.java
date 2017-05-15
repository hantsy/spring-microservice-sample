package com.hantsylabs.sample.springmicroservice.post;

import java.net.URI;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/posts")
public class PostController {

    private static final Logger log = LoggerFactory.getLogger(PostController.class);

    @Inject
    private PostService postService;

    @Inject
    private PostRepository postRepository;

    @Inject
    private CommentRepository commentRepository;

    public PostController() {
    }

    @GetMapping()
//    @ApiOperation(nickname = "get-all-posts", value = "Get all posts")
//    @ApiResponses(
//        value = {
//            @ApiResponse(code = 200, message = "return all posts by page")
//        }
//    )
    public ResponseEntity<Page<Post>> getAllPosts(
        @RequestParam(value = "q", required = false) String keyword, //
        @RequestParam(value = "status", required = false) Post.Status status, //
        @PageableDefault(page = 0, size = 10, sort = "createdDate", direction = Direction.DESC) Pageable page) {

        log.debug("get all posts of q@" + keyword + ", status @" + status + ", page@" + page);

        Page<Post> posts = this.postRepository.findAll(PostSpecifications.filterByKeywordAndStatus(keyword, status), page);

        log.debug("get posts size @" + posts.getTotalElements());

        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Post> getPost(@PathVariable("id") Long id) {

        log.debug("get postsinfo by id @" + id);

        Post post = this.postRepository.findById(id).orElseThrow(
            () -> {
                return new PostNotFoundException(id);
            }
        );

        log.debug("get post @" + post);

        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Void> createPost(@RequestBody @Valid PostForm post, HttpServletRequest request) {

        log.debug("create a new post@" + post);

        Post saved = this.postService.createPost(post);

        log.debug("saved post id is @" + saved.getId());
        URI loacationHeader = ServletUriComponentsBuilder
            .fromContextPath(request)
            .path("/posts/{id}")
            .buildAndExpand(saved.getId())
            .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(loacationHeader);

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> updatePost(@PathVariable("id") Long id, @RequestBody @Valid PostForm form) {

        log.debug("update post by id @" + id + ", form content@" + form);

        this.postService.updatePost(id, form);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletePostById(@PathVariable("id") Long id) {

        log.debug("delete post by id @" + id);

        this.postService.deletePostById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/{id}/comments")
    public ResponseEntity<Page<Comment>> getCommentsOfPost(
        @PathVariable("id") Long id,
        @PageableDefault(page = 0, size = 10, sort = "createdDate", direction = Direction.DESC) Pageable page) {

        log.debug("get comments of post@" + id + ", page@" + page);

        Page<Comment> commentsOfPost = this.commentRepository.findByPost(new PostId(id), page);

        log.debug("get post comment size @" + commentsOfPost.getTotalElements());

        return new ResponseEntity<>(commentsOfPost, HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/comments")
    public ResponseEntity<Void> createComment(
        @PathVariable("id") @NotNull Long id, @RequestBody CommentForm comment) {

        log.debug("new comment of post@" + id + ", comment" + comment);

        Comment _comment = Comment.builder()
            .post(new PostId(id))
            .content(comment.getContent())
            .build();

        Comment saved = this.commentRepository.save(_comment);

        log.debug("saved comment @" + saved.getId());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
