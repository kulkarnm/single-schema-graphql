package org.graphql.sample.api.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.graphql.sample.api.config.GraphQLContext;
import org.graphql.sample.api.entity.CardHolderRegulatoryRiskFlagDetail;
import org.graphql.sample.api.entity.Customer;
import org.graphql.sample.api.repository.RegulatoryRiskFlagDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
@Component
public class RegulatoryRiskDataFetcher implements DataFetcher<CompletableFuture<CardHolderRegulatoryRiskFlagDetail>> {

    @Autowired
    private RegulatoryRiskFlagDetailRepository regulatoryRiskFlagDetailRepository;
    @Override
    public CompletableFuture<CardHolderRegulatoryRiskFlagDetail> get(DataFetchingEnvironment dataFetchingEnvironment) {
        return CompletableFuture.supplyAsync(() -> {
            GraphQLContext context = dataFetchingEnvironment.getContext();
            Customer source = dataFetchingEnvironment.getSource();
            return regulatoryRiskFlagDetailRepository.getRegulatoryRiskFlagDetail(Long.valueOf(source.getRiskFlagIndicator()),context);
        });
    }
}
