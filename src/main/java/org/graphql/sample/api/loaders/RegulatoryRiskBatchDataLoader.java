package org.graphql.sample.api.loaders;

import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.MappedBatchLoaderWithContext;
import org.graphql.sample.api.annotation.DataLoader;
import org.graphql.sample.api.config.GraphQLContext;
import org.graphql.sample.api.entity.CardHolderAddress;
import org.graphql.sample.api.entity.CardHolderRegulatoryRiskFlagDetail;
import org.graphql.sample.api.repository.RegulatoryRiskFlagDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
@Component
public class RegulatoryRiskBatchDataLoader {
    @Autowired
    private RegulatoryRiskFlagDetailRepository regulatoryRiskFlagDetailRepository;
    @DataLoader(name = "RegulatoryRiskLoader")
    public MappedBatchLoaderWithContext<Long, CardHolderRegulatoryRiskFlagDetail> regulatoryRiskFlagDetailLoader = new MappedBatchLoaderWithContext<Long, CardHolderRegulatoryRiskFlagDetail>() {
        @Override
        public CompletionStage<Map<Long, CardHolderRegulatoryRiskFlagDetail>> load(Set<Long> set, BatchLoaderEnvironment batchLoaderEnvironment) {
            return CompletableFuture.supplyAsync(
                    ()-> {
                        Map<Long,CardHolderRegulatoryRiskFlagDetail> results = new HashMap<>();
                        GraphQLContext context= batchLoaderEnvironment.getContext();
                        List<CardHolderRegulatoryRiskFlagDetail> cardHolderRegulatoryRiskFlagDetailList=regulatoryRiskFlagDetailRepository.getRegulatoryRiskFlagDetails(set,context);
                        if(null == cardHolderRegulatoryRiskFlagDetailList || cardHolderRegulatoryRiskFlagDetailList.size() == 0){
                            return results;
                        }

                        for(CardHolderRegulatoryRiskFlagDetail cardHolderRegulatoryRiskFlagDetail : cardHolderRegulatoryRiskFlagDetailList){
                            results.put(cardHolderRegulatoryRiskFlagDetail.getId(),cardHolderRegulatoryRiskFlagDetail);
                        }
                        return results;
                    });
        }
    };

}
