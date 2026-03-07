package com.securetransaction.service;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.securetransaction.dto.CreateTransactionRequest;
import com.securetransaction.model.Account;
import com.securetransaction.model.Account.AccountStatus;
import com.securetransaction.model.LedgerEntry;
import com.securetransaction.model.LedgerEntry.LedgerType;
import com.securetransaction.model.Transaction;
import com.securetransaction.model.Transaction.TransactionStatus;
import com.securetransaction.model.User;
import com.securetransaction.repository.AccountRepository;
import com.securetransaction.repository.LedgerRepository;
import com.securetransaction.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final LedgerRepository ledgerRepository;
    private final AccountService accountService;
    private final EmailService emailService;

    @Transactional
    public Transaction createTransaction(CreateTransactionRequest req, User user) {

        Account fromAccount = accountRepository.findById(req.getFromAccount())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Invalid fromAccount or toAccount"));
        Account toAccount = accountRepository.findById(req.getToAccount())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Invalid fromAccount or toAccount"));

        transactionRepository.findByIdempotencyKey(req.getIdempotencyKey())
                .ifPresent(existing -> {
                    switch (existing.getStatus()) {
                        case COMPLETED -> throw new ResponseStatusException(
                                HttpStatus.OK, "Transaction already processed");
                        case PENDING -> throw new ResponseStatusException(
                                HttpStatus.OK, "Transaction is still processing");
                        case FAILED -> throw new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Transaction processing failed, please retry");
                        case REVERSED -> throw new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Transaction was reversed, please retry");
                    }
                });

        if (fromAccount.getStatus() != AccountStatus.ACTIVE
                || toAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Both fromAccount and toAccount must be ACTIVE to process transaction");
        }

        BigDecimal balance = accountService.calculateBalance(req.getFromAccount());
        if (balance.compareTo(req.getAmount()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Insufficient balance. Current balance is " + balance
                    + ". Requested amount is " + req.getAmount());
        }

        Transaction transaction = new Transaction();
        transaction.setFromAccount(req.getFromAccount());
        transaction.setToAccount(req.getToAccount());
        transaction.setAmount(req.getAmount());
        transaction.setIdempotencyKey(req.getIdempotencyKey());
        transaction.setStatus(TransactionStatus.PENDING);
        transactionRepository.save(transaction);

        LedgerEntry debit = new LedgerEntry();
        debit.setAccountId(req.getFromAccount());
        debit.setAmount(req.getAmount());
        debit.setTransactionId(transaction.getId());
        debit.setType(LedgerType.DEBIT);
        ledgerRepository.save(debit);

        LedgerEntry credit = new LedgerEntry();
        credit.setAccountId(req.getToAccount());
        credit.setAmount(req.getAmount());
        credit.setTransactionId(transaction.getId());
        credit.setType(LedgerType.CREDIT);
        ledgerRepository.save(credit);

        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);

        emailService.sendTransactionEmail(
                user.getEmail(), user.getName(),
                req.getAmount().toString(), req.getToAccount());

        return transaction;
    }

}
