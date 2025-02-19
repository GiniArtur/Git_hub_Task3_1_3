package com.artur.sprinboot.springboot.service;

import com.artur.sprinboot.springboot.model.Role;
import com.artur.sprinboot.springboot.model.User;
import com.artur.sprinboot.springboot.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role save(Role role) {
        return null;
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {
    }

    @Override
    public Optional<Role> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public List<String> getUserRoles(User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .map(role -> role.split("_")[1])
                .toList();
    }
}
