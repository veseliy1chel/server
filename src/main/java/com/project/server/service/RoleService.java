package com.project.server.service;

import com.project.server.entity.Role;
import com.project.server.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // Метод для створення нової ролі
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    // Метод для отримання всіх ролей
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // Метод для отримання ролі за назвою
    public Optional<Role> getRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    // Метод для видалення ролі
    public void deleteRole(Long roleId) {
        roleRepository.deleteById(roleId);
    }

    // Метод для перевірки існування ролі за ID
    public boolean existsById(Long roleId) {
        return roleRepository.existsById(roleId);
    }

    // Метод для перевірки існування ролі за ім'ям
    public boolean existsByName(String roleName) {
        return roleRepository.existsByName(roleName);
    }
}
