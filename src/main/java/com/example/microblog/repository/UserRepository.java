package com.example.microblog.repository;

import com.example.microblog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserByLogin(String login);
    Optional <User> findUserByDescriptiveName(String descriptiveName);
}
