package com.securetransaction.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "tokenBlacklists")
public class TokenBlacklist {

    @Id
    private String id;

    @Indexed(unique = true)
    private String token;

    @CreatedDate
    @Indexed(expireAfter = "P3D")
    private LocalDateTime createdAt;
}