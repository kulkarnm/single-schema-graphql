package org.graphql.sample.api.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.dataloader.DataLoader;
import org.graphql.sample.api.entity.Account;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class AccountByAccountIdDataFetcher implements DataFetcher<CompletableFuture<Account>> {

    @Override
    public CompletableFuture<Account> get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
        Long accountId = Long.parseLong(dataFetchingEnvironment.getArgument("accountId"));
        DataLoader<Long,Account> accountDataLoader = dataFetchingEnvironment.getDataLoader("AccountLoader");
        return accountDataLoader.load(accountId);
    }
}
