package com.project.server;

import com.project.server.entity.Role;
import com.project.server.service.RoleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;


@SpringBootTest
@AutoConfigureMockMvc
public class RoleControllerTest {
    @Autowired
    private RoleService roleService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        roleService.deleteAllRoles();
    }

    @AfterEach
    public void tearDown() {
        roleService.deleteAllRoles();
    }

    private static final String ROLE_ADMIN = "ADMIN";
private static final String ROLE_ERROR = "Role name is required.";
private static final String ROLE_USER = "USER";

    @Test
    public void testCreateInvalidRole() throws Exception {
        String roleJson = "{\"name\": \"\"}";
        mockMvc.perform(post("/api/roles")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roleJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Role name is required."));
    }

    @Test
    public void testCreateRole() throws Exception {
        String roleJson = "{\"name\": \"" + ROLE_ADMIN + "\"}";
        mockMvc.perform(post("/api/roles")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roleJson))
                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.name").value(ROLE_ADMIN))
                .andExpect(jsonPath("$.message").value("Role created successfully"));
    }

    @Test
    public void testGetAllRoles() throws Exception {
        createRoleIfNotExists(ROLE_ADMIN);
        createRoleIfNotExists(ROLE_USER);

        mockMvc.perform(get("/api/roles")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(ROLE_ADMIN))
                .andExpect(jsonPath("$[1].name").value(ROLE_USER));
    }

    private void createRoleIfNotExists(String roleName) {
        if (!roleService.existsByName(roleName)) {
            roleService.createRole(new Role(roleName));
        }
    }

    @Test
    public void testCreateDuplicateRole() throws Exception {
        String roleJson = "{\"name\": \"" + ROLE_ADMIN + "\"}";
        roleService.createRole(new Role(ROLE_ADMIN));

        mockMvc.perform(post("/api/roles")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roleJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Role with name " + ROLE_ADMIN + " already exists."));
    }
    @Test
    public void testGetRoleByName() throws Exception {
        createRoleIfNotExists(ROLE_ADMIN);

        mockMvc.perform(get("/api/roles/" + ROLE_ADMIN)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(ROLE_ADMIN));
    }
}
