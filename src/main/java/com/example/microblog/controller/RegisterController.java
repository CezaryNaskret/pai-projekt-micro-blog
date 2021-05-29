package com.example.microblog.controller;

import com.example.microblog.model.User;
import com.example.microblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Properties;

@Controller
public class RegisterController {
    private final UserService userService;
    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    // ------------------ LOGIN AND REGISTER ---------------

    @GetMapping("/login")
    public String login(Model model, Authentication auth){
        // model - komunikacja między warstwani BE - FE
        // model.addAttribute(nazwa obiektu w FE, obiekt BE);
        // auth == is null to niezalogowano : zalogowano
        model.addAttribute("isAuth", auth);
        return "loginPage";     // widok z resources/templates i bez rozszerzenia html
    }

    @GetMapping("/registration")
    public String registration(Model model, Authentication auth){
        model.addAttribute("user", new User());
        model.addAttribute("isAuth", auth);
        return "registerPage";       // przekierowanie na adres metodą GET
    }

    @PostMapping("/registrationPost")
    public String registration(@ModelAttribute User user, @RequestParam String password2) throws IOException {
        if(!user.getPassword().equals(password2)){
            return "redirect:/registration";
        }

        user.setRegisterDate(LocalDate.now());
        user.setStatus((short) 1);

        // add default avatar
        File avatar = new File("src\\main\\resources\\static\\assets\\img\\avatar.png");
        MultipartFile multipartFile = new MockMultipartFile("avatar.png", new FileInputStream(avatar));
        user.setAvatar(Base64.getEncoder().encodeToString(multipartFile.getBytes()));

        user.getFollow().add(user); // obserwuje sam siebe, żeby zawsze stronie głównej widzieć również swoje posty
        userService.insertUser(user);
        return "redirect:/login";       // przekierowanie na adres metodą GET
    }

    // ------------ RESET PASSWORD ---------------------

    @GetMapping("/sendEmail")
    public String sendEmail() {
        return "sendEmail";
    }

    @PostMapping(path = "/sendEmailPost")
    public String sendEmail(@RequestParam String email, RedirectAttributes redirectAttributes) {
        User user = userService.findUserByLogin(email);
        System.out.println("test" + user);
        if(user.getUserId() != null){
            String password = getRandomString(); // generujemy hasło
            System.out.println("New Password: " + password); // wyświetlamy je
            //sendMessage(email, password); // wysyłame email z nowym hasłem
            user.setPassword(password);
            userService.insertUser(user); // zapisujemy nowe hasło
            redirectAttributes.addAttribute("email", email);
            return "redirect:/changePassword";
        }
        return "redirect:/sendEmail";
    }

    @GetMapping("/changePassword")
    public String changePassword(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "forgotPassword";
    }

    @PostMapping(path = "/changePasswordPost")
    public String changePasswordSave(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String newPassword2,
            @RequestParam String email,
            RedirectAttributes redirectAttributes
    ) {
        if(!newPassword.equals(newPassword2)){
            redirectAttributes.addAttribute("email", email);
            return "redirect:/changePassword";
        }

        User user = userService.findUserByLogin(email);
        if(user.getUserId() != null){
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if(encoder.matches(oldPassword, user.getPassword())){
                user.setPassword(newPassword);
                userService.insertUser(user);
                return "redirect:/login";
            }
        }
        return "forgotPassword";
    }

    private void sendMessage(String email, String password) {
        String from = "cezary.naskret@gmail.com";
        String host = "smtp.gmail.com";
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("cezary.naskret@gmail.com", "haslo1234%^&*");
            }
        });
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("Zmiana hasła - Rocket");
            message.setText("Twoje nowe hasło to " + password);
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    static String getRandomString() {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }

        return sb.toString();
    }
}
