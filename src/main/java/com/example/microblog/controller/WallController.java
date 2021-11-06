package com.example.microblog.controller;

import com.example.microblog.model.Post;
import com.example.microblog.model.User;
import com.example.microblog.service.CommentService;
import com.example.microblog.service.PostService;
import com.example.microblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class WallController {

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    @Autowired
    public WallController(PostService postService, UserService userService, CommentService commentService) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
    }

    @GetMapping("/myWall")
    public String home(Model model, Authentication auth){
        User user = userService.findUserByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("user", user);

        List<Post> posts;
        if(user.getRole().equals("ROLE_USER")){
            posts = user.getFollow()
                    .stream()
                    .map(postService::getAllUsersPosts)
                    .flatMap(Collection::stream)
                    .filter(post -> post.getStatus() == 1)
                    .sorted(Comparator.comparing(Post::getDate))
                    .collect(Collectors.toList());
        }
        else if(user.getRole().equals("ROLE_ADMIN")){
            posts = postService.getPosts();
        }
        else {
            posts = new ArrayList<>();
        }
        model.addAttribute("posts", posts);

        List<Integer> comments = posts.stream()
                .map(commentService::getComments)
                .map(List::size)
                .collect(Collectors.toList());
        model.addAttribute("comments", comments);

        model.addAttribute("isAuth", auth);
        return "home";
    }

    @GetMapping("/user={userName}")
    public String getUser(@PathVariable("userName") String userName, Model model, Authentication auth) {
        Optional<User> userOpt = userService.findUserByName(userName);
        model.addAttribute("isAuth", auth);
        if(userOpt.isPresent() && (userOpt.get().getStatus() == 1 || auth != null && auth.getAuthorities().toString().equals("[ROLE_ADMIN]"))) {
            User user = userOpt.get();
            model.addAttribute("posts", postService.getAllUsersPosts(user)
                    .stream()
                    .filter(post -> auth.getAuthorities().toString().equals("[ROLE_ADMIN]") || post.getStatus() == 1 )
                    .collect(Collectors.toList()));
            model.addAttribute("user", user);
            if(auth != null){
                User visitor = userService.findUserByLogin(auth.getName());
                model.addAttribute("follow", visitor.getFollow().contains(user));
            }
        }
        else {
            return "redirect:/myWall";
        }
        return "wall";
    }
}
