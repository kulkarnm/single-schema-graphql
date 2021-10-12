package org.graphql.sample.api.controller;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class Response {
    private HttpStatus status;
    private Map<String,Object> data;

    public Response(HttpStatus status,Map<String,Object> data){
        this.status=status;
        this.data = data;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
