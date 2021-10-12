package org.graphql.sample.api.repository;

import org.graphql.sample.api.config.GraphQLContext;
import org.graphql.sample.api.entity.CardHolderRegulatoryRiskFlagDetail;

import java.util.List;
import java.util.Set;

public interface RegulatoryRiskFlagDetailRepository {
    CardHolderRegulatoryRiskFlagDetail getRegulatoryRiskFlagDetail(Long id, GraphQLContext context);
    List<CardHolderRegulatoryRiskFlagDetail> getRegulatoryRiskFlagDetails(Set<Long> ids, GraphQLContext context);
    void save(CardHolderRegulatoryRiskFlagDetail cardHolderRegulatoryRiskFlagDetail);
}
