package com.securetransaction.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.securetransaction.dto.AuthResponse;
import com.securetransaction.dto.LoginRequest;
import com.securetransaction.dto.RegisterRequest;
import com.securetransaction.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest req,
            HttpServletResponse response) 
    {
        AuthResponse auth = authService.register(req);
        addTokenCookie(response, auth.getToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(auth);
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest req,
            HttpServletResponse response) 
    {
        AuthResponse auth = authService.login(req);
        addTokenCookie(response, auth.getToken());
        return ResponseEntity.ok(auth);
    }

    // POST /api/auth/logout
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            HttpServletRequest request,
            HttpServletResponse response) 
    {

        String token = extractToken(request);
        authService.logout(token);
        Cookie clear = new Cookie("token", null);
        clear.setMaxAge(0);
        clear.setPath("/");
        response.addCookie(clear);
        return ResponseEntity.ok(Map.of("message", "User logged out successfully"));
    }

    private void addTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);   
        cookie.setPath("/");
        cookie.setMaxAge(259200);//3 days in secs
        response.addCookie(cookie);
    }

    private String extractToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null)
            for (Cookie c : cookies)
                if ("token".equals(c.getName())) return c.getValue();
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) return header.substring(7);
        return null;
    }
}