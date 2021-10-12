package org.graphql.sample.api.utils;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLDirective;
import org.apache.commons.lang3.StringUtils;
import org.graphql.sample.api.constant.ErrorCodes;
import org.graphql.sample.api.controller.Message;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class CommonUtils {
    public static boolean validateMaxLength(String number){
        try{
            long validateNumber = Long.parseLong(number);
            if(validateNumber > 0 && validateNumber <=Long.MAX_VALUE){
                return true;
            }
        }catch(NumberFormatException ex){
            System.out.println("Invalid Number");
            return false;
        }
        return true;
    }

    public static String generateNumberSigns(Integer n, String initialString){
        String s = initialString;
        for(int i=0;i < n;i++){
            s+="0";
        }
        return s;
    }

    public static Double convertToDouble(Long initialValue,Integer decimalDigit){
        Double convertedDoubleValue =null;
        String numberSigns = generateNumberSigns(decimalDigit,"");
        DecimalFormat fmt = new DecimalFormat("0." + numberSigns + 0);
        fmt.setNegativePrefix("-");
        if(null != initialValue && null != decimalDigit){
            Double longToDouble = initialValue.doubleValue();
            int exp = (int)Math.pow(10,decimalDigit);
            convertedDoubleValue = Double.valueOf(longToDouble/Double.parseDouble(String.valueOf(exp)));
        }
        return convertedDoubleValue;
    }

    public static boolean validateCustomerId(String customerId){
        if(!StringUtils.isNotBlank(customerId)) {
            return false;
        }
        if(!StringUtils.isNumeric(customerId)){
            return false;
        }
        return validateMaxLength(customerId);
    }

    public static Set<String> getDataGroups(
        Map<String, List<String>> map, Set<String> selectionFields,String excludePrefix){
        Set<String> dataGroups = new HashSet<>();
        Pattern excludePattern = null;
        if(null != excludePrefix){
            excludePattern=Pattern.compile("^" + excludePrefix);
        }

        List<String> defaultGroups = map.get("");
        if(null != defaultGroups){
            dataGroups.addAll(defaultGroups);
        }
        for(String fieldName : selectionFields) {
            if(excludePrefix != null){
                String modifiedFieldName = excludePattern.matcher(fieldName).replaceFirst("");
                if(modifiedFieldName.equals(fieldName) == false){
                    fieldName=modifiedFieldName;
                }else {
                    continue;
                }
            }
            List<String> groups = map.get(fieldName);
            if(null !=groups){
                dataGroups.addAll(groups);
            }
        }
        return dataGroups;
    }

    public static Message getErrorMessage(ErrorCodes code,String title){
        return Message.create(code.toString(),title);
    }
    public static Object getDirectiveArgument(DataFetchingEnvironment env,String directiveName,String argumentName){
        List<GraphQLDirective> graphQLDirectives = env.getQueryDirectives().getImmediateDirective(directiveName);
        if(graphQLDirectives.size()== 0){
            return null;
        }else {
            return graphQLDirectives.get(0).getArgument(argumentName).getValue();
        }

    }
}
