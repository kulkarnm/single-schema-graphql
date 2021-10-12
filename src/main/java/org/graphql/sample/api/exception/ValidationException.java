package org.graphql.sample.api.exception;

import org.graphql.sample.api.constant.ErrorCodes;

public class ValidationException extends ApiException{
    public ValidationException(ErrorCodes errorCodes) {
        super(errorCodes.toString(), errorCodes.getDescription());
    }

    public ValidationException(ErrorCodes errorCodes, String errorMessage) {
        super(errorCodes.toString(), errorMessage);
    }

}
