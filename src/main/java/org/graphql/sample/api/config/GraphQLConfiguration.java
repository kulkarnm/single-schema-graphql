package org.graphql.sample.api.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import graphql.ExecutionInput;
import graphql.GraphQL;
import graphql.execution.instrumentation.ChainedInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentation;
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentationOptions;
import graphql.execution.instrumentation.tracing.TracingInstrumentation;
import graphql.execution.preparsed.PreparsedDocumentEntry;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.graphql.sample.api.directive.Constraint;
import org.graphql.sample.api.directive.DataFetcherDirective;
import org.graphql.sample.api.directive.DateFormat;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Configuration
@EnableCaching
public class GraphQLConfiguration {
    private static final String[] SCHEMA_FILES =
            new String[]{
                    "schema/root.graphql",
                    "schema/account.graphql",
                    "schema/customer.graphql",
                    "schema/regulatoryRisk.graphql",
                    "schema/directives.graphql"
            };
   private GraphQL graphQL;
    @Value("${GRAPHQL.TRACING.ENABLE:false}")
    private Boolean isGraphQLTracingEnabled;

    @Autowired
    private DataFetcherDirective dataFetcherDirective;
    private Cache<String, PreparsedDocumentEntry> preparsedQueryCache;

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }

    @Bean
    public Reflections reflections() {
        return new Reflections("org.graphql.sample.api",new FieldAnnotationsScanner());
    }
    @PostConstruct
    public void init() throws IOException {
        preparsedQueryCache = Caffeine.newBuilder().maximumSize(1000).build();
        RuntimeWiring runtimeWiring = buildWiring();
        TypeDefinitionRegistry typeDefinitionRegistry = loadSchema();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

        GraphQL.Builder graphQLBuilder = GraphQL.newGraphQL(graphQLSchema).preparsedDocumentProvider(this::getCachedQuery);

        List<Instrumentation> chainedList = new ArrayList<>();
        if (isGraphQLTracingEnabled) {
            chainedList.add(new TracingInstrumentation(TracingInstrumentation.Options.newOptions().includeTrivialDataFetchers(false)));
        }

        DataLoaderDispatcherInstrumentationOptions options =
                DataLoaderDispatcherInstrumentationOptions.newOptions().includeStatistics(true);
        DataLoaderDispatcherInstrumentation dispatcherInstrumentation = new DataLoaderDispatcherInstrumentation(options);
        chainedList.add(dispatcherInstrumentation);

        ChainedInstrumentation chainedInstrumentation = new ChainedInstrumentation(chainedList);
        this.graphQL = graphQLBuilder.instrumentation(chainedInstrumentation).build();
    }

    private PreparsedDocumentEntry getCachedQuery(ExecutionInput executionInput, Function<ExecutionInput, PreparsedDocumentEntry> computeFunction) {
        GraphQLContext context = (GraphQLContext) executionInput.getContext();
        PreparsedDocumentEntry cachedValue = preparsedQueryCache.getIfPresent(context.getQueryHash());
        if(null != cachedValue){
            return cachedValue;
        }else {
            return preparsedQueryCache.get(context.getQueryHash(), (key) -> computeFunction.apply(executionInput));
        }
    }

    private TypeDefinitionRegistry loadSchema() throws IOException {
        TypeDefinitionRegistry typeDefinitionRegistry = new TypeDefinitionRegistry();
        SchemaParser schemaParser = new SchemaParser();
        for (String schemaFileName : SCHEMA_FILES) {
            URL url = Resources.getResource(schemaFileName);
            String sdl = Resources.toString(url, Charsets.UTF_8);
            typeDefinitionRegistry.merge(schemaParser.parse(sdl));
        }
        return typeDefinitionRegistry;
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .directive("dateFormat", new DateFormat())
                .directive("constraint", new Constraint())
                .directive("fetcher",dataFetcherDirective)
                .build();
    }
}
