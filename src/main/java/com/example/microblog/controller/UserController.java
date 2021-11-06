package com.example.microblog.controller;

import com.example.microblog.model.Comment;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class UserController {
    UserService userService;
    PostService postService;
    CommentService commentService;
    @Autowired
    UserController (UserService userService, PostService postService, CommentService commentService){
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;
    }

    // ------------------------ FOLLOW ------------------

    @PostMapping(path = "/follow")
    public String follow(
            @RequestParam String userName
    ) {
        User user = userService.findUserByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
        userService.findUserByName(userName).ifPresent(u -> user.getFollow().add(u));
        userService.updateUser(user);
        return "redirect:/user=" + userName;
    }

    @PostMapping(path = "/unfollow")
    public String unfollow(
            @RequestParam String userName
    ) {
        User user = userService.findUserByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
        userService.findUserByName(userName).ifPresent(u -> user.getFollow().remove(u));
        userService.updateUser(user);
        return "redirect:/user=" + userName;
    }

    //------------------------ EDIT USER ---------------------

    @GetMapping("/editUser")
    public String editUser(Model model, Authentication auth) {
        User user = userService.findUserByLogin(auth.getName());
        model.addAttribute("user", user);
        return "editUser";
    }

    @PostMapping(path = "/editUser")
    public String editUserSave(
            @RequestParam("file") MultipartFile file,
            @ModelAttribute @Valid User user,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "editUser";
        }
        try {
            userService.updateUserAndAvatar(file, user);
//            userService.updateUser(user);
        } catch (HttpStatusCodeException | IOException e) {
            e.printStackTrace();
            return "editUser";
        }
        return "redirect:/myWall";
    }

    @PostMapping(params = "cancel", path = "/editUser")
    public String editUserCancel() {
        return "redirect:/myWall";
    }

    // ---------------------- FOR ADMIN -------------

    @PostMapping(path = "/changeUserStatus")
    public String changeStatus(@RequestParam String userName){
        Optional<User> userOpt = userService.findUserByName(userName);
        if(userOpt.isPresent()){
            User user = userOpt.get();
            if(user.getStatus() == 1){
                List<Post> posts = postService.getAllUsersPosts(user);
                List<Comment> comments = commentService.getAllUsersComments(user);
                posts.stream().peek(p -> p.setStatus((short)0)).forEach(p -> postService.setPost(p));
                comments.stream().peek(c -> c.setStatus((short)0)).forEach(c -> commentService.setComment(c));
                user.setStatus((short) 0);
            }
            else if(user.getStatus() == 0){
                user.setStatus((short) 1);
            }
            userService.updateUser(user);
        }
        return "redirect:/user=" + userName;
    }
}
