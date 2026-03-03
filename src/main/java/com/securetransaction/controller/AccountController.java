package com.securetransaction.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.securetransaction.model.Account;
import com.securetransaction.security.UserPrincipal;
import com.securetransaction.service.AccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController 
{
    private final AccountService accountService;

    // POST /api/accounts – create a new account for the logged-in user
    @PostMapping
    public ResponseEntity<Account> createAccount(
            @AuthenticationPrincipal UserPrincipal principal) 
    {
        Account account = accountService.createAccount(principal.getUser().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    // GET /api/accounts – get all accounts of the logged-in user
    @GetMapping
    public ResponseEntity<List<Account>> getUserAccounts(
            @AuthenticationPrincipal UserPrincipal principal) 
    {
        List<Account> accounts = accountService.getUserAccounts(principal.getUser().getId());
        return ResponseEntity.ok(accounts);
    }

    // GET /api/accounts/balance/:accountId
    @GetMapping("/balance/{accountId}")
    public ResponseEntity<Map<String, Object>> getBalance(
            @PathVariable String accountId,
            @AuthenticationPrincipal UserPrincipal principal) {

        BigDecimal balance = accountService.getAccountBalance(
                accountId, principal.getUser().getId());

        return ResponseEntity.ok(Map.of(
                "accountId", accountId,
                "balance",   balance
        ));
    }
}
