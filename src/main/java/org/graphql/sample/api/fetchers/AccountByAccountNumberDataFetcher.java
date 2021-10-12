package org.graphql.sample.api.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.dataloader.DataLoader;
import org.graphql.sample.api.entity.Account;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
@Component
public class AccountByAccountNumberDataFetcher implements DataFetcher<CompletableFuture<Account>> {

    @Override
    public CompletableFuture<Account> get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
        String accountNumber = dataFetchingEnvironment.getArgument("accountNumber");
        DataLoader<String, Account> accountNumberLoader = dataFetchingEnvironment.getDataLoader("AccountNumberLoader");
        return accountNumberLoader.load(accountNumber);
    }
}
