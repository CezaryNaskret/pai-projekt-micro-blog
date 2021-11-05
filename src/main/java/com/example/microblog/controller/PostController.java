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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    @Autowired
    public PostController(PostService postService, UserService userService, CommentService commentService) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
    }

    //--------------- POST ------------------

    @GetMapping("/createPost")
    public String createPost(@RequestParam(required = false) Integer postId, Model model) {
        Post post;
        if(postId != null) {
            Optional<Post> postOpt = postService.getPost(postId);
            post = postOpt.orElseGet(Post::new);
        }
        else {
            post = new Post();
            post.setStatus((short) 1);
        }
        model.addAttribute("post", post);
        return "createPost";
    }

    @PostMapping(path = "/createPost")
    public String createPostSave(@ModelAttribute @Valid Post post, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "createPost";
        }
        try {
            User author = userService.findUserByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
            post.setAuthor(author);
            postService.setPost(post);
        } catch (HttpStatusCodeException e) {
            bindingResult.rejectValue(null, String.valueOf(e.getStatusCode().value()),
                    e.getStatusCode().getReasonPhrase());
            return "createPost";
        }
        return "redirect:/myWall";
    }

    @PostMapping(params = "cancel", path = "/createPost")
    public String createPostCancel() {
        return "redirect:/myWall";
    }

    @PostMapping(params = "delete", path = "/createPost")
    public String createPostDelete(@ModelAttribute Post post) {
        postService.deletePost(post);
        return "redirect:/myWall";
    }

    //------------------- POST PAGE ----------------------

    @GetMapping("/post/{postId}")
    public String getPost(
            @PathVariable("postId") Integer postId,
            @RequestParam(required = false) Integer commentId,
            Model model,
            Authentication auth
    ) {
        Optional<Post> postOpt = postService.getPost(postId);
        if(postOpt.isPresent() && (postOpt.get().getStatus() == 1 || (auth != null && auth.getAuthorities().toString().equals("[ROLE_ADMIN]")))){
            model.addAttribute("post", postOpt.get());
            model.addAttribute("isAuth", auth);

            List<Comment> comments = commentService.getComments(postOpt.get());
            if(!auth.getAuthorities().toString().equals("[ROLE_ADMIN]")){
                comments = comments.stream().filter(com -> com.getStatus() == 1).collect(Collectors.toList());
            }
            model.addAttribute("comments", comments);

            Optional<Comment> commentOpt = commentService.getComment(commentId);
            if (commentOpt.isPresent()) {
                model.addAttribute("comment", commentOpt.get());
            } else {
                Comment comment = new Comment();
                model.addAttribute("comment", comment);
            }
            return "postPage";
        }
        else{
            return "redirect:/myWall";
        }
    }

    //-------------------- COMMENT -----------------------------

    @PostMapping(path = "/createComment")
    public String createCommentSave(
            @ModelAttribute @Valid Comment comment,
            BindingResult bindingResult,
            @RequestParam Integer postId
    ) {
        if (bindingResult.hasErrors()) {
            return "redirect:/post/" + postId; // error
        }
        try {
            User author = userService.findUserByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
            comment.setAuthor(author);
            postService.getPost(postId).ifPresent(comment::setPost);
            commentService.setComment(comment);
        } catch (HttpStatusCodeException e) {
            bindingResult.rejectValue(null, String.valueOf(e.getStatusCode().value()),
                    e.getStatusCode().getReasonPhrase());
            return "redirect:/post/" + postId; // error
        }
        return "redirect:/post/" + postId;
    }

    @PostMapping(params = "delete", path = "/createComment")
    public String createCommentDelete(
            @ModelAttribute Comment comment,
            @RequestParam Integer postId
    ) {
        commentService.deleteComment(comment.getCommentId());
        return "redirect:/post/" + postId;
    }

    // ---------------------- SEARCH ENGINE -------------

    @PostMapping(path = "/search")
    public String search(@RequestParam String sentence, Model model, Authentication auth) {
        List<Post> posts = postService.getPosts(sentence);
        model.addAttribute("posts", posts);
        model.addAttribute("noResults", posts.isEmpty());
        model.addAttribute("isAuth", auth);
        return "searchResult";
    }

    // ---------------------- FOR ADMIN -------------

    @PostMapping(path = "/changePostStatus")
    public String changePostStatus(@RequestParam(required = true) Integer postId, Model model){
        Optional<Post> postOpt = postService.getPost(postId);
        if(postOpt.isPresent()){
            Post post = postOpt.get();
            if(post.getStatus() == 1){
                post.setStatus((short) 0);
            }
            else if(post.getStatus() == 0){
                post.setStatus((short) 1);
            }
            postService.setPost(post);
        }
        return "redirect:/post/" + postId;
    }

    @PostMapping(path = "/changeCommentStatus")
    public String changeCommentStatus(@RequestParam(required = true) Integer commentId, Model model){
        Optional<Comment> commentOpt = commentService.getComment(commentId);
        if(commentOpt.isPresent()){
            Comment comment = commentOpt.get();
            if(comment.getStatus() == 1){
                comment.setStatus((short) 0);
            }
            else if(comment.getStatus() == 0){
                comment.setStatus((short) 1);
            }
            commentService.setComment(comment);
            return "redirect:/post/" + comment.getPost().getPostId();
        }
        return "redirect:/myWall";
    }
}

