package com.project.server;

import com.project.server.controller.AuthController;
import com.project.server.entity.AppUser;
import com.project.server.util.JwtTokenUtil;
import com.project.server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUserSuccess() {
        AppUser user = new AppUser();
        user.setUsername("testuser");
        user.setPassword("testpass");

        when(userService.userExistsByUsername("testuser")).thenReturn(false);
        when(userService.saveUser(any(AppUser.class))).thenReturn(user);

        ResponseEntity<String> response = authController.registerUser(user);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully", response.getBody());
    }


    @Test
    public void testRegisterUserAlreadyExists() {
        AppUser user = new AppUser();
        user.setUsername("testuser");
        user.setPassword("testpass");

        when(userService.userExistsByUsername("testuser")).thenReturn(true);

        ResponseEntity<String> response = authController.registerUser(user);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("User already exists", response.getBody());
    }

    @Test
    public void testLoginUserSuccess() {
        AppUser user = new AppUser();
        user.setUsername("testuser");
        user.setPassword("testpass");

        when(authenticationManager.authenticate(any())).thenReturn(null); // update according to real return type
        when(jwtTokenUtil.generateToken(any())).thenReturn("testtoken");

        ResponseEntity<String> response = authController.loginUser(user);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("testtoken", response.getBody());
    }
}
