package com.project.server;

import com.project.server.entity.Role;
import com.project.server.service.RoleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RoleServiceTest {

    @Autowired
    private RoleService roleService;

    @Test
    public void testCreateRole() {
        Role role = new Role();
        role.setName("ADMIN");

        if (!roleService.existsByName("ADMIN")) {
            Role createdRole = roleService.createRole(role);
            assertNotNull(createdRole);
            assertEquals("ADMIN", createdRole.getName());
        }
    }

    @Test
    public void testGetAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        assertNotNull(roles);
    }

    @Test
    public void testGetRoleByName() {
        // Спочатку створюємо роль, якщо її ще немає
        if (!roleService.existsByName("ADMIN")) {
            Role role = new Role();
            role.setName("ADMIN");
            roleService.createRole(role);
        }

        // Тепер перевіряємо, чи можемо отримати цю роль
        Optional<Role> foundRole = roleService.getRoleByName("ADMIN");
        assertTrue(foundRole.isPresent());
        assertEquals("ADMIN", foundRole.get().getName());
    }
}