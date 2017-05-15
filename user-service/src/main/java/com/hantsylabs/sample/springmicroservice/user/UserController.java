package com.hantsylabs.sample.springmicroservice.user;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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

/**
 *
 * @author Hantsy Bai<hantsy@gmail.com>
 */
@RequestMapping(value = "/users")
@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Inject
    private UserService userService;

    @Inject
    private UserRepository userRepository;

    public UserController() {
    }

    @GetMapping(value = "exists")
    public ResponseEntity exists(
        @RequestParam(name = "username", required = false) String username,
        @RequestParam(name = "email", required = false) String email
    ) {

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(email)) {
            return ResponseEntity.badRequest().build();
        }

        if (StringUtils.hasText(username) && this.userRepository.findByUsername(username).isPresent()) {
            throw new UsernameWasTakenException(username);
        }

        if (StringUtils.hasText(email) && this.userRepository.findByEmail(email).isPresent()) {
            throw new EmailWasTakenException(email);
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "")
    public ResponseEntity getAll(
        @RequestParam("q") String q,
        @PageableDefault(page = 0, size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable page) {
        Page<User> users = this.userRepository.findAll(UserSpecifications.byKeyword(q), page);

        return ResponseEntity.ok(users);
    }

    @PostMapping(value = {""})
    public ResponseEntity<Void> createUser(
        @RequestBody @Valid UserForm form,
        HttpServletRequest req) {
        log.debug("user data@" + form);

        User saved = this.userService.createUser(form);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(
            ServletUriComponentsBuilder
                .fromContextPath(req)
                .path("/users/{id}")
                .buildAndExpand(saved.getId()).toUri()
        );

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity getById(@PathVariable("id") Long id) {
        User _user = this.userRepository.findById(id).orElseThrow(
            () -> {
                return new UserNotFoundException(id);
            }
        );

        return ResponseEntity.ok(_user);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity updateUser(@PathVariable("id") Long id,
        @RequestBody @Valid UserForm form,
        HttpServletRequest req) {

        log.debug("updating user form:" + form);

        User saved = this.userService.updateUser(id, form);

        log.debug("updated user:" + saved);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteById(@PathVariable("id") Long id) {
        User _user = this.userRepository.findById(id).orElseThrow(
            () -> {
                return new UserNotFoundException(id);
            }
        );

        this.userRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}
