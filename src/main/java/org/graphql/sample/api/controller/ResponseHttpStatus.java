package org.graphql.sample.api.controller;

import org.springframework.http.HttpStatus;

public enum ResponseHttpStatus {
    OK(HttpStatus.OK),
    CREATED(HttpStatus.CREATED),
    ACCEPTED(HttpStatus.ACCEPTED),
    NO_CONTENT(HttpStatus.NO_CONTENT),
    BAD_REQUEST(HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

    private HttpStatus status;

    private ResponseHttpStatus(HttpStatus status){this.status=status;}

    public HttpStatus getStatus() {
        return status;
    }
    public int getCode(){return this.status.value();}
}
