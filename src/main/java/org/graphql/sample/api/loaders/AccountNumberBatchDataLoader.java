package org.graphql.sample.api.loaders;

import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.MappedBatchLoaderWithContext;
import org.graphql.sample.api.annotation.DataLoader;
import org.graphql.sample.api.config.GraphQLContext;
import org.graphql.sample.api.entity.Account;
import org.graphql.sample.api.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
@Component
public class AccountNumberBatchDataLoader {
    @Autowired
    private AccountRepository accountRepository;
    @DataLoader(name = "AccountNumberLoader")
    public MappedBatchLoaderWithContext<String, Account> accountNumberBatchLoader = new MappedBatchLoaderWithContext<String, Account>() {
        @Override
        public CompletionStage<Map<String, Account>> load(Set<String> set, BatchLoaderEnvironment batchLoaderEnvironment) {
            return CompletableFuture.supplyAsync(
                    ()-> {
                        Map<String,Account> results = new HashMap<>();
                        GraphQLContext context= batchLoaderEnvironment.getContext();
                        List<Account> accountList=accountRepository.getAccountByAccountNumber(set,context);
                        if(null == accountList || accountList.size() == 0){
                            return results;
                        }

                        for(Account account : accountList){
                            results.put(account.getAccountNumber(),account);
                        }
                        return results;
                    });
        };
    };

}
