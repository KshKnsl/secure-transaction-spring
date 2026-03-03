package com.securetransaction.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * @EnableMongoAuditing activates @CreatedDate and @LastModifiedDate
 * MongoTransactionManager enables @Transactional in TransactionService.
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig 
{
    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) 
    {
        return new MongoTransactionManager(dbFactory);
    }
}
