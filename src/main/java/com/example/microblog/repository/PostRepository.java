package com.example.microblog.repository;

import com.example.microblog.model.Post;
import com.example.microblog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findAllByAuthorOrderByDateDesc(User author);
    List<Post> findByTitleContaining(String title);
}
