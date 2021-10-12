package org.graphql.sample.api.repository;

import org.graphql.sample.api.config.GraphQLContext;
import org.graphql.sample.api.entity.CardHolderRegulatoryRiskFlagDetail;
import org.graphql.sample.api.tracer.DBQueryTracer;
import org.graphql.sample.api.tracer.DBQueryTracingSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;
@Repository
public class RegulatoryRiskFlagDetailRepositoryImpl implements RegulatoryRiskFlagDetailRepository{
    @Autowired
    MongoTemplate mongoTemplate;
    @Override
    public CardHolderRegulatoryRiskFlagDetail getRegulatoryRiskFlagDetail(Long id, GraphQLContext context) {
        return (CardHolderRegulatoryRiskFlagDetail)mongoTemplate.findById(id,CardHolderRegulatoryRiskFlagDetail.class);
    }

    @Override
    public List<CardHolderRegulatoryRiskFlagDetail> getRegulatoryRiskFlagDetails(Set<Long> ids, GraphQLContext context) {
        Query query = new Query(Criteria.where("id").in(ids));
        DBQueryTracer tracer =new DBQueryTracer("MongoDB","RegulatoryRiskFlagDetailRepository",query).startTracing();
        List<CardHolderRegulatoryRiskFlagDetail> cardHolderRegulatoryRiskFlagDetailsResponse = mongoTemplate.find(query,CardHolderRegulatoryRiskFlagDetail.class);
        ((DBQueryTracingSummary)context.getDbQueryTracingSummary()).addQueryTracer(tracer.stopTracing(cardHolderRegulatoryRiskFlagDetailsResponse));
        return cardHolderRegulatoryRiskFlagDetailsResponse;
    }

    public CardHolderRegulatoryRiskFlagDetail fallback(Long id,GraphQLContext context,Throwable exp){
        CardHolderRegulatoryRiskFlagDetail fallbackResponse = new CardHolderRegulatoryRiskFlagDetail();
        fallbackResponse.setId(1L);
        fallbackResponse.setIndicator("Fallback");
        fallbackResponse.setUpdateDate(new Date());
        return fallbackResponse ;
    }

    @Override
    public void save(CardHolderRegulatoryRiskFlagDetail cardHolderRegulatoryRiskFlagDetail) {
        mongoTemplate.save(cardHolderRegulatoryRiskFlagDetail);
    }
}
