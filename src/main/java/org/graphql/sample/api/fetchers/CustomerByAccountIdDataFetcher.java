package org.graphql.sample.api.fetchers;

import graphql.GraphQLContext;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.graphql.sample.api.entity.Account;
import org.graphql.sample.api.entity.CardHolderAddress;
import org.graphql.sample.api.entity.Customer;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
@Component
public class CustomerByAccountIdDataFetcher implements DataFetcher<CompletableFuture<Customer>> {

    @Override
    public CompletableFuture<Customer> get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            Account account = dataFetchingEnvironment.getSource();
            return account.getCustomer();
        });
    }
}
