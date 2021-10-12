package org.graphql.sample.api.fetchers;

import graphql.schema.DataFetchingEnvironment;
import org.graphql.sample.api.config.GraphQLContext;
import org.graphql.sample.api.entity.CardHolderRegulatoryRiskFlagDetail;
import org.graphql.sample.api.entity.Customer;
import org.graphql.sample.api.repository.RegulatoryRiskFlagDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegulatoryDataFetcher {
    @Autowired
    RegulatoryRiskFlagDetailRepository regulatoryRiskFlagDetailRepository;

    public CardHolderRegulatoryRiskFlagDetail getId(DataFetchingEnvironment environment){
        GraphQLContext context = environment.getContext();
        Customer customer = (Customer) environment.getSource();
        Long regRiskId = customer.getRiskFlagIndicator();
        return regulatoryRiskFlagDetailRepository.getRegulatoryRiskFlagDetail(regRiskId,context);
    }
}
