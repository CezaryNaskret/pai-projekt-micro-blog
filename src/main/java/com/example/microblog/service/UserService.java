package com.example.microblog.service;

import com.example.microblog.model.Comment;
import com.example.microblog.model.Post;
import com.example.microblog.model.User;
import com.example.microblog.repository.CommentRepository;
import com.example.microblog.repository.PostRepository;
import com.example.microblog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    @Autowired
    public UserService(UserRepository userRepository, PostRepository postRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public void insertUser(User user){
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public User findUserByLogin(String login) {
        Optional<User> userOpt = userRepository.findUserByLogin(login);
        return userOpt.orElseGet(User::new);
    }

    public Optional<User> findUserByName(String name) {
        return userRepository.findUserByDescriptiveName(name);
    }

    public void updateUserAndAvatar(MultipartFile file, User user) throws IOException {
        if(!file.isEmpty()) {
            user.setAvatar(Base64.getEncoder().encodeToString(file.getBytes()));
        }
        userRepository.save(user);
    }

    public void changeStatus(User user){
        if(user.getStatus() == 1){
            List<Post> posts = postRepository.findAllByAuthorOrderByDateDesc(user);
            List<Comment> comments = commentRepository.findAllByAuthorOrderByDateDesc(user);
            posts.stream().peek(p -> p.setStatus((short)0)).forEach(postRepository::save);
            comments.stream().peek(c -> c.setStatus((short)0)).forEach(commentRepository::save);
            user.setStatus((short)0);
            userRepository.save(user);
        }
        else if(user.getStatus() == 0){
            user.setStatus((short)1);
            userRepository.save(user);
        }
    }

}
