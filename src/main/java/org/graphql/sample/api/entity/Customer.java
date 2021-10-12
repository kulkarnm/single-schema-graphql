package org.graphql.sample.api.entity;

public class Customer {
    private Long id;
    private String name;
    private String customerNumber;
    private CardHolderAddress cardHolderAddress;
    private long riskFlagIndicator ;
    private CardHolderRegulatoryRiskFlagDetail cardHolderRegulatoryRiskFlagDetail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CardHolderAddress getCardHolderAddress() {
        return cardHolderAddress;
    }

    public void setCardHolderAddress(CardHolderAddress cardHolderAddress) {
        this.cardHolderAddress = cardHolderAddress;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public long getRiskFlagIndicator() {
        return riskFlagIndicator;
    }

    public void setRiskFlagIndicator(long riskFlagIndicator) {
        this.riskFlagIndicator = riskFlagIndicator;
    }

    public CardHolderRegulatoryRiskFlagDetail getCardHolderRegulatoryRiskFlagDetail() {
        return cardHolderRegulatoryRiskFlagDetail;
    }

    public void setCardHolderRegulatoryRiskFlagDetail(CardHolderRegulatoryRiskFlagDetail cardHolderRegulatoryRiskFlagDetail) {
        this.cardHolderRegulatoryRiskFlagDetail = cardHolderRegulatoryRiskFlagDetail;
    }
}
