package org.graphql.sample.api.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class WebResponse {

    private MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
    HttpStatus httpStatus;
    MetaData meta;
    Object data;
    List<Message> errors;

    private WebResponse() {}

    public static ResponseAssembler assemble(){
        return ResponseAssembler.assemble(new WebResponse());
    }
    public static WebResponse empty(ResponseHttpStatus status){
        if(status == null){
            throw new IllegalArgumentException("Status cannot be null");
        }else {
            WebResponse response = new WebResponse();
            response.httpStatus= status.getStatus();
            return response;
        }
    }

    public static WebResponse empty(HttpStatus status){
        if(status == null){
            throw new IllegalArgumentException("Status cannot be null");
        }else {
            WebResponse response = new WebResponse();
            response.httpStatus= status;
            return response;
        }
    }

    public MetaData getMeta() { return this.meta;}
    public void setMeta(MetaData meta){this.meta = meta ;}
    public <T> T getData() throws ClassCastException {
        return (T)this.data;
    }
    public <T> T getData(Class T) throws ClassCastException {
        ObjectMapper mapper = new ObjectMapper();
        List<T> dataList = new ArrayList<>();
        if(this.data == null){
            return null;
        }else if ( this.data instanceof List && !((List)this.data).isEmpty()) {
            Iterator iterator = ((List)this.data).iterator();
            while(iterator.hasNext()){
                Object obj = iterator.next();
                dataList.add(mapper.convertValue(obj, new TypeReference<T>(){}));
            }
            return (T)dataList ;
        }else {
            return  mapper.convertValue(this.data, new TypeReference<T>() {
            });
        }
    }

    public void setData(Object data){
        this.data = data;
    }

    public List<Message> getErrors() {
        if(this.errors == null){
            return null;
        }else {
            return Collections.unmodifiableList(this.errors);
        }
    }

    public void setErrors(List<Message> errors){
        this.errors = errors;
    }
    @JsonIgnore
    public MultiValueMap<String,String> getMultiValueHeaders() {
        return this.headers == null ?null :CollectionUtils.unmodifiableMultiValueMap(this.headers);
    }
    public void setHeaders(MultiValueMap<String,String> headers){
        this.headers = headers;
    }
    @JsonIgnore
    public HttpStatus getHttpStatus() {return this.httpStatus;}

    @JsonIgnore
    public void setHttpStatus(HttpStatus httpStatus){this.httpStatus = httpStatus;}

    @Override
    public String toString() {
        return "WebResponse{" +
                "headers=" + headers +
                ", httpStatus=" + httpStatus +
                ", meta=" + meta +
                ", data=" + data +
                ", errors=" + errors +
                '}';
    }
}
