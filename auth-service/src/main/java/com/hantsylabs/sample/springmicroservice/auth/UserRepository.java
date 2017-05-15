package com.hantsylabs.sample.springmicroservice.auth;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>{

    public Optional<User> findByUsername(String username);

    public Optional<User> findByEmail(String email);
}
