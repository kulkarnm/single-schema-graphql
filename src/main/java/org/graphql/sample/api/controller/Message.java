package org.graphql.sample.api.controller;

import java.util.UUID;

public class Message {
    private String id;
    private String code;
    private String title;
    private String detail;

    public Message(){}

    Message(String code, String title, String detail) {
        this.id = UUID.randomUUID().toString();
        this.code = code;
        this.title = title;
        this.detail = detail;
    }

    Message(String id, String code, String title, String detail) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.detail = detail;
    }
    public static Message create(String code,String title) {return  new Message(code,title,null);}
    public static Message create(String code,String title,String detail) {return  new Message(code,title,detail);}
    public Message formatTitle(Object ... arguments){
        return new Message(this.code,SpelExpUtils.evalExp(this.title,arguments),this.detail);
    }
    public Message formatDetail(Object ... arguments){
        return new Message(this.code,this.title,SpelExpUtils.evalExp(this.detail,arguments));
    }
    public Message withDetail(String detail) { return new Message(this.code,this.title,detail);}

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", title='" + title + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }
}
