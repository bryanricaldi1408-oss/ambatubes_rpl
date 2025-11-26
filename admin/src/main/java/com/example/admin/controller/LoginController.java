package com.example.admin.controller;

import com.example.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/")
    public String showLoginPage() {
        return "login-logout";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password) {
        if (adminService.validateAdmin(email, password)) {
            return "redirect:/adminHome";  // Redirects to AdminController
        } else {
            return "redirect:/?error=true";
        }
    }
}