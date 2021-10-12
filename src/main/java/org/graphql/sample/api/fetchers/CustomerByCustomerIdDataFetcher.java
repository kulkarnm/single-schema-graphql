package org.graphql.sample.api.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.dataloader.DataLoader;
import org.graphql.sample.api.entity.Account;
import org.graphql.sample.api.entity.Customer;
import org.graphql.sample.api.repository.RegulatoryRiskFlagDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
@Component
public class CustomerByCustomerIdDataFetcher implements DataFetcher<CompletableFuture<Customer>> {

    @Autowired
    private RegulatoryRiskFlagDetailRepository regulatoryRiskFlagDetailRepository;
    @Override
    public CompletableFuture<Customer> get(DataFetchingEnvironment dataFetchingEnvironment) {
        return CompletableFuture.supplyAsync(() -> {
            Long customerId = Long.parseLong(dataFetchingEnvironment.getArgument("customerId"));
            DataLoader<Long,Customer> customerDataLoader = dataFetchingEnvironment.getDataLoader("CustomerLoader");
            CompletableFuture<Customer> customerCompletableFuture = customerDataLoader.load(customerId);
            try{
                return customerCompletableFuture.get();
            }catch (InterruptedException | ExecutionException ex){
                ex.printStackTrace();
            }
            return null;
        });
    }
}
