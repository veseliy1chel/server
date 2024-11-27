package com.project.server.controller;

import com.project.server.entity.Role;
import com.project.server.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
@Validated
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // Створення нової ролі
    @PostMapping
    public ResponseEntity<Map<String, String>> createRole(@RequestBody Role role) {
        if (role.getName() == null || role.getName().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Role name is required."));
        }

        if (roleService.existsByName(role.getName())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Collections.singletonMap("error", "Role with name " + role.getName() + " already exists."));
        }

        roleService.createRole(role);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Role created successfully");
        response.put("name", role.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Отримання всіх ролей
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        logger.info("Fetched all roles");
        return ResponseEntity.ok(roles);
    }

    // Отримання ролі за назвою
    @GetMapping("/{roleName}")
    public ResponseEntity<Role> getRoleByName(@PathVariable String roleName) {
        return roleService.getRoleByName(roleName)
                .map(role -> {
                    logger.info("Role found with name: {}", roleName);
                    return ResponseEntity.ok(role);
                })
                .orElseGet(() -> {
                    logger.warn("Role not found with name: {}", roleName);
                    return ResponseEntity.notFound().build();
                });
    }

    // Оновлення ролі
    @PutMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Role> updateRole(@PathVariable Long roleId, @RequestBody @Valid Role roleDetails) {
        return roleService.updateRole(roleId, roleDetails)
                .map(updatedRole -> {
                    logger.info("Role updated with id: {}", roleId);
                    return ResponseEntity.ok(updatedRole);
                })
                .orElseGet(() -> {
                    logger.warn("Role not found with id: {}", roleId);
                    return ResponseEntity.notFound().build();
                });
    }

    // Видалення ролі
    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRole(@PathVariable Long roleId) {
        if (!roleService.existsById(roleId)) {
            logger.warn("Role not found with id: {}", roleId);
            return ResponseEntity.notFound().build();
        }
        roleService.deleteRole(roleId);
        logger.info("Role deleted with id: {}", roleId);
        return ResponseEntity.noContent().build();
    }
}