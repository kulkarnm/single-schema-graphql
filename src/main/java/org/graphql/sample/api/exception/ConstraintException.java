package org.graphql.sample.api.exception;

public class ConstraintException extends RuntimeException{
    public ConstraintException(String fieldName,String argumentName,String errorMessage){
        super(fieldName + ":" + argumentName + " " + errorMessage);
    }
}
