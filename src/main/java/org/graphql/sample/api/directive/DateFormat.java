package org.graphql.sample.api.directive;

import graphql.schema.*;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.graphql.sample.api.constant.ErrorCodes;
import org.graphql.sample.api.exception.DataException;
import org.graphql.sample.api.utils.CommonUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormat implements SchemaDirectiveWiring {
    public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment){
        GraphQLFieldDefinition field = environment.getElement();
        GraphQLFieldsContainer parentType = environment.getFieldsContainer();
        DataFetcher originalDataFetcher = environment.getCodeRegistry().getDataFetcher(parentType,field);

        DataFetcher dataFetcher = DataFetcherFactories.wrapDataFetcher(
                originalDataFetcher,((dataFetchingEnvironment,value)->{
                 if(value == null){
                     return null;
                 }
                 String format = "yyyy-mm-dd" ;
                 Object argument = CommonUtils.getDirectiveArgument(dataFetchingEnvironment,"dateFormat","format");
                 if(argument != null){
                     format = (String)argument;
                 }
                 Date date;
                 String dateInString;
                 try{
                     if( value instanceof String){
                         date = new SimpleDateFormat().parse((String)value);
                     }else if(value instanceof Date){
                         date = (Date)value;
                     }else {
                         throw new DataException(ErrorCodes.GQ504,"Invalid date type");
                     }
                     dateInString = DateFormatUtils.formatUTC(date,format);
                 }catch(Exception ex){
                     throw new DataException(ErrorCodes.GQ504,"Failed to format Date");
                 }
                 return dateInString;
                }));
        FieldCoordinates coordinates = FieldCoordinates.coordinates(parentType,field);
        environment.getCodeRegistry().dataFetcher(coordinates,dataFetcher);
        return field;
    }
}
