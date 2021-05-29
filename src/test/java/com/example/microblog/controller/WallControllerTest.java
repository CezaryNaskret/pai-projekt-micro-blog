package com.example.microblog.controller;

import com.example.microblog.model.User;
import com.example.microblog.service.CommentService;
import com.example.microblog.service.PostService;
import com.example.microblog.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class WallControllerTest {
    private MockMvc mockMvc;

    @Mock
    private WallController wallController;
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

        wallController = new WallController(postService, userService, commentService);
        mockMvc = MockMvcBuilders.standaloneSetup(wallController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    public void homeTest() throws Exception {
        Mockito.when(userService.findUserByLogin(anyString())).thenReturn(new User());

        mockMvc.perform(MockMvcRequestBuilders
                .get("/myWall")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    public void getUserTest() throws Exception {
        Mockito.when(userService.findUserByName("testUser")).thenReturn(new User());
        Mockito.when(userService.findUserByLogin(anyString())).thenReturn(new User());

        mockMvc.perform(MockMvcRequestBuilders
                .get("/user={userName}", "testUser")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("wall"));
    }
}
