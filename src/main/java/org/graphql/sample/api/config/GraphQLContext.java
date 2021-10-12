package org.graphql.sample.api.config;

import org.graphql.sample.api.tracer.DBQueryTracingSummary;

import java.util.HashMap;

public class GraphQLContext {
    private String queryHash;
    private HashMap<String,Object> props;
    private DBQueryTracingSummary dbQueryTracingSummary = new DBQueryTracingSummary();

    public GraphQLContext(String queryHash){
        this.queryHash=queryHash;
    }


    public String getQueryHash() {
        return queryHash;
    }

    public void setQueryHash(String queryHash) {
        this.queryHash = queryHash;
    }

    public HashMap<String, Object> getProps() {
        return props;
    }

    public void setProps(HashMap<String, Object> props) {
        this.props = props;
    }

    public DBQueryTracingSummary getDbQueryTracingSummary() {
        return dbQueryTracingSummary;
    }

    public void setDbQueryTracingSummary(DBQueryTracingSummary dbQueryTracingSummary) {
        this.dbQueryTracingSummary = dbQueryTracingSummary;
    }
    public <T> T get(String name) {
        return (T)this.props.get(name);
    }
    public void set(String name,Object value) {
        this.props.put(name,value);
    }

}
