package org.graphql.sample.api.controller;

public interface ResponseSuccessAssembler {
    WebResponse build(Object object);
    WebResponse build(ResponseHttpStatus status,Object object);
}
