package com.artur.sprinboot.springboot.controller;

import com.artur.sprinboot.springboot.model.User;
import com.artur.sprinboot.springboot.service.PageAttributeService;
import com.artur.sprinboot.springboot.service.RoleService;
import com.artur.sprinboot.springboot.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final RoleService roleService;
    private final UserService userService;
    private final PageAttributeService pageAttributeService;

    @Autowired
    public AdminController(RoleService roleService, UserService userService, PageAttributeService pageAttributeService) {
        this.roleService = roleService;
        this.userService = userService;
        this.pageAttributeService = pageAttributeService;
    }

    @GetMapping("/users")
    public String getUsers(Principal principal, Model model) {
        pageAttributeService.addMainPageAttributes(principal, model);
        return "admin/user-list";
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute("newUser") @Valid User newUser,
                             BindingResult bindingResult, Model model, Principal principal) {
        boolean isUserCreated = userService.createUser(newUser, bindingResult);
        if (!isUserCreated) {
            model.addAttribute("hasErrors", true);
            pageAttributeService.addMainPageAttributes(principal, model);
            return "/admin/user-list";
        }
        this.userService.add(newUser);
        return "redirect:/admin/users";
    }

    @GetMapping("/user-update")
    public String showFormForUpdate(@RequestParam("id") long id,
                                    Model model) {
        User user = userService.readUser(id);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("listRoles", roleService.findAll());
            return "/admin/edit-user";
        } else {
            return "redirect:/admin/users";
        }
    }

    @PatchMapping("/users/edit")
    public String editUser(@ModelAttribute("updatingUser") @Valid User updatingUser,
                           BindingResult bindingResult, Model model, Principal principal) {
        boolean isUserUpdated = userService.updateUser(updatingUser, bindingResult);//  проверка обновленный ли пользователь
        if (!isUserUpdated) {
            pageAttributeService.addMainPageAttributes(principal, model);
            model.addAttribute("hasErrors", true);
            return "admin/user-list";
        }
        return "redirect:/admin/users";
    }

    @DeleteMapping("/users/delete")
    public String deleteUser(@ModelAttribute("deletingUser") User user) {
        Long id = user.getId();
        if (userService.readUser(id) != null) {
            userService.delete(id);
        }
        return "redirect:/admin/users";
    }
}