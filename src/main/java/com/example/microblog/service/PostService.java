package com.example.microblog.service;

import com.example.microblog.model.Post;
import com.example.microblog.model.User;
import com.example.microblog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private final PostRepository postRepository;
    @Autowired
    public PostService(PostRepository postRepository){
        this.postRepository = postRepository;
    }

    public List<Post> getAllUsersPosts(User author){
        return postRepository.findAllByAuthorOrderByDateDesc(author);
    }

    public Optional<Post> getPost(Integer id) {
        return postRepository.findById(id);
    }

    public void setPost(Post post) {
        postRepository.save(post);
    }

    public void deletePost(Post post) {
        postRepository.delete(post);
    }

    public List<Post> getPosts(String title) {
        return postRepository.findByTitleContaining(title);
    }

    public List<Post> getPosts(){
        return postRepository.findAll();
    }
}
