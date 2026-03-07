package com.securetransaction.service;

import com.securetransaction.model.Account;
import com.securetransaction.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final MongoTemplate mongoTemplate;

    public Account createAccount(String userId) {
        Account account = new Account();
        account.setUserId(userId);
        return accountRepository.save(account);
    }

    public List<Account> getUserAccounts(String userId) {
        return accountRepository.findByUserId(userId);
    }

    public BigDecimal getAccountBalance(String accountId, String userId) {
        accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        return calculateBalance(accountId);
    }

    public BigDecimal calculateBalance(String accountId) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("accountId").is(accountId)),
                Aggregation.group()
                        .sum(ConditionalOperators.Cond
                                .when(Criteria.where("type").is("DEBIT"))
                                .thenValueOf("amount").otherwise(0)).as("totalDebit")
                        .sum(ConditionalOperators.Cond
                                .when(Criteria.where("type").is("CREDIT"))
                                .thenValueOf("amount").otherwise(0)).as("totalCredit"),
                Aggregation.project()
                        .andExpression("totalCredit - totalDebit").as("balance")
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(agg, "ledgers", Document.class);

        List<Document> docs = results.getMappedResults();
        if (docs.isEmpty()) return BigDecimal.ZERO;

        Object raw = docs.get(0).get("balance");
        if (raw == null) return BigDecimal.ZERO;
        if (raw instanceof Decimal128 d) return d.bigDecimalValue();
        return new BigDecimal(raw.toString());
    }
}
