package com.securetransaction.repository;

import com.securetransaction.model.TokenBlacklist;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TokenBlacklistRepository extends MongoRepository<TokenBlacklist, String> {

    boolean existsByToken(String token);
}
