package com.hopper.tests.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Price
{
    @JsonProperty("type")
    private String type;

    @JsonProperty("value")
    private String value;

    @JsonProperty("currency")
    private String currency;

    public Price(){}

    public String getType()
    {
        return type;
    }

    public String getValue()
    {
        return value;
    }

    public String getCurrency()
    {
        return currency;
    }
}
