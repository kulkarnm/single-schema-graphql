package org.graphql.sample.api.controller;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class ResponseAssembler implements ResponseSuccessAssembler,ResponseErrorAssembler {
    private WebResponse responseToAssemble;

    protected ResponseAssembler(WebResponse responseToAssemble){
        this.responseToAssemble=responseToAssemble;
    }
    public ResponseAssembler addMetadata(MetaData meta){
        if(this.responseToAssemble.meta == null){
            this.responseToAssemble.meta=meta;
            return this;
        }else{
            this.responseToAssemble.meta.merge(meta);
            return this;
        }
    }

    public ResponseAssembler setHeader(String key,String value){
        this.responseToAssemble.getMultiValueHeaders().add(key,value);
        return this;
    }
    public WebResponse build(Object data){
        this.responseToAssemble.data = data;
        this.responseToAssemble.httpStatus = HttpStatus.OK ;
        return this.build();
    }
    public WebResponse build(ResponseHttpStatus status,Object payload){
        this.responseToAssemble.httpStatus = status.getStatus();
        this.responseToAssemble.data =payload;
        return this.build();
    }

    public ResponseErrorAssembler addErrorMessage(Message message){
        if(message== null){
            return this;
        }else {
            if(this.responseToAssemble.errors == null){
                this.responseToAssemble.errors = new ArrayList<>();
            }
            this.responseToAssemble.errors.add(message);
            return this;
        }
    }

    public ResponseErrorAssembler addErrorMessages(Collection<Message> messages){
        if(messages== null){
            return this;
        }else {
            Iterator iter = messages.iterator();
            while (iter.hasNext()){
                Message m = (Message)iter.next();
                if(m !=null){
                    this.addErrorMessage(m) ;
                }
            }
        }
        return this;
    }

    public WebResponse build(ResponseHttpStatus errorStatus){
        this.responseToAssemble.httpStatus = errorStatus.getStatus();
        return this.build();
    }
    static ResponseAssembler assemble(WebResponse responseToAssemble){
        return new ResponseAssembler(responseToAssemble);
    }

    WebResponse build() {
        if(this.responseToAssemble.httpStatus == null){
            throw new IllegalStateException("cannot buuld response without http status code");
        }else if (this.responseToAssemble.data != null ||!this.responseToAssemble.errors.isEmpty() || this.responseToAssemble.httpStatus == HttpStatus.NO_CONTENT || this.responseToAssemble.meta !=null && !this.responseToAssemble.meta.isEmpty()){
            return this.responseToAssemble;
        }else {
            throw new IllegalStateException("cannot build empty response");
        }
    }
}
