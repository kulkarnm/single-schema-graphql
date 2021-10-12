package org.graphql.sample.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoTimeoutException;
import graphql.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.dataloader.*;
import org.graphql.sample.api.config.GraphQLContext;
import org.graphql.sample.api.constant.ErrorCodes;
import org.graphql.sample.api.controller.*;
import org.graphql.sample.api.exception.ApiException;
import org.graphql.sample.api.exception.ValidationException;
import org.graphql.sample.api.utils.CommonUtils;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;

@Service
public class GraphQLService implements ApplicationContextAware {
    private GraphQL graphQL;
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
     private Reflections reflections;

    private Map<String,Object> registeredLoaders;
    @Autowired
    public GraphQLService(GraphQL graphQL) {
        this.graphQL = graphQL;
        this.registeredLoaders = new HashMap<>();
    }

    public void setApplicationContext(ApplicationContext applicationContext){
        this.applicationContext = applicationContext;
    }
    private void scanForLoaders(){
        Set<Field> dataLoaders =  reflections.getFieldsAnnotatedWith(org.graphql.sample.api.annotation.DataLoader.class);
        try {
            for (Field field : dataLoaders) {
                Class<?> declaringClass = field.getDeclaringClass();
                registeredLoaders.put(field.getAnnotation(org.graphql.sample.api.annotation.DataLoader.class).name(), field.get(applicationContext.getBean(declaringClass)));
            }
        }catch (IllegalAccessException ex){
            ex.printStackTrace();
        }
    }
    public WebResponse resolve(String queryHash, String query, Map<String, Object> variables) throws ExecutionException, InterruptedException, TimeoutException {
        if (null == query) {
            return WebResponse.assemble()
                    .addErrorMessage(CommonUtils.getErrorMessage(ErrorCodes.GQ400, "Null Query Received"))
                    .build(ResponseHttpStatus.BAD_REQUEST);
        }
        GraphQLContext context = new GraphQLContext(queryHash/*, customerByCustomerIdDataFetcher, regulatoryDataFetcher*/);

        ExecutionInput executionInput = ExecutionInput
                .newExecutionInput()
                .query(query)
                .variables(variables)
                .context(context)
                .dataLoaderRegistry(getDataLoaders(context))
                .build();

        GraphQLContext finalContext = context;
        CompletableFuture<WebResponse> promise = graphQL.executeAsync(executionInput).thenApply(result -> getResponse(finalContext, result));
        context = (GraphQLContext) executionInput.getContext();
        System.out.println(context.getDbQueryTracingSummary().toString());
        return promise.get(5L, TimeUnit.MINUTES);
    }

    private WebResponse getResponse(GraphQLContext context, ExecutionResult result) {
        ResponseAssembler assembler = WebResponse.assemble();
        if (result.getExtensions() != null) {
            Map<Object, Object> ext = result.getExtensions();
            updateDuration(ext);
            assembler.addMetadata(new MetaData().put("tracing", ext.get("tracing")));
            assembler.addMetadata(new MetaData().put("dataloader", ext.get("dataloader")));
            assembler.addMetadata(new MetaData().put("dbstats", context.getDbQueryTracingSummary()));
        }
        Map<String, Object> data = result.getData();
        ObjectMapper mapper = new ObjectMapper();
        try {
            String tracing = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(result.getExtensions().get("tracing"));
            String dataLoader = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(result.getExtensions().get("dataloader"));
            System.out.println("Query DB tracing -" +tracing +
                    "\n data loader - " + dataLoader +
                    "\nDb stats - " + context.getDbQueryTracingSummary());
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
        if (result.getErrors().size() > 0) {
            System.out.println("Error encountered : " + result.getErrors().toString());
            ResponseHttpStatus responseHttpStatus = getHttpResponseCode(result.getErrors());
            assembler.addErrorMessages(getErrorMessages(result.getErrors()));
            return assembler.build(responseHttpStatus, data);
        } else {
            return assembler.build(ResponseHttpStatus.OK, data);
        }
    }

    private ResponseHttpStatus getHttpResponseCode(List<GraphQLError> graphQLErrors) {
        for (GraphQLError error : graphQLErrors) {
            if (error.getErrorType() == ErrorType.ValidationError) {
                return ResponseHttpStatus.BAD_REQUEST;
            } else if (error.getErrorType() == ErrorType.InvalidSyntax) {
                return ResponseHttpStatus.BAD_REQUEST;
            } else if (error.getErrorType() == ErrorType.OperationNotSupported) {
                return ResponseHttpStatus.BAD_REQUEST;
            }
            if (error instanceof ExceptionWhileDataFetching) {
                ExceptionWhileDataFetching dataFetchingException = (ExceptionWhileDataFetching) error;
                if (dataFetchingException.getException() instanceof ValidationException) {
                    return ResponseHttpStatus.BAD_REQUEST;
                }
            }
        }
        return ResponseHttpStatus.INTERNAL_SERVER_ERROR;
    }

    private List<Message> getErrorMessages(List<GraphQLError> graphQLErrors) {
        List<Message> messages = new ArrayList<>();
        for (GraphQLError error : graphQLErrors) {
            Message msg = Message.create(getErrorCodes(error), getErrorTitle(error), getErrorDetail(error));
            messages.add(msg);
        }
        return messages;
    }

    private String getErrorDetail(GraphQLError error) {
        List<Object> paths = error.getPath();
        if (paths == null) {
            return null;
        }
        List<String> list = new ArrayList<>();
        for (Object obj : paths) {
            list.add(obj.toString());
        }
        return String.join("/", list);
    }

    private String getErrorCodes(GraphQLError error) {
        if (error.getErrorType() == ErrorType.ValidationError) {
            return ErrorCodes.GQ400.toString();
        } else if (error.getErrorType() == ErrorType.InvalidSyntax) {
            return ErrorCodes.GQ401.toString();
        } else if (error.getErrorType() == ErrorType.OperationNotSupported) {
            return ErrorCodes.GQ402.toString();
        } else {
            Throwable throwable = getRootException(error);
            if (throwable != null && throwable instanceof ApiException) {
                ApiException apiException = (ApiException) throwable;
                return apiException.getErrorCode().toString();
            } else if (throwable instanceof MongoTimeoutException) {
                return ErrorCodes.GQ501.toString();
            } else {
                return ErrorCodes.GQ500.toString();
            }
        }
    }

    private String getErrorTitle(GraphQLError error) {
        String messageDetail = error.getMessage();
        Throwable throwable = getRootException(error);
        if (throwable != null && throwable instanceof ApiException) {
            ApiException apiException = (ApiException) throwable;
            messageDetail = apiException.getMessage();
        } else if (throwable instanceof MongoTimeoutException) {
            messageDetail = ErrorCodes.GQ501.getDescription();
        } else {
            messageDetail = "UNEXPECTED INTERNAL ERROR";
        }
        return messageDetail;
    }

    private Throwable getRootException(GraphQLError error) {
        if (error instanceof ExceptionWhileDataFetching) {
            ExceptionWhileDataFetching exceptionWhileDataFetching = (ExceptionWhileDataFetching) error;
            Throwable throwable = exceptionWhileDataFetching.getException();
            if (throwable instanceof CompletionException) {
                CompletionException completionException = (CompletionException) throwable;
                throwable = completionException.getCause();
            }
            if (throwable instanceof ValidationException) {
                return throwable;
            }
            return ExceptionUtils.getRootCause(exceptionWhileDataFetching.getException());
        }
        return null;
    }

    private DataLoaderRegistry getDataLoaders(GraphQLContext context) {
        DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();
        BatchLoaderContextProvider contextProvider = () -> context;
        DataLoaderOptions loaderOptions = DataLoaderOptions.newOptions()
                .setBatchLoaderContextProvider(contextProvider);

        scanForLoaders();
        for(Map.Entry<String,Object> entry : registeredLoaders.entrySet()) {
            registerDataLoader(dataLoaderRegistry, loaderOptions, (MappedBatchLoaderWithContext)entry.getValue(), entry.getKey());
        }
        return dataLoaderRegistry;
    }

    private <K, V> void registerDataLoader(DataLoaderRegistry dataLoaderRegistry, DataLoaderOptions dataLoaderOptions, MappedBatchLoaderWithContext<K, V> batchLoader, String name) {
        DataLoader<K, V> dataLoader = DataLoader.newMappedDataLoader(batchLoader, dataLoaderOptions);
        dataLoaderRegistry.register(name, dataLoader);
    }

    private boolean updateDuration(Map<Object, Object> map) {
        for (Map.Entry<Object, Object> item : map.entrySet()) {
            Object key = item.getKey();
            Object value = item.getValue();
            if (key.equals("duration")) {
                item.setValue((Long) value / 1000000);
                if ((Long) value == 0) {
                    return true;
                }
            } else if (value instanceof LinkedHashMap) {
                updateDuration((Map) value);
            } else if (value instanceof ArrayList) {
                ArrayList list = (ArrayList) value;
                ArrayList listModified = new ArrayList();
                for (Object val : list) {
                    if (val instanceof LinkedHashMap) {
                        boolean remove = updateDuration((Map) val);
                        if (!remove) {
                            listModified.add(val);
                        }
                    } else {
                        listModified.add(val);
                    }
                }
                item.setValue(listModified);
            }
        }
        return false;
    }
}
