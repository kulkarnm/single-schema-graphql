package org.graphql.sample.api.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

public class Account {
    @Transient
    public static final String SEQUENCE_NAME = "account_sequence" ;

    @Id
    private Long id;
    private String accountNumber;
    private int availableFunds;
    Customer customer ;

    public Account(Long id, String accountNumber, int availableFunds, Customer customer) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.availableFunds = availableFunds;
        this.customer = customer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getAvailableFunds() {
        return availableFunds;
    }

    public void setAvailableFunds(int availableFunds) {
        this.availableFunds = availableFunds;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
