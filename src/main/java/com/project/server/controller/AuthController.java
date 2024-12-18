package com.project.server.controller;

import com.project.server.entity.AppUser;
import com.project.server.service.UserService;
import com.project.server.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

@PostMapping("/register")
public ResponseEntity<String> registerUser(@RequestBody AppUser user) {
    if (userService.userExistsByUsername(user.getUsername())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
    }
    userService.saveUser(user);
    return ResponseEntity.status(HttpStatus.OK).body("User registered successfully");
}

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody AppUser user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            String token = jwtTokenUtil.generateToken(authentication);
            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Invalid credentials");
        }
    }
}
