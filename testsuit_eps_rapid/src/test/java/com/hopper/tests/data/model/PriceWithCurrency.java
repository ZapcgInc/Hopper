package com.hopper.tests.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hopper.tests.data.model.response.Price;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceWithCurrency
{
    @JsonProperty("billable_currency")
    private Price billable;

    @JsonProperty("request_currency")
    private Price request;

    public PriceWithCurrency(){}

    public Price getBillable()
    {
        return billable;
    }

    public Price getRequest()
    {
        return request;
    }
}
