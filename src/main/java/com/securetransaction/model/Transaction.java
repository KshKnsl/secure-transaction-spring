package com.securetransaction.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "transactions")
public class Transaction {

    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED, REVERSED
    }

    @Id
    private String id;

    @Indexed
    private String fromAccount;

    @Indexed
    private String toAccount;

    private BigDecimal amount;

    @Indexed(unique = true)
    private String idempotencyKey;

    private TransactionStatus status = TransactionStatus.PENDING;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
