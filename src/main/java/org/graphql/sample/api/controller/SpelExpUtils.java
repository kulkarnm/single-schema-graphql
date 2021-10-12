package org.graphql.sample.api.controller;

import org.springframework.expression.*;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;

public class SpelExpUtils {
    private static final ParserContext parseContext = new TemplateParserContext("${","}");
    private static final ExpressionParser parser = new SpelExpressionParser();

    private SpelExpUtils(){}
     public static String evalExp(String expression, Object ... contextArgs){
        if(expression==null){
            return expression;
        }else {
            try {
                contextArgs = contextArgs != null && contextArgs.length > 0 ? contextArgs : new Object[10];
                Expression exp = parser.parseExpression(expression, parseContext);
                StandardEvaluationContext context = new StandardEvaluationContext(new SpelExpUtils.Args(contextArgs));
                return (String) exp.getValue(context, String.class);
            }catch(ParseException | EvaluationException exp){
                return expression;
            }
        }
     }

     private static class Args {
        public List<Object> arg ;
        private Args(Object[] contextArgs){
            if(contextArgs != null && contextArgs.length > 0) {
                for(int i=0; i <contextArgs.length;i++){
                    this.arg.add(contextArgs[i]);
                }
            }
        }
     }
}
