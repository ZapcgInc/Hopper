package com.hopper.tests.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import cucumber.api.java.eo.Do;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CancelPolicies
{
    @JsonProperty("currency")
    private String currency;

    @JsonProperty("start")
    private String start;

    @JsonProperty("end")
    private String end;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("nights")
    private int nights;

    @JsonProperty("percent")
    private Float percentage;

    public CancelPolicies(){}

    public String getCurrency()
    {
        return currency;
    }

    public String getStart()
    {
        return start;
    }

    public String getEnd()
    {
        return end;
    }

    public Double getAmount()
    {
        return amount;
    }

    public int getNights()
    {
        return nights;
    }

    public Float getPercentage()
    {
        return percentage;
    }
}
