package com.artur.sprinboot.springboot.service;

import com.artur.sprinboot.springboot.model.Role;
import com.artur.sprinboot.springboot.model.User;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Role save(Role role);

    List<Role> findAll();

    Optional<Role> findById(Long id);

    void deleteById(Long id);

    Optional<Role> findByName(String name);

    List<String> getUserRoles(User user);
}

