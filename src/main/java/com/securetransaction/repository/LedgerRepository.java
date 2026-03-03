package com.securetransaction.repository;

import com.securetransaction.model.LedgerEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Ledger entries are immutable – we never update or delete them.
 * Balance calculation is done via an aggregation in AccountService,not through a repository query.
 */
public interface LedgerRepository extends MongoRepository<LedgerEntry, String> {
}
