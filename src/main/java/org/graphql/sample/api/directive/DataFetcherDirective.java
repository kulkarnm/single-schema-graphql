package org.graphql.sample.api.directive;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLFieldsContainer;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
@PropertySource("datafetcher-mapping.properties")
@Component
public class DataFetcherDirective implements SchemaDirectiveWiring {
    Properties properties;
    @Autowired
    ApplicationContext context;

    public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment){
        loadProperties();
        String dataFetcherValue = (String)environment.getDirective().getArgument("datafetcher").getValue();
        GraphQLFieldDefinition field = environment.getElement();
        GraphQLFieldsContainer parentType = environment.getFieldsContainer();
        String dataFetcherClassName = properties.getProperty(dataFetcherValue);
        DataFetcher object=null;
        try{
            Class<?> clazz = Class.forName(dataFetcherClassName);
            object =(DataFetcher)context.getBean(clazz);
        }catch(Exception ex){
            System.out.println("Failed to load datafetcher for " + dataFetcherValue);
        }
        environment.getCodeRegistry().dataFetcher(parentType,field,object);
        return field;
    }
    private void loadProperties() {
        try{
            InputStream inputStream = DataFetcherDirective.class.getClassLoader().getResourceAsStream("datafetcher-mapping.properties");
            properties = new Properties();
            if(inputStream == null){
                System.out.println("Failed to load datafetcher-mapping.properties");
                return ;
            }
            properties.load(inputStream);
        }catch(IOException ex){
            System.out.println("Failed to load datafetcher config " + ex.getMessage());
        }
    }
}
