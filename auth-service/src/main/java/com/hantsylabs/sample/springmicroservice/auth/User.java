package com.hantsylabs.sample.springmicroservice.auth;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements  Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String password;

    private String email;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private List<String> roles = new ArrayList<>();

    private LocalDateTime createdDate;

}
