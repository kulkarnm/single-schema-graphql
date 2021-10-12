package org.graphql.sample.api.constant;

public enum ErrorCodes {
    GQ400("GraphQL query validation error"),
    GQ401("GraphQL invalid Syntax"),
    GQ402("GraphQL Operation not supported"),
    GQ403("Access forbidden"),
    GQ404("Data not found"),
    GQ405("Input validation failure"),
    GQ500("Data fetching error"),
    GQ501("DB Fetching Error : Timeout"),
    GQ502("DB Fetching Error"),
    GQ503("Service Unavailable"),
    GQ504("Date Field Error");

    private String description;

    ErrorCodes(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
