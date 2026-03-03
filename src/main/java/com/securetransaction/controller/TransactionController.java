package com.securetransaction.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.securetransaction.dto.CreateTransactionRequest;
import com.securetransaction.model.Transaction;
import com.securetransaction.security.UserPrincipal;
import com.securetransaction.service.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController 
{
    private final TransactionService transactionService;
    // POST /api/transactions – create a new transfer between two accounts
    @PostMapping
    public ResponseEntity<Map<String, Object>> createTransaction(
            @Valid @RequestBody CreateTransactionRequest req,
            @AuthenticationPrincipal UserPrincipal principal) 
    {
        Transaction transaction = transactionService.createTransaction(req, principal.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Transaction completed successfully",
                "transaction", transaction
        ));
    }

}
