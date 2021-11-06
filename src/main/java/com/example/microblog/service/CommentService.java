package com.example.microblog.service;

import com.example.microblog.model.Comment;
import com.example.microblog.model.Post;
import com.example.microblog.model.User;
import com.example.microblog.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    @Autowired
    public CommentService(CommentRepository commentRepository){
        this.commentRepository = commentRepository;
    }

    public List<Comment> getAllUsersComments(User author){
        return commentRepository.findAllByAuthorOrderByDateDesc(author);
    }

    public List<Comment> getComments(Post post){
        return commentRepository.findAllByPost(post);
    }

    public Optional<Comment> getComment(Integer commentId) {
        if(commentId == null) return Optional.empty();
        return commentRepository.findById(commentId);
    }

    public void setComment(Comment comment) {
        commentRepository.save(comment);
    }

    public void deleteComment(Integer commentId) {
        commentRepository.deleteById(commentId);
    }
}
