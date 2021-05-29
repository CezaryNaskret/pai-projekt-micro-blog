package com.example.microblog.service;

import com.example.microblog.model.User;
import com.example.microblog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void insertUser(User user){
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    public User findUserByLogin(String login) {
        Optional<User> userOpt = userRepository.findUserByLogin(login);
        return userOpt.orElseGet(User::new);
    }

    @Transactional
    public User findUserByName(String name) {
        return userRepository.findUserByDescriptiveName(name);
    }

    @Transactional
    public void updateUserAndAvatar(MultipartFile file, User user) throws IOException {
        if(!file.isEmpty()) {
            user.setAvatar(Base64.getEncoder().encodeToString(file.getBytes()));
        }
        userRepository.save(user);
    }

}
