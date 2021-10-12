package org.graphql.sample.api.entity;

import org.springframework.data.annotation.Id;

import java.util.Date;

public class CardHolderRegulatoryRiskFlagDetail {
    @Id
    private Long id;
    private String indicator;
    private Date updateDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIndicator() {
        return indicator;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
