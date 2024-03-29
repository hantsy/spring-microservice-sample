package com.example.user.web;

import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import com.example.user.repository.UserSpecifications;
import com.example.user.service.EmailWasTakenException;
import com.example.user.service.UserNotFoundException;
import com.example.user.service.UserService;
import com.example.user.service.UsernameWasTakenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;

import static org.springframework.http.ResponseEntity.*;

/**
 * @author Hantsy Bai<hantsy@gmail.com>
 */
@RequestMapping(value = "/users")
@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserRepository userRepository;

    @GetMapping(value = "exists")
    public ResponseEntity exists(
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "email", required = false) String email
    ) {

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(email)) {
            return badRequest().build();
        }

        if (StringUtils.hasText(username) && this.userRepository.findByUsername(username).isPresent()) {
            throw new UsernameWasTakenException(username);
        }

        if (StringUtils.hasText(email) && this.userRepository.findByEmail(email).isPresent()) {
            throw new EmailWasTakenException(email);
        }

        return ok().build();
    }

    @GetMapping(value = "")
    public ResponseEntity getAll(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "active", required = false) String active,
            @PageableDefault(page = 0, size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable page) {

        Page<User> users = this.userRepository.findAll(UserSpecifications.byKeyword(q, role, active), page);

        return ok(users);
    }

    @PostMapping(value = {""})
    public ResponseEntity<Void> createUser(
            @RequestBody @Valid UserForm form,
            HttpServletRequest req) {
        log.debug("user data@" + form);

        User saved = this.userService.createUser(form);

        URI createdUri = ServletUriComponentsBuilder
                .fromContextPath(req)
                .path("/users/{username}")
                .buildAndExpand(saved.getId()).toUri();

        return created(createdUri).build();
    }

    @GetMapping(value = "/{username}")
    public ResponseEntity getById(@PathVariable("username") String username) {
        User _user = this.userRepository.findByUsername(username).orElseThrow(
                () -> {
                    return new UserNotFoundException(username);
                }
        );

        return ok(_user);
    }

    @PostMapping(value = "/{username}/lock")
    public ResponseEntity lockUser(@PathVariable("username") String username) {

        log.debug("locking user:" + username);

        this.userService.lock(username);

        return noContent().build();
    }

    @DeleteMapping(value = "/{username}/lock")
    public ResponseEntity unlockUser(@PathVariable("username") String username) {

        log.debug("unlocking user:" + username);

        this.userService.unlock(username);

        return noContent().build();
    }

    @DeleteMapping(value = "/{username}")
    public ResponseEntity deleteById(@PathVariable("username") String username) {
        User _user = this.userRepository.findByUsername(username).orElseThrow(
                () -> {
                    return new UserNotFoundException(username);
                }
        );

        this.userRepository.delete(_user);

        return noContent().build();
    }

}
