package org.graphql.sample.api.controller;

import org.graphql.sample.api.config.AuthChecker;
import org.graphql.sample.api.entity.Account;
import org.graphql.sample.api.entity.CardHolderRegulatoryRiskFlagDetail;
import org.graphql.sample.api.entity.Customer;
import org.graphql.sample.api.repository.AccountRepository;
import org.graphql.sample.api.repository.RegulatoryRiskFlagDetailRepository;
import org.graphql.sample.api.service.GraphQLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
public class GraphQLController {
    @Autowired
    GraphQLService graphQLService;

    @Autowired
    AuthChecker authChecker;

    @Autowired
    RegulatoryRiskFlagDetailRepository regulatoryRiskFlagDetailRepository;

    @Autowired
    AccountRepository accountRepository;

    @PostMapping(value="/graphql")
    public WebResponse query(@RequestBody Map<String,Object> body, HttpServletRequest request) throws TimeoutException {
        try{
            Object object = (Object)body.get("variables");
            String queryHash = authChecker.isAuthGranted(request,(String)body.get("query"));
            return graphQLService.resolve(queryHash,(String)body.get("query"),(Map<String,Object>)body.get("variables"));
        }catch(ExecutionException ex){
            System.out.println(Arrays.stream(ex.getStackTrace()).toString());
        }catch(InterruptedException ex){
            System.out.println(Arrays.stream(ex.getStackTrace()).toString());
        }
        return null;
    }

    private void save(){
        CardHolderRegulatoryRiskFlagDetail regulatoryRiskFlagDetail = new CardHolderRegulatoryRiskFlagDetail();
        regulatoryRiskFlagDetail.setId(new Long(1));
        regulatoryRiskFlagDetail.setIndicator("123");
        regulatoryRiskFlagDetail.setUpdateDate(new Date());
        regulatoryRiskFlagDetailRepository.save(regulatoryRiskFlagDetail);
    }

    @PostMapping("/account")
    public ResponseEntity<Object> add(){
        try {
            Long id = new Long(3);
            Customer customer = new Customer();
            customer.setCardHolderAddress(null);
            customer.setCustomerNumber("2");
            customer.setId(new Long(2));
            customer.setName("Customer 2");
            customer.setRiskFlagIndicator(1);

            Account account = new Account(id, "A300000000", 100, customer);
            accountRepository.save(account);
            CardHolderRegulatoryRiskFlagDetail regulatoryRisk = new CardHolderRegulatoryRiskFlagDetail();
            regulatoryRisk.setId(new Long(1));
            regulatoryRisk.setIndicator("123");
            regulatoryRisk.setUpdateDate(new Date());
            regulatoryRiskFlagDetailRepository.save(regulatoryRisk);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception ex){
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}