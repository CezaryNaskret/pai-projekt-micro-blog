package com.example.microblog.controller;

import com.example.microblog.model.Comment;
import com.example.microblog.model.Post;
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

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PostControllerTest {
    private MockMvc mockMvc;

    @Mock
    private PostController postController;
    PostService postService;
    UserService userService;
    CommentService commentService;

    @Before
    public void setup() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/view/");
        viewResolver.setSuffix(".jsp");

        postService = mock(PostService.class);
        userService = mock(UserService.class);
        commentService = mock(CommentService.class);

        postController = new PostController(postService, userService, commentService);
        mockMvc = MockMvcBuilders.standaloneSetup(postController)
                .setViewResolvers(viewResolver)
                .build();
    }

    //--------------- POST ------------------

    @Test
    public void createPostTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/createPost"))
                .andExpect(status().isOk())
                .andExpect(view().name("createPost"));
    }

    @Test
    public void createPostSaveTest() throws Exception {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(MockMvcRequestBuilders
                    .post("/createPost")
                        .flashAttr("post", new Post()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/myWall"));
    }

    @Test
    public void createPostCancelTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                    .post("/createPost")
                        .param("cancel", String.valueOf(true)))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/myWall"));
    }

    @Test
    public void createPostDeleteTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                    .post("/createPost")
                        .param("delete", String.valueOf(true)))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/myWall"));
    }

    //------------------- POST PAGE ----------------------

    @Test
    public void getPostTest() throws Exception {
        Mockito.when(postService.getPost(1)).thenReturn(java.util.Optional.of(new Post()));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/post/{postId}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("postPage"));
    }

    //-------------------- COMMENT -----------------------------

    @Test
    public void createCommentSaveTest() throws Exception{
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(MockMvcRequestBuilders
                    .post("/createComment")
                        .param("postId", String.valueOf(1))
                        .flashAttr("comment", new Comment()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/post/1"));
    }

    @Test
    public void createCommentDeleteTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                    .post("/createComment")
                        .param("delete", String.valueOf(true))
                        .param("postId", String.valueOf(1)))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/post/1"));
    }

    // ---------------------- SEARCH ENGINE -------------

    @Test
    public void searchTest() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                    .post("/search")
                        .param("sentence", "userInput"))
                .andExpect(status().isOk())
                .andExpect(view().name("searchResult"));
    }
}
