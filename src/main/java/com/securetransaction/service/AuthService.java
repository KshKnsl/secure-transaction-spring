package com.securetransaction.service;

import com.securetransaction.dto.AuthResponse;
import com.securetransaction.dto.LoginRequest;
import com.securetransaction.dto.RegisterRequest;
import com.securetransaction.model.TokenBlacklist;
import com.securetransaction.model.User;
import com.securetransaction.repository.TokenBlacklistRepository;
import com.securetransaction.repository.UserRepository;
import com.securetransaction.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail().toLowerCase().trim())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "User already exists with email.");
        }
        User user = new User();
        user.setEmail(req.getEmail().toLowerCase().trim());
        user.setName(req.getName());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getId());
        emailService.sendRegistrationEmail(user.getEmail(), user.getName());
        return new AuthResponse(user.getId(), user.getEmail(), user.getName(), token);
    }

    public AuthResponse login(LoginRequest req) 
    {
        User user = userRepository.findByEmail(req.getEmail().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Email or password is INVALID"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Email or password is INVALID");
        }

        String token = jwtUtil.generateToken(user.getId());
        return new AuthResponse(user.getId(), user.getEmail(), user.getName(), token);
    }

    public void logout(String token) {
        if (token == null || token.isBlank()) return;

        TokenBlacklist entry = new TokenBlacklist();
        entry.setToken(token);
        tokenBlacklistRepository.save(entry);
    }
}
