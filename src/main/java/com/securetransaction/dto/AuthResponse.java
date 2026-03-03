package com.securetransaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// Response body for both /register and /login.
@Data
@AllArgsConstructor
public class AuthResponse 
{
    private String id;
    private String email;
    private String name;
    private String token;
}
