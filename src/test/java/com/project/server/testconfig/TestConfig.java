package com.project.server.testconfig;

import com.project.server.service.UserService;
import com.project.server.util.JwtTokenUtil;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

    @Bean
    public JwtTokenUtil jwtTokenUtil() {
        return new JwtTokenUtil();
    }
    @Bean
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }
}