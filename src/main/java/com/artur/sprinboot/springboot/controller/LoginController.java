package com.artur.sprinboot.springboot.controller;

import com.artur.sprinboot.springboot.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String welcomeToPage() {    //welcome to... - это повелительная форма глагола "to welcome"
        return "login";
    }

    public void info(String s, User user) {
    }
}
