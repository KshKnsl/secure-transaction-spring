package com.securetransaction.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "accounts")
@CompoundIndex(def = "{'userId': 1, 'status': 1}")
public class Account {

    public enum AccountStatus {
        ACTIVE, FROZEN, CLOSED
    }

    @Id
    private String id;

    @Indexed
    private String userId;

    private AccountStatus status = AccountStatus.ACTIVE;
    private String currency = "INR";

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}