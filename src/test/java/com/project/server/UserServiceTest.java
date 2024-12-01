package com.project.server;

import com.project.server.entity.AppUser;
import com.project.server.entity.Role;
import com.project.server.repository.RoleRepository;
import com.project.server.repository.UserRepository;
import com.project.server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoadUserByUsername_UserExists() {
        AppUser user = new AppUser();
        user.setUsername("testuser");
        user.setPassword("testpass");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("testpass", userDetails.getPassword());
    }

    @Test
    public void testLoadUserByUsername_UserNotExists() {
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nonexistentuser"));
    }

    @Test
    public void testSaveUser() {
        AppUser user = new AppUser();
        user.setUsername("testuser");
        user.setPassword("rawpass");

        when(passwordEncoder.encode("rawpass")).thenReturn("encodedpass");
        when(userRepository.save(any(AppUser.class))).thenReturn(user);

        AppUser savedUser = userService.saveUser(user);

        verify(passwordEncoder).encode("rawpass");
        verify(userRepository).save(user);

        assertEquals("encodedpass", savedUser.getPassword());
    }

    @Test
    public void testAssignRoleToUser() {
        AppUser user = new AppUser();
        user.setUsername("testuser");

        Role role = new Role();
        role.setName("ROLE_USER");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));

        userService.assignRoleToUser("testuser", "ROLE_USER");

        verify(userRepository).save(user);
        assertTrue(user.getRoles().contains(role));
    }

    @Test
    public void testAssignRoleToUser_UserNotFound() {
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(new Role()));

        assertThrows(RuntimeException.class, () -> userService.assignRoleToUser("nonexistentuser", "ROLE_USER"));
    }

    @Test
    public void testAssignRoleToUser_RoleNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(new AppUser()));
        when(roleRepository.findByName("NON_EXISTENT_ROLE")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.assignRoleToUser("testuser", "NON_EXISTENT_ROLE"));
    }

    @Test
    public void testUserExistsByUsername_UserExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(new AppUser()));

        boolean exists = userService.userExistsByUsername("testuser");

        assertTrue(exists);
    }

    @Test
    public void testUserExistsByUsername_UserNotExists() {
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        boolean exists = userService.userExistsByUsername("nonexistentuser");

        assertFalse(exists);
    }
}