package com.securetransaction.model;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "ledgers")
public class LedgerEntry {

    public enum LedgerType {
        CREDIT, DEBIT
    }

    @Id
    private String id;

    @Indexed
    private String accountId;

    private BigDecimal amount;

    @Indexed
    private String transactionId;

    private LedgerType type;
}
