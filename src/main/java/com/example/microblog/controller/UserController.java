package com.example.microblog.controller;

import com.example.microblog.model.User;
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

@Controller
public class UserController {
    UserService userService;
    @Autowired
    UserController (UserService userService){
        this.userService = userService;
    }

    // ------------------------ FOLLOW ------------------

    @PostMapping(path = "/follow")
    public String follow(
            @RequestParam String userName
    ) {
        User user = userService.findUserByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
        user.getFollow().add(userService.findUserByName(userName));
        userService.updateUser(user);
        return "redirect:/user=" + userName;
    }

    @PostMapping(path = "/unfollow")
    public String unfollow(
            @RequestParam String userName
    ) {
        User user = userService.findUserByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
        user.getFollow().remove(userService.findUserByName(userName));
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
}
