package com.example.microblog.repository;

import com.example.microblog.model.Comment;
import com.example.microblog.model.Post;
import com.example.microblog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByAuthorOrderByDateDesc(User author);
    List<Comment> findAllByPost(Post post);
}
