package org.graphql.sample.api.tracer;

import java.util.ArrayList;
import java.util.List;

public class DBQueryTracingSummary {
    private long duration =0;
    private List<DBQueryTracer> queries = new ArrayList<>();

    public void addQueryTracer(DBQueryTracer dbQueryTracer){
        this.queries.add(dbQueryTracer);
        this.duration +=dbQueryTracer.getDuration();
    }

    public long getDuration() {
        return duration;
    }

    public List<DBQueryTracer> getQueries() {
        return queries;
    }

    public String toString() {
        String queryTracer ="";
        for(DBQueryTracer tracer:queries){
            queryTracer +=tracer.toString() + " || ";
        }
        return "DBQueryTracingSummary {" +
                "duration= " + duration +
                ", queries " + queries + "}" ;
    }
}
