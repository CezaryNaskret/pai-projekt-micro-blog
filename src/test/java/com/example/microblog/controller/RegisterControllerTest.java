package com.example.microblog.controller;

import com.example.microblog.model.User;
import com.example.microblog.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RegisterControllerTest {
    private MockMvc mockMvc;

    @Mock
    private RegisterController registerController;
    UserService userService;

    @Before
    public void setup() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/view/");
        viewResolver.setSuffix(".jsp");

        userService = mock(UserService.class);
        registerController = new RegisterController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(registerController)
                .setViewResolvers(viewResolver)
                .build();
    }

    // ------------------ LOGIN AND REGISTER ---------------

    @Test
    public void loginTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginPage"));
    }

    @Test
    public void registrationTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/registration"))
                .andExpect(status().isOk())
                .andExpect(view().name("registerPage"));
    }

    @Test
    public void registration2Test() throws Exception {
        User user = new User();
        user.setPassword("new_password");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/registrationPost")
                    .flashAttr("user", user)
                    .param("password2", "new_password"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));
    }

    // ------------ RESET PASSWORD ---------------------

    @Test
    public void sendEmailTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/sendEmail"))
                .andExpect(status().isOk())
                .andExpect(view().name("sendEmail"));
    }

    @Test
    public void sendEmail2Test() throws Exception {
        User user = new User();
        user.setUserId(1);
        Mockito.when(userService.findUserByLogin("test@gmail.com")).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders
                    .post("/sendEmailPost")
                        .param("email", "test@gmail.com"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/changePassword?email=test%40gmail.com"));
    }

    @Test
    public void changePasswordTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/changePassword")
                    .param("email", "test@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("forgotPassword"));
    }

    @Test
    public void changePassword2Test() throws Exception {
        User user = new User();
        user.setUserId(1);
        user.setPassword(new BCryptPasswordEncoder().encode("old_pass"));
        Mockito.when(userService.findUserByLogin("test@gmail.com")).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders
                    .post("/changePasswordPost")
                        .param("oldPassword", "old_pass")
                        .param("newPassword", "new_pass")
                        .param("newPassword2","new_pass")
                        .param("email", "test@gmail.com"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));
    }


}
