package com.example.microblog.controller;

import com.example.microblog.model.User;
import com.example.microblog.service.CommentService;
import com.example.microblog.service.PostService;
import com.example.microblog.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {
    private MockMvc mockMvc;

    @Mock
    private UserController userController;
    UserService userService;
    PostService postService;
    CommentService commentService;
    Authentication authentication;

    @Before
    public void setup() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/view/");
        viewResolver.setSuffix(".jsp");

        authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(authentication.getName()).thenReturn("anyString()");

        userService = mock(UserService.class);
        postService = mock(PostService.class);
        commentService = mock(CommentService.class);
        userController = new UserController(userService, postService, commentService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setViewResolvers(viewResolver)
                .build();
    }

    // ------------------------ FOLLOW ------------------

    @Test
    public void followTest() throws Exception {
        Mockito.when(userService.findUserByLogin(anyString())).thenReturn(new User());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/follow")
                    .param("userName", "test@gmail.com"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/user=test@gmail.com"));
    }

    @Test
    public void unfollowTest() throws Exception {
        Mockito.when(userService.findUserByLogin(anyString())).thenReturn(new User());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/unfollow")
                .param("userName", "test@gmail.com"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/user=test@gmail.com"));
    }

    //------------------------ EDIT USER ---------------------

    @Test
    public void editUserTest() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                .get("/editUser")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("editUser"));
    }

    @Test
    public void editUserSaveTest() throws Exception{
        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "file",
                        MediaType.IMAGE_JPEG_VALUE,
                        "bytest from image".getBytes(StandardCharsets.UTF_8));

        MockMultipartFile user =
                new MockMultipartFile(
                        "user",
                        "user",
                        MediaType.APPLICATION_JSON_VALUE,
                        new ObjectMapper().writeValueAsString(new User()).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                multipart("/editUser")
                        .file(file)
                        .file(user))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/myWall"));
    }

    @Test
    public void editUserCancelTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/editUser")
                .param("cancel", String.valueOf(true)))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/myWall"));
    }
}
