package org.graphql.sample.api.repository;

import org.graphql.sample.api.config.GraphQLContext;
import org.graphql.sample.api.entity.Account;
import org.graphql.sample.api.entity.Customer;
import org.graphql.sample.api.tracer.DBQueryTracer;
import org.graphql.sample.api.tracer.DBQueryTracingSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.DbCallback;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
@Repository
public class CustomerRepositoryImpl implements CustomerRepository{
    @Autowired
    MongoTemplate mongoTemplate;
    @Override
    public List<Customer> getCustomersByCustomerIds(Set<Long> customerIds, GraphQLContext context) {
        Query query = new Query(Criteria.where("id").in(customerIds));
        DBQueryTracer tracer =new DBQueryTracer("MongoDB","CustomerRepository",query).startTracing();
        List<Customer> customerResponse = mongoTemplate.find(query,Customer.class);
        ((DBQueryTracingSummary)context.getDbQueryTracingSummary()).addQueryTracer(tracer.stopTracing(customerResponse));
        return customerResponse;

    }
}
