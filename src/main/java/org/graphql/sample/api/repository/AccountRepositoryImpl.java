package org.graphql.sample.api.repository;

import org.graphql.sample.api.config.GraphQLContext;
import org.graphql.sample.api.entity.Account;
import org.graphql.sample.api.tracer.DBQueryTracer;
import org.graphql.sample.api.tracer.DBQueryTracingSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
@Repository
public class AccountRepositoryImpl implements AccountRepository{
    @Autowired
    MongoTemplate mongoTemplate;
    @Override
    public List<Account> getAccountByAccountIds(Set<Long> accountIds, GraphQLContext context) {
        Query query = new Query(Criteria.where("id").in(accountIds));
        DBQueryTracer tracer = new DBQueryTracer("MongoDB","AccountRepository",query).startTracing();
        List<Account> accountResponse = mongoTemplate.find(query,Account.class);
        ((DBQueryTracingSummary)context.getDbQueryTracingSummary()).addQueryTracer(tracer.stopTracing(accountResponse));
        return accountResponse;
    }

    @Override
    public List<Account> getAccountByAccountNumber(Set<String> accountNumbers, GraphQLContext context) {
        Query query = new Query(Criteria.where("accountNumber").in(accountNumbers));
        DBQueryTracer tracer = new DBQueryTracer("MongoDB","AccountRepository",query).startTracing();
        List<Account> accountResponse = mongoTemplate.find(query,Account.class);
        ((DBQueryTracingSummary)context.getDbQueryTracingSummary()).addQueryTracer(tracer.stopTracing(accountResponse));
        return accountResponse;
    }

    @Override
    public void save(Account account) {
        mongoTemplate.save(account);
    }
}
