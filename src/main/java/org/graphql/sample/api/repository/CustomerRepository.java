package org.graphql.sample.api.repository;

import org.graphql.sample.api.config.GraphQLContext;
import org.graphql.sample.api.entity.Customer;

import java.util.List;
import java.util.Set;

public interface CustomerRepository {
    List<Customer> getCustomersByCustomerIds(Set<Long> customerIds, GraphQLContext context);
}
