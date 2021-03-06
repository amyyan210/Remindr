package com.sweetjandy.remindr.controllers;

import com.sweetjandy.remindr.models.User;
import com.sweetjandy.remindr.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class AuthenticationController {
    private final UsersRepository usersRepository;

    @Autowired
    public AuthenticationController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @GetMapping("/login")
    public String showLoginForm(Model viewModel) {

        viewModel.addAttribute("user", new User());
        return "users/login";
    }

}