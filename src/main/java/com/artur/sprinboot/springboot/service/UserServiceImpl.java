package com.artur.sprinboot.springboot.service;

import com.artur.sprinboot.springboot.controller.LoginController;
import com.artur.sprinboot.springboot.model.Role;
import com.artur.sprinboot.springboot.model.User;
import com.artur.sprinboot.springboot.repository.RoleRepository;
import com.artur.sprinboot.springboot.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginController loginController;

    @Autowired
    public UserServiceImpl(RoleRepository roleRepository, UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder, LoginController loginController) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginController = loginController;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public boolean add(User user) {
        User userFromDB = userRepository.findByEmail(user.getEmail());
        if (userFromDB != null) {
            return false;
        }
        List<Role> userRoles = user.getRoles().stream()
                .map(role -> roleRepository.findByName(role.getName()).orElseThrow(() ->
                        new RuntimeException("Role not found: " + role.getName())))
                .collect(Collectors.toList());
        user.setRoles(userRoles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public User readUser(long id) {                                                 //Находим по ID пользователя
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User with id = " + id + " not exist"));
    }

    @Override
    @Transactional
    public void delete(long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Such user not exists"));
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public boolean createUser(User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return false;
        }
        User userFromDB = userRepository.findByEmail(user.getEmail());
        if (userFromDB != null) {
            bindingResult.rejectValue("email", "email.exists");
            return false;
        }
        // Шифруем пароль перед сохранением
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return false;
    }

    @Override
    @Transactional
    public boolean updateUser(User user, BindingResult bindingResult) {
        User userFromDB = null;
        userFromDB = userRepository.findByEmail(user.getEmail());
        if (userFromDB != null && (userFromDB.getId() != user.getId())) {
            bindingResult.rejectValue("email", "email.exists", "This email already exists");
        }
        if (bindingResult.hasErrors()) {
            return false;
        }
        if (userFromDB.getPassword() != null && !userFromDB.getPassword().equals(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        loginController.info("Updating user: {}", user);  // Лог перед сохранением
        userRepository.save(user);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public User getAuthenticatedUser(String email) {
        return userRepository.findByEmail(email);
    }
}