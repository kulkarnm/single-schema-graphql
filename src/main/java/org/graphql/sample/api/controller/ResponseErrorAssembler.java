package org.graphql.sample.api.controller;

import java.util.Collection;

public interface ResponseErrorAssembler {
    ResponseErrorAssembler addErrorMessage(Message msg);
    ResponseErrorAssembler addErrorMessages(Collection<Message> msgs);
    WebResponse build(ResponseHttpStatus status);
}
