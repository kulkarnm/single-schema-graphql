package org.graphql.sample.api.exception;

import org.graphql.sample.api.constant.ErrorCodes;

public class DataException extends ApiException {
    public DataException(ErrorCodes errorCodes) {
        super(errorCodes.toString(), errorCodes.getDescription());
    }

    public DataException(ErrorCodes errorCodes, String errorMessage) {
        super(errorCodes.toString(), errorMessage);
    }
    public DataException(ErrorCodes errorCodes, String errorMessage,Throwable ex) {
        super(errorCodes.toString(), errorMessage,ex);
    }

}