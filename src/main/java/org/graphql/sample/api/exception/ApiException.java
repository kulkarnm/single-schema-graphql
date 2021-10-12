package org.graphql.sample.api.exception;

public class ApiException extends RuntimeException {
    private String errorCode;

    public ApiException(String toString,String description){
        super(description);
        setErrorCode(toString);
    }
    public ApiException(String toString,String description,Throwable ex){
        super(description,ex);
        setErrorCode(toString);
    }
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
