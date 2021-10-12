package org.graphql.sample.api.fetchers;

import graphql.GraphQLContext;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.dataloader.DataLoader;
import org.graphql.sample.api.entity.Account;
import org.graphql.sample.api.entity.CardHolderAddress;
import org.graphql.sample.api.entity.Customer;
import org.graphql.sample.api.repository.RegulatoryRiskFlagDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
@Component
public class CardHolderAddressByCustomerDataFetcher implements DataFetcher<CompletableFuture<CardHolderAddress>> {

    @Autowired
    private RegulatoryRiskFlagDetailRepository regulatoryRiskFlagDetailRepository;
    @Override
    public CompletableFuture<CardHolderAddress> get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            GraphQLContext context = dataFetchingEnvironment.getContext();
            Customer source = dataFetchingEnvironment.getSource();
            return source.getCardHolderAddress();
        });
    }
}
