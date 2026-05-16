package com.example.demo;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index(Model model) {
        return "base";
    }

    @GetMapping("/search")
    public String search() {
        return "search";
    }

    @GetMapping("/home")
    public String home(Model model) {
        return "base";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "profile";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String email,
                               Model model) {
        if (userService.userExists(username)) {
            model.addAttribute("error", "Username already exists!");
            return "register";
        }
        userService.registerUser(username, password, email);
        model.addAttribute("success", "Account created successfully! You can now login.");
        return "register";
    }
}
