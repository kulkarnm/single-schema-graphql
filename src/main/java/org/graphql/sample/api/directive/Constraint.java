package org.graphql.sample.api.directive;

import graphql.schema.*;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import org.graphql.sample.api.constant.ErrorCodes;
import org.graphql.sample.api.exception.ConstraintException;
import org.graphql.sample.api.exception.ValidationException;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Constraint implements SchemaDirectiveWiring {
    public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
        GraphQLFieldDefinition field = environment.getFieldDefinition();
        GraphQLFieldsContainer parentType = environment.getFieldsContainer();

        GraphQLFieldDefinition argument = environment.getElement();
        GraphQLType inputType = argument.getType();
        if(inputType instanceof GraphQLNonNull){
            inputType=((GraphQLNonNull)inputType).getWrappedType();
        }else if(inputType instanceof GraphQLList){
            inputType=((GraphQLList)inputType).getWrappedType();
            if(inputType instanceof GraphQLNonNull) {
                inputType = ((GraphQLNonNull) inputType).getWrappedType();
            }
        }
        String argumentName = argument.getName();
        GraphQLArgument regexArg = environment.getDirective().getArgument("pattern");
        GraphQLArgument minArg = environment.getDirective().getArgument("min");
        GraphQLArgument maxArg = environment.getDirective().getArgument("max");
        GraphQLArgument minItemsArg = environment.getDirective().getArgument("minItems");
        GraphQLArgument maxItemsArg = environment.getDirective().getArgument("maxItems");

        Pattern pattern =regexArg.getValue() !=null?Pattern.compile((String)regexArg.getValue()) :null ;
        Integer min = minArg.getValue() !=null ? (int)minArg.getValue() : null;
        Integer max = maxArg.getValue() !=null ? (int)maxArg.getValue() : null;
        Integer minItems = minItemsArg.getValue() !=null ? (int)minItemsArg.getValue() : null;
        Integer maxItems = maxItemsArg.getValue() !=null ? (int)maxItemsArg.getValue() : null;

        if(pattern == null && min == null && max == null){
            throw new ConstraintException(field.getName(),argumentName,"Must have either pattern or min/max for constraint directive");
        }

        if(argument.getType() instanceof GraphQLList ){
            if(minItems == null || maxItems == null) {
                throw new ConstraintException(field.getName(),argumentName,"Must have both minItems and maxItems for constraint directive");
            }
        }
        if(inputType instanceof GraphQLScalarType) {
            if (((GraphQLScalarType) inputType).getName().equals("Int")) {
                if (min == null && max != null) {
                    throw new ConstraintException(field.getName(), argumentName, "constraint directive requires min/max here");
                }
                if (pattern != null) {
                    throw new ConstraintException(field.getName(), argumentName, "constraint directive does not support pattern here");
                }
            } else if (((GraphQLScalarType) inputType).getName().equals("String")) {
                if (pattern == null) {
                    throw new ConstraintException(field.getName(), argumentName, "constraint directive requires pattern here");
                }
                if (min != null || max != null) {
                    throw new ConstraintException(field.getName(), argumentName, "constraint directive does not support min/max here");
                }
            } else {
                throw new ConstraintException(field.getName(), argumentName, "constraint directive is not supported here");
            }
        }else{
            throw new ConstraintException(field.getName(),argumentName,"constraint directive is not supported here");
        }

        DataFetcher originalDataFetcher = environment.getCodeRegistry().getDataFetcher(parentType,field);
        DataFetcher constraintDataFetcher = dataFetchingEnvironment -> {
            Object argValue = dataFetchingEnvironment.getField();
            if(argValue ==  null){
                return originalDataFetcher.get(dataFetchingEnvironment);
            }else if(argValue instanceof String){
                checkString(pattern,(String)argValue);
            }else if( argValue instanceof Integer) {
                checkInteger(min,max,(Integer)argValue);
            }else if (argValue instanceof ArrayList){
                ArrayList valueList = (ArrayList)argValue ;
                checkInteger(minItems,maxItems,valueList.size());
                if(!valueList.isEmpty()) {
                    if(valueList.get(0) instanceof String){
                        for(Object value : valueList){
                            checkString(pattern,(String)value);
                        }
                    }else if(valueList.get(0) instanceof Integer){
                        for(Object value : valueList){
                            checkInteger(min,max,(Integer)value);
                        }
                    }
                }
            }
            return originalDataFetcher.get(dataFetchingEnvironment);
        };
        //now change the field definition to have the new authorising data fetcher
        environment.getCodeRegistry().dataFetcher(parentType,field,constraintDataFetcher);
        return environment.getElement();
    }

    public GraphQLArgument onArgument(SchemaDirectiveWiringEnvironment<GraphQLArgument> environment){
        GraphQLFieldDefinition field = environment.getFieldDefinition();
        GraphQLFieldsContainer parentType = environment.getFieldsContainer();
        GraphQLArgument argument = environment.getElement();
        GraphQLType inputType = argument.getType();
        if(inputType instanceof GraphQLNonNull){
            inputType = ((GraphQLNonNull)inputType).getWrappedType();
        }else if(inputType instanceof GraphQLList){
            inputType = ((GraphQLList)inputType).getWrappedType();
            if(inputType instanceof GraphQLNonNull){
                inputType=((GraphQLNonNull)inputType).getWrappedType();
            }
        }
        String argumentName = argument.getName();
        GraphQLArgument regexArg = environment.getDirective().getArgument("pattern");
        GraphQLArgument minArg = environment.getDirective().getArgument("min");
        GraphQLArgument maxArg = environment.getDirective().getArgument("max");
        GraphQLArgument minItemsArg = environment.getDirective().getArgument("minItems");
        GraphQLArgument maxItemsArg = environment.getDirective().getArgument("maxItems");
        Pattern pattern = regexArg.getValue()!=null? Pattern.compile((String)regexArg.getValue()) : null;
        Integer min = minArg.getValue() !=null ? (int)minArg.getValue() : null ;
        Integer max = maxArg.getValue() !=null ? (int)maxArg.getValue() : null ;
        Integer minItems = minItemsArg.getValue() !=null ? (int)minItemsArg.getValue() : null ;
        Integer maxItems = maxItemsArg.getValue() !=null ? (int)maxItemsArg.getValue() : null ;

        if(pattern == null && min == null && max == null){
            throw new ConstraintException(field.getName(),argumentName,"Must have either pattern or min/max for constraint directive");
        }
        if(argument.getType() instanceof GraphQLList){
            if(minItems== null || maxItems == null){
                throw new ConstraintException(field.getName(),argumentName,"Must have both minItems and maxItems for constraint directive");
            }
        }
        if(inputType instanceof GraphQLScalarType) {
            if (((GraphQLScalarType) inputType).getName().equals("Int")) {
                if (min == null && max != null) {
                    throw new ConstraintException(field.getName(), argumentName, "constraint directive requires min/max here");
                }
                if (pattern != null) {
                    throw new ConstraintException(field.getName(), argumentName, "constraint directive does not support pattern here");
                }
            } else if (((GraphQLScalarType) inputType).getName().equals("String")) {
                if (pattern == null) {
                    throw new ConstraintException(field.getName(), argumentName, "constraint directive requires pattern here");
                }
                if (min != null || max != null) {
                    throw new ConstraintException(field.getName(), argumentName, "constraint directive does not support min/max here");
                }
            } else {
                throw new ConstraintException(field.getName(), argumentName, "constraint directive is not supported here");
            }
        }else {
            throw new ConstraintException(field.getName(), argumentName, "constraint directive is not supported here");
        }
        DataFetcher originalDataFetcher = environment.getCodeRegistry().getDataFetcher(parentType,field);
        DataFetcher authDataFetcher = dataFetchingEnvironment -> {
            Object argValue = dataFetchingEnvironment.getArguments().get(argumentName);
            if(argValue ==  null){
                return originalDataFetcher.get(dataFetchingEnvironment);
            }else if(argValue instanceof String){
                checkString(pattern,(String)argValue);
            }else if( argValue instanceof Integer) {
                checkInteger(min,max,(Integer)argValue);
            }else if (argValue instanceof ArrayList){
                ArrayList valueList = (ArrayList)argValue ;
                checkInteger(minItems,maxItems,valueList.size());
                if(!valueList.isEmpty()) {
                    if(valueList.get(0) instanceof String){
                        for(Object value : valueList){
                            checkString(pattern,(String)value);
                        }
                    }else if(valueList.get(0) instanceof Integer){
                        for(Object value : valueList){
                            checkInteger(min,max,(Integer)value);
                        }
                    }
                }
            }
            return originalDataFetcher.get(dataFetchingEnvironment);
        };
        //now change the field definition to have the new authorising data fetcher
        environment.getCodeRegistry().dataFetcher(parentType,field,authDataFetcher);
        return environment.getElement();
    }

    private void checkString(Pattern pattern, String value){
        if(pattern != null){
            Matcher matcher = pattern.matcher(value);
            if(!matcher.matches()){
                throw new ValidationException(ErrorCodes.GQ405,"input validation failure: does not match pattern" + pattern.pattern());
            }
        }
    }

    private void checkInteger(Integer min,Integer max,Integer value){
        if(min !=null && value <min){
            throw new ValidationException(ErrorCodes.GQ405,"Input validation failure:must be greater than " + min);
        }
        if(max !=null && value >min){
            throw new ValidationException(ErrorCodes.GQ405,"Input validation failure:must be less than " + max);
        }

    }
}
