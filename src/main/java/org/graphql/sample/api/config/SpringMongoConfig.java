package org.graphql.sample.api.config;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class SpringMongoConfig {
    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(new MongoClient("127.0.0.1"),"graphql");
    }
}
