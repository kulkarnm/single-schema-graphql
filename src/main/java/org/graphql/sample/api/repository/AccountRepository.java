package org.graphql.sample.api.repository;

import org.graphql.sample.api.config.GraphQLContext;
import org.graphql.sample.api.entity.Account;

import java.util.List;
import java.util.Set;

public interface AccountRepository {
    List<Account> getAccountByAccountIds(Set<Long> accountIds, GraphQLContext context);
    List<Account> getAccountByAccountNumber(Set<String> accountNumbers, GraphQLContext context);
    void save(Account account);
}
