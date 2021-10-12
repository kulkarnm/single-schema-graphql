package org.graphql.sample.api.loaders;

import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.MappedBatchLoaderWithContext;
import org.graphql.sample.api.annotation.DataLoader;
import org.graphql.sample.api.config.GraphQLContext;
import org.graphql.sample.api.entity.CardHolderAddress;
import org.graphql.sample.api.entity.CardHolderRegulatoryRiskFlagDetail;
import org.graphql.sample.api.entity.Customer;
import org.graphql.sample.api.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
@Component
public class CustomerBatchDataLoader {

    @Autowired
    private CustomerRepository customerRepository;
    @DataLoader(name = "CustomerLoader")
    public MappedBatchLoaderWithContext<Long, Customer> customerBatchLoader = new MappedBatchLoaderWithContext<Long, Customer>() {
        @Override
        public CompletionStage<Map<Long, Customer>> load(Set<Long> set, BatchLoaderEnvironment batchLoaderEnvironment) {
            return CompletableFuture.supplyAsync(
                    ()-> {
                        Map<Long,Customer> results = new HashMap<>();
                        GraphQLContext context= batchLoaderEnvironment.getContext();
                        List<Customer> customerList=customerRepository.getCustomersByCustomerIds(set,context);
                        if(null == customerList || customerList.size() == 0){
                            return results;
                        }

                        for(Customer customer : customerList){
                            Customer response = new Customer();
                            response.setId(customer.getId());
                            response.setName(customer.getName());
                            response.setCustomerNumber(customer.getCustomerNumber());
                            response.setCardHolderAddress(new CardHolderAddress());
                            response.setRiskFlagIndicator(customer.getRiskFlagIndicator());
                            response.setCardHolderRegulatoryRiskFlagDetail(new CardHolderRegulatoryRiskFlagDetail());
                            context.set(
                                    "CUST_DATA"+customer.getId(),customer
                            );
                            results.put(customer.getId(),response);
                        }
                        return results;
                    });
        }
    };

}
